/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stegotool;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author jesse
 */
public class BPCSTechnique implements StegoTechnique {
    private final double COMPLEXITY_THRESHOLD = 0.4;
    
    public BPCSTechnique() {
    }

    
    private double calculateComplexity(byte[] bitPlane) {
        int sum = 0;
        
        // count horizontal bit flips
        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 7; col++) {
                sum += ((bitPlane[row] >> col) ^ (bitPlane[row] >> (col + 1))) & 1;
            }
        }
        
        // count vertical bit flips
        for(int col = 0; col < 8; col++) {
            for(int row = 0; row < 7; row++) {
                sum += ((bitPlane[row] >> col) ^ (bitPlane[row + 1] >> col)) & 1;
            }
        }
        
        System.out.println("sum: " + sum);
        
        return (double) sum / 112.0;
    }

    @Override
    public int getImageCapacity(BufferedImage img) {
        return 0;
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
