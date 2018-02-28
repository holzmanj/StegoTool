package stegotool;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author jesse
 */
public class LSBTechnique implements StegoTechnique {
    final int BITS_PER_BYTE = 2;    // TODO replace contant with read value from config file.

    @Override
    public int getImageCapacity(BufferedImage img) {
        int colorChannels = img.getRaster().getNumDataElements();
        int width = img.getWidth();
        int height = img.getHeight();
        
        return (width * height * colorChannels * BITS_PER_BYTE) / 8;
    }

    @Override
    public BufferedImage insertFile(File messageFile, BufferedImage vesselImage) {
        return null;
    }

    @Override
    public File extractFile(BufferedImage vesselImage) {
        return null;
    }
    
}
