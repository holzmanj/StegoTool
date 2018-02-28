package stegotool;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author holzmanj
 */
public class Main {
    static StegoTechnique t;
    
    public static void main(String[] args) throws IOException {
        
        t = new LSBTechnique();
        
        File imgFile = new File("garden.png");
        BufferedImage img = ImageIO.read(imgFile);
        BufferedImage out;

        
        File msgFile = new File("SteganographyUML");
        
        out = t.insertFile(msgFile, img);
        
        File outFile = new File("output.png");
        ImageIO.write(out, "png", outFile);

    }
    
}
