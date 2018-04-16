package stegotool;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Divides an image up into sections and creates an LSB thread for each
 * image section.
 * @author jesse
 */
public class LSBMultithreader implements StegoTechnique {
    final int BITS_PER_BYTE = 2;
    final int THREAD_COUNT;
    
    public LSBMultithreader(int threads) {
        THREAD_COUNT = threads;
    }

    @Override
    public int getImageCapacity(BufferedImage img) {
        int colorChannels = img.getRaster().getNumDataElements();
        int width = img.getWidth();
        int height = img.getHeight();
        
        return (width * height * colorChannels * BITS_PER_BYTE) / 8;
    }

    @Override
    public BufferedImage insertFile(File messageFile, BufferedImage imgInput) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void extractFile(BufferedImage img, File outputFile) throws FileNotFoundException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

class LSBInsertThread implements Callable<BufferedImage> {
    LSBTechnique t;
    BufferedImage imgInput, imgOutput;
    InputFileBitStream bitStream;
    
    public LSBInsertThread(BufferedImage img, InputFileBitStream stream) {
        t = new LSBTechnique();
        imgInput = img;
        bitStream = stream;
    }

    @Override
    public BufferedImage call() {
        try {
            imgOutput = t.insertRawData(bitStream, imgInput);
        } catch (IOException ex) {
            Logger.getLogger(LSBInsertThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imgOutput;
    }
    
}
