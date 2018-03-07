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
        
        /*
        File imgFile = new File("res/garden.png");
        BufferedImage img = ImageIO.read(imgFile);
        BufferedImage out;

        
        File msgFile = new File("res/message.txt");
        
        out = t.insertFile(msgFile, img);
        
        File outFile = new File("res/output.png");
        ImageIO.write(out, "png", outFile);
        */
        
        
        File imgFile = new File("res/output.png");
        BufferedImage img = ImageIO.read(imgFile);
        
        File msgFile = new File("res/extract.txt");
                
        t.extractFile(img, msgFile);
        

    }
    
}
