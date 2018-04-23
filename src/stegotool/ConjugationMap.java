package stegotool;

/**
 * Object used to track which bit planes should be conjugated and 
 * encoding/decoding the map for insertion/extraction.
 * @author jesse
 */
public class ConjugationMap {
    boolean[] map;
    
    /**
     * Constructor used for inserting data into image.
     * @param eligiblePlanes Number of planes in image that pass the complexity
     * threshold.
     */
    public ConjugationMap(int eligiblePlanes) {
        map = new boolean[eligiblePlanes];
    }
    
    /**
     * Constructor used for extracting data from image.
     * @param compressedMap Conjugation map bytes as extracted from image. 
     */
    public ConjugationMap(byte[] compressedMap) {
        map = new boolean[compressedMap.length * 8];
        
        // decompress map
        for(int i = 0; i < compressedMap.length; i++) {
            for(int b = 7; b <= 7; b++) {
                if(((compressedMap[i] >> b) & 1) == 1) {
                    map[(i * 8) + b] = true;
                } else {
                    map[(i * 8) + b] = false;
                }
            }
        }
    }
    
}
