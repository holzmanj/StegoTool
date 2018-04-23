/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stegotool;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author jesse
 */
public class BPCSTechnique implements StegoTechnique {
    private final double COMPLEXITY_THRESHOLD = 0.5;
    
    public BPCSTechnique() {
    }

    /**
     * Converts a full sized buffered image to 2d array of ImageBlock
     * objects representing each 8x8 section of the image.
     * @param img BufferedImage greater that 8x8
     * @return 2d array of ImageBlock objects
     */
    private ImageBlock[][] imageToBlocks(BufferedImage img) {
        int xBlocks = Math.floorDiv(img.getWidth(), 8);
        int yBlocks = Math.floorDiv(img.getHeight(), 8);
        ImageBlock[][] imgBlocks = new ImageBlock[xBlocks][yBlocks];
        
        for(int x = 0; x < xBlocks; x++) {
            for(int y = 0; y < yBlocks; y++) {
                imgBlocks[x][y] = new ImageBlock(
                        img.getSubimage(x * 8, y * 8, 8, 8));
            }
        }
        
        return imgBlocks;
    }
    
    /**
     * Reconstructs all ImageBlocks and pastes them onto original image.
     * @param imgBlocks 2d array of ImageBlocks
     * @param originalImg Original input image, needed to preserve right and
     * bottom edges of the image.
     */
    private void blocksToImage(ImageBlock[][] imgBlocks, BufferedImage originalImg) {
        Graphics g = originalImg.getGraphics();
        
        for(int x = 0; x < imgBlocks.length; x++) {
            for(int y = 0; y < imgBlocks[0].length; y++) {
                g.drawImage(imgBlocks[x][y].getImage(), x * 8, y * 8, null);
            }
        }
    }
    
    /**
     * Calculates the complexity value of a given 8x8 bit plane.
     * @param bitPlane 8x8 bit plane as an array of 8 bytes
     * @return Double value from 0.0 to 1.0 indicating bit plane complexity
     */
    private double calculateComplexity(byte[] bitPlane) {
        int sum = 0;
        // count horizontal bit flips
        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 7; col++) {
                sum += ((bitPlane[row] >> col) ^ (bitPlane[row] >> (col + 1))) & 1;
            }
        }
        // count vertical bit flips
        for(int col = 0; col < 8; col++) {
            for(int row = 0; row < 7; row++) {
                sum += ((bitPlane[row] >> col) ^ (bitPlane[row + 1] >> col)) & 1;
            }
        }
        return (double) sum / 112.0;
    }
    
    /**
     * Converts a Pure Binary Code (normal) image to the Canonical Grey
     * Code format for BPCS operations.
     * @param pbcImg Image in PBC format
     * @return Image in CGC format
     */
    public BufferedImage PBCToCGC(BufferedImage pbcImg) {
        BufferedImage cgcImg = new BufferedImage(pbcImg.getWidth(),
                pbcImg.getHeight(), pbcImg.getType());
        int b0, b1;
        // copy first column of pixels from PBC to CGC
        for(int y = 0; y < pbcImg.getHeight(); y++) {
            cgcImg.setRGB(0, y, pbcImg.getRGB(0, y));
        }
        // get the rest of the pixel columns through XOR operations
        for(int x = 1; x < pbcImg.getWidth(); x++) {
            for(int y = 0; y < pbcImg.getHeight(); y++) {
                for(int c = 0; c < pbcImg.getColorModel().getNumColorComponents(); c++) {
                    b0 = pbcImg.getRGB(x - 1, y);
                    b1 = pbcImg.getRGB(x, y);
                    cgcImg.setRGB(x, y, b0 ^ b1);
                }
            }
        }
        return cgcImg;
    }
    
    /**
     * Converts a Canonical Grey Code image back to Pure Binary Code.
     * @param cgcImg Image in CGC format
     * @return Image in PBC format
     */
    public BufferedImage CGCToPBC(BufferedImage cgcImg) {
        BufferedImage pbcImg = new BufferedImage(cgcImg.getWidth(),
                cgcImg.getHeight(), cgcImg.getType());
        int g, b;
        // copy first column of pixels from PBC to CGC
        for(int y = 0; y < cgcImg.getHeight(); y++) {
            pbcImg.setRGB(0, y, cgcImg.getRGB(0, y));
        }
        // get the rest of the pixel columns through XOR operations
        for(int x = 1; x < cgcImg.getWidth(); x++) {
            for(int y = 0; y < cgcImg.getHeight(); y++) {
                for(int c = 0; c < cgcImg.getColorModel().getNumColorComponents(); c++) {
                    g = cgcImg.getRGB(x, y);
                    b = pbcImg.getRGB(x - 1, y);
                    pbcImg.setRGB(x, y, g ^ b);
                }
            }
        }
        return pbcImg;
    }
    
    private byte[] conjugatePlane(byte[] bitPlane) {
        if(bitPlane.length != 8) return null;
        byte[] output = new byte[8];
        
        for(int i = 0; i < 8; i++) {
            if(i % 2 == 0)
                output[i] = (byte) (bitPlane[i] ^ 0b10101010);
            else
                output[i] = (byte) (bitPlane[i] ^ 0b01010101);
        }
        return output;
    }
    
    @Override
    public int getImageCapacity(BufferedImage img) {
        if(img == null) return 0;
        
        ImageBlock[][] imgBlocks = imageToBlocks(img);
        int colorChannels = img.getColorModel().getNumColorComponents();
        int eligiblePlanes = 0;
        double complexity;
        
        for(int x = 0; x < imgBlocks.length; x++) {
            for(int y = 0; y < imgBlocks[0].length; y++) {
                for(int c = 0; c < colorChannels; c++) {
                    for(int bit = 0; bit < 8; bit++) {
                        // bit plane is eligible if its complexity meets threshold
                        complexity = calculateComplexity(
                                imgBlocks[x][y].getBitPlane(c, bit));
                        if(complexity >= COMPLEXITY_THRESHOLD) {
                            eligiblePlanes++;
                        }
                    }
                }
            }
        }
        return eligiblePlanes * 8;
    }

    @Override
    public BufferedImage insertFile(File messageFile, BufferedImage imgInput)
            throws IOException {
        if(imgInput == null) {
            System.err.println("ABORT: Input image is null.");
            return null;
        } else
        if(messageFile == null) {
            System.err.println("ABORT: Message file is null.");
            return null;
        } else
        if(getImageCapacity(imgInput) < messageFile.length()) {
            System.err.println("ABORT: Message file is too large for image.");
            return null;
        }
        
        ImageBlock[][] imgBlocks = imageToBlocks(imgInput);
        FileInputStream stream = new FileInputStream(messageFile);
        int colorChannels = imgInput.getColorModel().getNumColorComponents();
        double complexity;
        byte[] plane = new byte[8];
        int readCount;
        
        outermost_loop:
        for(int x = 0; x < imgBlocks.length; x++) {
            for(int y = 0; y < imgBlocks[0].length; y++) {
                for(int c = 0; c < colorChannels; c++) {
                    for(int bit = 0; bit < 8; bit++) {
                        complexity = calculateComplexity(
                                imgBlocks[x][y].getBitPlane(c, bit));
                        if(complexity >= COMPLEXITY_THRESHOLD) {
                            readCount = stream.read(plane);
                            
                            if(calculateComplexity(plane) < COMPLEXITY_THRESHOLD)
                                plane = conjugatePlane(plane);
                            
                            switch (readCount) {
                                case 8:     // full 8 bytes were read from file
                                    imgBlocks[x][y].replaceBitPlane(c, bit, plane);
                                    break;
                                case -1:    // no bytes were read, file is exhausted
                                    break outermost_loop;
                                default:    // some bytes were read, file is exhausted
                                    imgBlocks[x][y].replaceBitPlane(c, bit, plane);
                                    break outermost_loop;
                            }
                        }
                    }
                }
            }
        }
        
        blocksToImage(imgBlocks, imgInput);
        return imgInput;
    }

    @Override
    public void extractFile(BufferedImage img, File outputFile) throws FileNotFoundException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
