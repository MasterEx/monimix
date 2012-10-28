package monimix;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Periklis Ntanasis
 */
public class Utils {
    
    public BufferedImage readImage(File image) throws IOException {
        BufferedImage bfimg = ImageIO.read(image);
        return bfimg;
    }
    
    public void saveImage(BufferedImage image, String destination) throws IOException {
        // idea: get prefix from destination name
        saveImage(image, destination, "png");
    }
    
    public void saveImage(BufferedImage image, String destination, String type) throws IOException {
        File file = new File(destination);
        ImageIO.write(image, type, file);
    }
    
    public BufferedImage multiImageEncoding(File[] images) {
        BufferedImage[] bfimages = new BufferedImage[images.length];
        int width = 0, height = 0;
        for(int i=0;i<images.length;i++) {
            try {
                bfimages[i] = readImage(images[i]);
                if(width == 0 || height == 0) {
                    width = bfimages[i].getWidth();
                    height = bfimages[i].getHeight();
                } else if(width != bfimages[i].getWidth() || height != bfimages[i].getHeight()) {
                    System.out.println("All the images should be the same size.");
                    System.exit(1);
                }
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        BufferedImage tmpMixedImg = bfimages[0];
        int RGBval = Color.BLACK.getRGB();
        for(int i=1;i<bfimages.length;i++) {
        HashMap<Integer,Integer> colorTable = new HashMap<Integer,Integer>();
            int colorB = RGBval = RGBval+5000;            
            for(int w=0;w<width;w++) {
                for(int h=0;h<height;h++) {
                    if(bfimages[i].getRGB(w, h) == Color.WHITE.getRGB() && 
                            tmpMixedImg.getRGB(w, h) == Color.WHITE.getRGB()) {
                        continue;
                    } else if(bfimages[i].getRGB(w, h) == Color.WHITE.getRGB()) {
                        if(i==0)
                            tmpMixedImg.setRGB(w, h, Color.BLACK.getRGB());
                    } else if(tmpMixedImg.getRGB(w, h) == Color.WHITE.getRGB()) {
                        tmpMixedImg.setRGB(w, h, colorB);
                    } else {
                        if(!colorTable.containsKey(tmpMixedImg.getRGB(w, h)))
                            colorTable.put(tmpMixedImg.getRGB(w, h), (RGBval = RGBval+5000));
                        tmpMixedImg.setRGB(w, h, colorTable.get(tmpMixedImg.getRGB(w, h)));
                    }
                }
            }
        }        
        return tmpMixedImg;
    }

}
