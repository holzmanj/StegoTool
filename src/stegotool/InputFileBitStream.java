package stegotool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 *
 * @author jesse
 */
public class InputFileBitStream {
    private final InputStream fileStream;
    private final int bitMask;
    private final int BITS_PER_CLUSTER;
    private int[] reservedBytes;
    private int loadedByte;
    private int shift;
    
    public InputFileBitStream(File file, int bitsPerCluster) 
           throws FileNotFoundException, IOException {
        fileStream = new FileInputStream(file);
        BITS_PER_CLUSTER = bitsPerCluster;
        shift = -1;
        
        // initialize mask
        int mask = 0;
        for(int i = 0; i < BITS_PER_CLUSTER; i++) {
            mask = (mask << 1) | 0b1;
        }
        bitMask = mask;
    }
    
    /**
     * Sets an array of bytes to be prepended to the stream of bit clusters.
     * Used to store the size of the file.
     * @param reservedBytes Array of bytes to prepend to data file.
     */
    public void setReservedBytes(int[] reservedBytes) {
        this.reservedBytes = reservedBytes;
    }
    
    /**
     * Reads next n bits from the file, where n is the bits per cluster.
     * @return An integer masked with n bits from the file.
     */
    public int read() throws IOException {
        
        if(shift < 0) {
            if(reservedBytes.length > 0) {
                loadedByte = reservedBytes[0];
                reservedBytes = Arrays.copyOfRange(reservedBytes, 1, reservedBytes.length);
            } else {
                loadedByte = fileStream.read();
            }
            shift = 8 - BITS_PER_CLUSTER;
        }
        if(loadedByte == -1) {
            return 0;
        }
        
        int bitCluster = (loadedByte >> shift) & bitMask;
        shift -= BITS_PER_CLUSTER;
        
        System.out.print(Integer.toBinaryString(bitCluster) + " ");
        return bitCluster;
    }
    
}
