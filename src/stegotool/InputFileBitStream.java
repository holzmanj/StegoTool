package stegotool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author jesse
 */
public class InputFileBitStream {
    private final InputStream fileStream;
    private final int bitMask;
    private final int BITS_PER_BYTE;
    private int loadedByte;
    private int shift;
    
    public InputFileBitStream(File file, int bitsPerByte) 
           throws FileNotFoundException, IOException {
        fileStream = new FileInputStream(file);
        loadedByte = fileStream.read();
        BITS_PER_BYTE = bitsPerByte;
        shift = 8 - BITS_PER_BYTE;
        
        System.out.println(Integer.toBinaryString(loadedByte));
        
        // initialize mask
        int mask = 0;
        for(int i = 0; i < BITS_PER_BYTE; i++) {
            mask = (mask << 1) | 0b1;
        }
        bitMask = mask;
    }
    
    /**
     * Reads next n bits from the file, where n is the BITS_PER_BYTE constant.
     * @return An integer masked with n bits from the file.
     */
    public int read() throws IOException {
        
        if(shift < 0) {
            loadedByte = fileStream.read();
            shift = 8 - BITS_PER_BYTE;
        }
        if(loadedByte == -1) {
            return 0;
        }
        
        int bits = (loadedByte >> shift) & bitMask;
        shift -= BITS_PER_BYTE;
        
        System.out.print(Integer.toBinaryString(bits) + " ");
        return bits;
    }
    
}
