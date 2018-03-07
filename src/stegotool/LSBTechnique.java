package stegotool;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jesse
 */
public class LSBTechnique implements StegoTechnique {
    private final int BITS_PER_BYTE = 2;    // TODO replace constant with read value from config file.

    private final int mask;
    
    public LSBTechnique() {
        // initialize mask
        int mask = 0;
        for(int i = 0; i < BITS_PER_BYTE; i++) {
            mask = (mask << 1) | 0b1;
        }
        this.mask = mask;
    }
    
    /**
     * Calculates the number of bytes needed to hold the number of encodable
     * bytes in the image.
     * @param img Image to analyze.
     * @return Number of bytes.
     */
    private int getNumBytesForCapacity(BufferedImage img) {
        int capacity = getImageCapacity(img);
        
        // calculate number of bytes needed
        int byteCount = 1;
        while(Math.pow(2.0, 8.0 * byteCount) < capacity) {
            byteCount++;
        }
        
        return byteCount;
    }
    
    /**
     * Seperates size of file into byte array where the number of bytes is the 
     * minimum needed to hold the full capacity of the image.
     * @param file Message file.
     * @param img Vessel image.
     * @return Byte array containing file size.
     */
    private int[] getReservedBytesForFileSize(File file, BufferedImage img) {
        int fileSize = (int) file.length();
        int fileSizeBytes[];

        fileSizeBytes = new int[getNumBytesForCapacity(img)];
        
        // split file size into seperate bytes
        int shiftCount;
        for(int i = 0; i < fileSizeBytes.length; i++) {
            shiftCount = 8 * (fileSizeBytes.length - (i + 1));
            fileSizeBytes[i] = (fileSize >> shiftCount) & 0xFF;
        }
        
        return fileSizeBytes;
    }
    
    @Override
    public int getImageCapacity(BufferedImage img) {
        int colorChannels = img.getRaster().getNumDataElements();
        int width = img.getWidth();
        int height = img.getHeight();
        
        return (width * height * colorChannels * BITS_PER_BYTE) / 8;
    }

    @Override
    public BufferedImage insertFile(File messageFile, BufferedImage imgInput)
            throws IOException {
        BufferedImage imgOutput;
        InputFileBitStream bitStream;
        
        if(imgInput == null) {
            System.err.println("ABORT: Input image is null.");
            return null;
        } else
        if(messageFile == null) {
            System.err.println("ABORT: Message file is null.");
            return null;
        } else
        if(getImageCapacity(imgInput) < messageFile.length()) {
            System.err.println("ABORT: Message file is null.");
            return null;
        }
        
        imgOutput = new BufferedImage(
                imgInput.getWidth(), 
                imgInput.getHeight(),
                imgInput.getType());
 
        bitStream = new InputFileBitStream(messageFile, BITS_PER_BYTE);
        
        bitStream.setReservedBytes(
                getReservedBytesForFileSize(messageFile, imgInput));
        
        Color pixel;
        int pixelRGB[] = {0, 0, 0};
        for(int x = 0; x < imgInput.getWidth(); x++) {
            for(int y = 0; y < imgInput.getHeight(); y++) {
                if(bitStream.isDoneReading()) {
                    imgOutput.setRGB(x, y, imgInput.getRGB(x, y));
                } else {
                    // get pixel values from input image
                    pixel = new Color(imgInput.getRGB(x, y));
                
                    // insert file data into pixel values
                    pixelRGB[0] = (pixel.getRed()   & ~mask) | bitStream.read();
                    pixelRGB[1] = (pixel.getGreen() & ~mask) | bitStream.read();
                    pixelRGB[2] = (pixel.getBlue()  & ~mask) | bitStream.read();
                
                    // set new pixel value in output image
                    pixel = new Color(pixelRGB[0], pixelRGB[1], pixelRGB[2]);
                    imgOutput.setRGB(x, y, pixel.getRGB());
                }
            }
        }
        return imgOutput;
    }

    @Override
    public void extractFile(BufferedImage vesselImage, File outputFile) 
            throws FileNotFoundException, IOException {
        
        OutputFileBitStream bitStream = new OutputFileBitStream(BITS_PER_BYTE, outputFile);
        
        // reserve bytes for file size
        bitStream.reserveBytes(getNumBytesForCapacity(vesselImage));
        
        Color pixel;
        for(int x = 0; x < vesselImage.getWidth(); x++) {
            for(int y = 0; y < vesselImage.getHeight(); y++) {
                // get pixel values from image
                pixel = new Color(vesselImage.getRGB(x, y));
                
                // get least significant bits and write them to file
                bitStream.write(pixel.getRed()   & mask);
                bitStream.write(pixel.getGreen() & mask);
                bitStream.write(pixel.getBlue()  & mask);
                
                if(bitStream.isDoneWriting()) {
                    bitStream.closeFile();
                    return;
                }
            }
        }
        bitStream.closeFile();
    }
    
}
