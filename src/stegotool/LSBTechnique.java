package stegotool;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
        
        int b = 0;
        Color pixel;
        int pixelRGB[] = {0, 0, 0};
        for(int x = 0; x < imgInput.getWidth(); x++) {
            for(int y = 0; y < imgInput.getHeight(); y++) {
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
        
        return imgOutput;
    }

    @Override
    public File extractFile(BufferedImage vesselImage) {
        return null;
    }
    
}
