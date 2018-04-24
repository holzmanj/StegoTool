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
            for(int b = 7; b >= 0; b--) {
                map[(i * 8) + (7 - b)] = (compressedMap[i] & (1 << b)) != 0;
            }
        }
    }

    /**
     * Marks the plane at a specified index as conjugated.
     * @param index Index of bit plane in list of eligible planes.
     */
    public void setPlaneAsConjugated(int index) {
        map[index] = true;
    }
    
    /**
     * Returns whether a given plane is conjugated.
     * @param index Index of bit plane in list of eligible planes.
     * @return 
     */
    public boolean isPlaneConjugated(int index) {
        return map[index];
    }
    
    /**
     * Compresses conjugation map into an array of bytes for embedding
     * in vessel image.
     * Each boolean value is represented by a bit in one of the bytes.
     * @return Compressed conjugation map.
     */
    public byte[] getCompressedMap() {
        byte[] compressedMap = new byte[(int) Math.ceil(map.length / 8.0)];
        
        for(int i = 0; i < compressedMap.length; i++) {
            for(int b = 0; b < 8 && (i*8)+b < map.length; b++) {
                if(map[(i*8)+b] == true) {
                    compressedMap[i] = (byte) ((compressedMap[i] << 1) | 1);
                } else {
                    compressedMap[i] = (byte) (compressedMap[i] << 1);
                }
            }
        }
        return compressedMap;
    }
    
    /**
     * Gives the number of bytes needed to store the compressed map.
     * @return Size of compressed conjugation map in bytes
     */
    public int getSize() {
        return (int) Math.ceil(map.length / 8.0);
    }
    
}
