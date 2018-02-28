package stegotool;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author jesse
 */
public interface StegoTechnique {
    
    /**
     * Calculates an image's potential storage capacity.
     * @param img The vessel image to analyze.
     * @return The number of encodable bytes in the image.
     */
    int getImageCapacity(BufferedImage img);
    
    /**
     * Embeds a file within a vessel image.
     * @param messageFile The file to be hidden inside the image.
     * @param vesselImage The cover image which will hold the hidden file.
     * @return The vessel image that has been manipulated to contain the file.
     * Returns null on failure.
     */
    BufferedImage insertFile(File messageFile, BufferedImage vesselImage);
    
    /**
     * Extracts a hidden file from a vessel image.
     * @param vesselImage An image which contains a hidden file embedded within it.
     * @return The file that is extracted from the given image.
     * Returns null on failure.
     */
    File extractFile(BufferedImage vesselImage);
    
}
