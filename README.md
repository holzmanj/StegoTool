
# Image StegoTool
This is a GUI based steganography tool for embedding data files within images.  Data can be embdedded using two different steganographic techniques:

**Least Significant Bit (LSB)** reserves the two least significant bits of every pixel color byte in an image for the storage of hidden data.  These bits control very small fluctuations in a pixel's color and so changing their values typically produces changes in an image that are not easily perceptible. 

**Bit-Plane Segmentation Complexity (BPCS)** decomposes an image into a set of small bit-plane segments and uses these to identify the visually complex areas of the image.  These areas can then be used to embed the hidden data.

Depending on the contents of the image, one of these techniques may provide a greater storage capacity for embedding hidden data.
## Installation
This tool was created in NetBeans and so the project can be downloaded and imported directly into NetBeans where it can be compiled and run.

To compile via command line, go to the desired installation directory and enter:
    
    git clone https://github.com/holzmanj/StegoTool.git
    cd StegoTool/
    ant -f build.xml compile
To run the compiled code enter:

    ant -f build.xml run
Alternatively, after compilation you can generate an executable jar by entering:
    
    ant -f build.xml jar
The jar will be found in the `dist/` subfolder and be executed with:

    java -jar StegoTool.jar

## Known bugs and future improvements
* Certain errors and exceptions are not displayed through GUI
* Extracting data from an image will occasionally produce an empty file