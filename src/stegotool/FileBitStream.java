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
public class FileBitStream {
    private final InputStream fileStream;
    private final int bitMask;
    private final int BITS_PER_BYTE;
    private int loadedByte;
    private int bitsShifted;
    
    public FileBitStream(File file, int bitsPerByte) 
           throws FileNotFoundException, IOException {
        fileStream = new FileInputStream(file);
        loadedByte = fileStream.read();
        bitsShifted = 0;
        BITS_PER_BYTE = bitsPerByte;
        
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
        
        if(bitsShifted >= 8) {
            loadedByte = fileStream.read();
            bitsShifted = 0;
        }
        if(loadedByte == -1) {
            return 0;
        }
        
        int bits = loadedByte & bitMask;
        loadedByte = loadedByte >> BITS_PER_BYTE;
        bitsShifted += BITS_PER_BYTE;
        
        return bits;
    }
    
}
