package stegotool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author jesse
 */
public class OutputFileBitStream {
    private final OutputStream fileStream;
    private final int bitMask;
    private final int BITS_PER_CLUSTER;
    private int currentByte;
    private int bitsShifted;
    private int[] reservedBytes = {};
    private int reservedBytesWritten;
    private int fileSize;
    private int fileBytesWritten = 0;
    private boolean doneWriting;
    
    public OutputFileBitStream(int bitsPerCluster, File file)
            throws FileNotFoundException {
        BITS_PER_CLUSTER = bitsPerCluster;
        currentByte = 0;
        bitsShifted = 0;
        
        fileStream = new FileOutputStream(file);
        fileBytesWritten = 0;
        
        doneWriting = false;
        
        // initialize mask
        int mask = 0;
        for(int i = 0; i < BITS_PER_CLUSTER; i++) {
            mask = (mask << 1) | 0b1;
        }
        bitMask = mask;
    }
    
    /**
     * Sets the amount of reserved bytes to store separately and not write to file.
     * @param count Number of bytes to reserve.
     */
    public void reserveBytes(int count) {
        reservedBytes = new int[count];
        reservedBytesWritten = 0;
    }
    
    /**
     * Writes x bits to the file, where x is the bits per cluster that was set
     * in the constructor.
     * @param bitCluster Integer containg bit cluster (final x bits).
     * @throws IOException 
     */
    public void write(int bitCluster) throws IOException {
        if(bitsShifted >= 8) {
            if(reservedBytesWritten < reservedBytes.length) {
                reservedBytes[reservedBytesWritten] = currentByte;
                reservedBytesWritten++;
                
                // if last reserved byte is written, determine file size
                if(reservedBytesWritten == reservedBytes.length) {
                    fileSize = 0;
                    for(int x = 0; x < reservedBytes.length; x++) {
                        fileSize = fileSize << 8;
                        fileSize |= reservedBytes[x] & 0xFF;
                    }
                }
            } else {
                if(doneWriting) return;
                
                fileStream.write(currentByte);
                fileBytesWritten++;
                
                if(fileBytesWritten >= fileSize) {
                    doneWriting = true;
                }
            }
            currentByte = 0;
            bitsShifted = 0;
        }
        
        currentByte = currentByte << BITS_PER_CLUSTER;
        currentByte |= (bitCluster & bitMask);
        bitsShifted += BITS_PER_CLUSTER;
    }
    
    /**
     * Returns whether or not the number of bytes written to the file has met
     * the number of bytes specified as the file size (no more writing is taking
     * place).
     * @return Boolean indicating if stream is done writing.
     */
    public boolean isDoneWriting() {
        return doneWriting;
    }
    
    /**
     * Writes any unwritten bit clusters to file and closes stream.
     * @throws IOException 
     */
    public void closeFile() throws IOException {
        while(bitsShifted > 0 && currentByte != 0) {
            write(0);
        }
        
        fileStream.close();
    }
    
}
