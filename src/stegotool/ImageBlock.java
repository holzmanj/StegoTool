/*
 * ImageBlock is an object used by the BPCS technique to store 8x8 subimages as
 * a set of bitplanes for reading and manipulating individual planes.
 * Bitplanes are stored as an array of 8 bytes, where each byte represents a row
 * of bits
 */
package stegotool;

import java.awt.image.BufferedImage;

/**
 *
 * @author jesse
 */
public class ImageBlock {
    byte[][] bitPlanes;
    
    public ImageBlock(BufferedImage subimage) {
        
    }
    
}
