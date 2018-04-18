package stegotool;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    LSBTechnique t;
    
    public LSBMultithreader(int threads) {
        THREAD_COUNT = threads;
        t = new LSBTechnique();
    }
    
    /**
     * Splits input image into a number of segments equal to the number
     * of threads.
     * @param img Input vessel image
     * @param segmentHeight Height of each image segment.
     * @return Array of image segments.
     */
    private BufferedImage[] splitImage(BufferedImage img, int segmentHeight) {
        BufferedImage[] imgSegments = new BufferedImage[THREAD_COUNT];

        for(int i = 0; i < THREAD_COUNT - 1; i++) {
            imgSegments[i] = img.getSubimage(0, i * segmentHeight,
                    img.getWidth(), segmentHeight);
        }
        // Last segment's height may be different size than segmentHeight
        imgSegments[THREAD_COUNT - 1] = img.getSubimage(0,
                segmentHeight * (THREAD_COUNT - 1), img.getWidth(), 
                img.getHeight() - (segmentHeight * (THREAD_COUNT - 1)));
        
        return imgSegments;
    }
    
    private BufferedImage constructImage(BufferedImage[] imgSegments) {
        int y = 0;
        BufferedImage imgOutput;
        Graphics g;
        // Calculate height of output
        for(BufferedImage seg : imgSegments) {
            y += seg.getHeight();
        }
        
        imgOutput = new BufferedImage(imgSegments[0].getWidth(), y,
                imgSegments[0].getType());
        
        g = imgOutput.getGraphics();
        y = 0;
        // Combine segments
        for (BufferedImage seg : imgSegments) {
            g.drawImage(seg, 0, y, null);
            y += seg.getHeight();
        }
        
        return imgOutput;
    }
    
    /**
     * Splits input file into blocks in order to be stored in each image
     * segment.
     * @param f Input message file
     * @param blockSize Number of bytes per block
     * @return Array of file blocks
     */
    private File[] splitFile(File f, int blockSize, int reservedBytes) 
            throws FileNotFoundException, IOException {
        File fileBlocks[] = new File[THREAD_COUNT];
        FileInputStream is = new FileInputStream(f);
        FileOutputStream os;
        
        // Size of first block is reduced to account for reserved bytes
        fileBlocks[0] = new File("lsb_block0.tmp");
        os = new FileOutputStream(fileBlocks[0]);
        for(int i = 0; i < blockSize - reservedBytes; i++) {
            os.write(is.read());
        }
        
        // Separate the remaining blocks
        for(int blk = 1; blk < THREAD_COUNT; blk++) {
            fileBlocks[blk] = new File("lsb_block" + blk + ".tmp");
            os = new FileOutputStream(fileBlocks[blk]);
            for(int i = 0; i < blockSize; i++) {
                os.write(is.read());
            }
        }
        
        return fileBlocks;
    }

    @Override
    public int getImageCapacity(BufferedImage img) {
        return t.getImageCapacity(img);
    }

    @Override
    public BufferedImage insertFile(File messageFile, BufferedImage imgInput)
            throws IOException {
        BufferedImage imgOutput = new BufferedImage(imgInput.getWidth(), 
                imgInput.getHeight(), imgInput.getType());
        
        int segmentHeight = Math.floorDiv(imgInput.getHeight(), THREAD_COUNT);        

        BufferedImage imgSegments[] = splitImage(imgInput, segmentHeight);

        int fileBlockSize = (segmentHeight * imgInput.getWidth()
                * imgInput.getColorModel().getNumColorComponents() * 8) / BITS_PER_BYTE;
        
        File fileSegments[] = splitFile(messageFile, fileBlockSize,
                t.getNumBytesForCapacity(imgInput));
        
        
        return imgOutput;
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
