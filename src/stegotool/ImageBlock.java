package stegotool;

import java.awt.image.BufferedImage;

/**
 * ImageBlock is an object used by the BPCS technique to store 8x8
 * subimages as a set of bitplanes for reading and manipulating
 * individual planes.  Bitplanes are stored as an array of 8 bytes, where
 * each byte represents a row of bits.
 * @author jesse
 */
public class ImageBlock {
    byte[][][] bitPlanes;   // indices are ordered [color][bitlevel][row]
    
    /**
     * Constructor takes 8x8 subimage and stores it as an array of bit
     * planes.
     * @param img 8x8 subimage.
     * @throws RuntimeException if image is null, wrong dimensions, or contains
     * transparency.
     */
    public ImageBlock(BufferedImage img) throws RuntimeException {
        if(img == null) 
            throw new RuntimeException("Image is null.");
        else if(img.getWidth() != 8 || img.getHeight() != 8)
            throw new RuntimeException("Image is not 8x8.");
        else if(img.getColorModel().hasAlpha())
            throw new RuntimeException("Image has transparency.");
        
        // Initialize bitPlanes array
        int numColorChannels = img.getColorModel().getNumColorComponents();
        switch (numColorChannels) {
            case 3:
                bitPlanes = new byte[3][8][8];
                break;
            case 1:
                bitPlanes = new byte[1][8][8];
                break;
            default:
                throw new RuntimeException("Invalid image color format.");
        }
        
        // Convert subimage to bit planes
        byte b;
        int c;
        for(int color = 0; color < numColorChannels; color++) {
            for(int bitLevel = 0; bitLevel < 8; bitLevel++) {
                for(int row = 0; row < 8; row++) {
                    b = 0;
                    for(int col = 0; col < 8; col++) {
                        b = (byte) (b << 1);
                        c = img.getRGB(col, row);
                        
                        b |= (c >> (bitLevel + (8 * color))) & 1;
                    }
                    bitPlanes[color][bitLevel][row] = b;
                }
            }
        }
    }
    
    /**
     * Converts the bit planes back into a BufferedImage.
     * @return 8x8 image
     */
    public BufferedImage getImage() {
        BufferedImage img;
        int mask, b, c;
        // initialize output image to 8x8 black square
        if(bitPlanes.length == 3)
            img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
        else
            img = new BufferedImage(8, 8, BufferedImage.TYPE_BYTE_GRAY);

        // iterate through bit planes and reconstruct image
        for(int color = 0; color < bitPlanes.length; color++) {
            for(int bitLevel = 0; bitLevel < 8; bitLevel++) {
                mask = 1 << bitLevel;
                for(int row = 0; row < 8; row++) {
                    for(int col = 0; col < 8; col++) {
                        c = img.getRGB(col, row);
                        b = bitPlanes[color][bitLevel][row];
                        c |= ((b << ((8 * color) + bitLevel)) >> 7 - col)       // shift bit to appropriate index in RGB int
                                & (mask << 8 * color);                          // mask specific bit to RGB int
                        img.setRGB(col, row, c);
                    }
                }
            }
        }
        return img;
    }
}
