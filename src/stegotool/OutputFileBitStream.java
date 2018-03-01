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
    private final int BITS_PER_BYTE;
    private int currentByte;
    private int bitsShifted;
    
    public OutputFileBitStream(int bitsPerByte, File file)
            throws FileNotFoundException {
        BITS_PER_BYTE = bitsPerByte;
        currentByte = 0;
        bitsShifted = 0;
        
        fileStream = new FileOutputStream(file);
        
        // initialize mask
        int mask = 0;
        for(int i = 0; i < BITS_PER_BYTE; i++) {
            mask = (mask << 1) | 0b1;
        }
        bitMask = mask;
        
    }
   
    public void write(int bits) throws IOException {
        if(bitsShifted >= 8) {
            fileStream.write(currentByte);
            currentByte = 0;
            bitsShifted = 0;
        }
        
        currentByte |= (bits & bitMask);
        currentByte = currentByte << BITS_PER_BYTE;
    }
    
    public void closeFile() throws IOException {
        while(bitsShifted > 0 && currentByte != 0) {
            write(0);
        }
        
        fileStream.close();
    }
    
}
