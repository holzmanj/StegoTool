package stegotool;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
     * @param imgInput The cover image which will hold the hidden file.
     * @return The vessel image that has been manipulated to contain the file.
     * Returns null on failure.
     * @throws java.io.IOException
     */
    BufferedImage insertFile(File messageFile, BufferedImage imgInput) throws IOException;
    
    /**
     * Extracts a hidden file from a vessel image.
     * @param img An image which contains a hidden file embedded within it.
     * @param outputFile The file that is extracted from the given image.
     * @throws java.io.FileNotFoundException
     */
    void extractFile(BufferedImage img, File outputFile) throws FileNotFoundException, IOException;
    
}
