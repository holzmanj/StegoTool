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
    byte[][] bitPlanes;
    
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
                bitPlanes = new byte[3][8];
                break;
            case 1:
                bitPlanes = new byte[1][8];
                break;
            default:
                throw new RuntimeException("Invalid image color format.");
        }
        
        // Convert subimage to bit planes
        byte b, mask;
        int c;
        for(int color = 0; color < numColorChannels; color++) {
            for(int bitLevel = 0; bitLevel < 8; bitLevel++) {
                mask = (byte) (0b01 << bitLevel);
                for(int row = 0; row < 8; row++) {
                    b = 0;
                    for(int col = 0; col < 8; col++) {
                        c = img.getRGB(col, row);
                        
                        b |= (c >> (8 * color)) & mask;
                        b = (byte) (b << 1);
                    }
                    bitPlanes[color][row] = b;
                }
            }
        }
    }
}
