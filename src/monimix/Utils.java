package monimix;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Periklis Ntanasis
 */
public class Utils {
    
    public static BufferedImage readImage(File image) throws IOException {
        BufferedImage bfimg = ImageIO.read(image);
        return bfimg;
    }
    
    public static void saveImage(BufferedImage image, String destination) throws IOException {
        // idea: get prefix from destination name
        saveImage(image, destination, "png");
    }
    
    public static void saveImage(BufferedImage image, String destination, String type) throws IOException {
        File file = new File(destination);
        ImageIO.write(image, type, file);
    }
    // NOTE make all th images full black
    public static BufferedImage multiImageEncoding(File[] images) {
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
        BufferedImage tmpMixedImg = makeItBlack(bfimages[0]);
        for(int i=1;i<bfimages.length;i++) {
        int numberOfColors = getColorNumber(i+1);
        // change midColor name to something more relevant
        int midColor = (int) Math.floor(numberOfColors/2);
            for(int w=0;w<width;w++) {
                for(int h=0;h<height;h++) {
                    if(bfimages[i].getRGB(w, h) == Color.WHITE.getRGB() && 
                            tmpMixedImg.getRGB(w, h) == Color.WHITE.getRGB()) {
                        continue;
                    } else if(bfimages[i].getRGB(w, h) == Color.WHITE.getRGB()) {
                        if(i==0)
                            tmpMixedImg.setRGB(w, h, Color.BLACK.getRGB());
                    } else if(tmpMixedImg.getRGB(w, h) == Color.WHITE.getRGB()) {
                        tmpMixedImg.setRGB(w, h, midColor*5000 + Color.BLACK.getRGB());
                    } else {
                        tmpMixedImg.setRGB(w, h, midColor*5000 + tmpMixedImg.getRGB(w, h) + 5000);
                    }
                }
            }
        }        
        return tmpMixedImg;
    }
    
    private static int getColorNumber(int numberOfImages) {
        int n = 0;
        for(int i=1;i<=numberOfImages;i++)
            n += factorial(numberOfImages)/(factorial(i)*factorial(numberOfImages-i));
        return n;
    }
    
    private static int factorial(int number) {
        if(number<=1)
            return 1;
        else
            return number*factorial(number-1);
    }
    
    public static BufferedImage[] multiImageDecoding(File image) {
        
        System.out.println(Color.BLACK.getRGB()+" "+Color.WHITE.getRGB());
        System.out.println((Color.BLACK.getRGB()+1000)>Color.BLACK.getRGB());
        
        BufferedImage originalImage = null;
        try {
            originalImage = readImage(image);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        System.out.println(numberOfImages(getDifferentColors(originalImage)) + " "+getDifferentColors(originalImage));
        // not counting zero
        BufferedImage[] bfimages = new BufferedImage[numberOfImages(getDifferentColors(originalImage)-1)];
        for(int i=bfimages.length-1;i>=0;i--) {            
            bfimages[i] = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            int numberOfColors = getColorNumber(i+1);
            // change midColor name to something more relevant
            int midColor = (int) Math.floor(numberOfColors/2);
            System.out.println(midColor);
            for(int w=0;w<width;w++) {
                for(int h=0;h<height;h++) {                    
                    if(i==0) {
                        if(originalImage.getRGB(w, h) != Color.WHITE.getRGB())
                            originalImage.setRGB(w, h, Color.BLACK.getRGB());
                        continue;
                    }
                    if(originalImage.getRGB(w, h) == Color.WHITE.getRGB()) {
                        bfimages[i].setRGB(w, h, Color.WHITE.getRGB());
                    } else if(originalImage.getRGB(w, h) > Color.BLACK.getRGB() + (midColor * 5000)) {
//                              System.out.println("+2 "+midColor+" "+numberOfColors+" "+i);
                          bfimages[i].setRGB(w, h, Color.BLACK.getRGB());                          
                          originalImage.setRGB(w, h, originalImage.getRGB(w, h) - midColor*5000 - 5000);                          
                    } else if(originalImage.getRGB(w, h) == Color.BLACK.getRGB() + (midColor * 5000)) {
//                              System.out.println("+1 "+midColor+" "+numberOfColors+" "+i);
                        bfimages[i].setRGB(w, h, Color.BLACK.getRGB());
                            originalImage.setRGB(w, h, Color.WHITE.getRGB());
                    } else {
                        bfimages[i].setRGB(w, h, Color.WHITE.getRGB());
                    }
                }
            }
        }
        bfimages[0] = originalImage;
        return bfimages;
    }
    
    private static int getDifferentColors(BufferedImage image) {
        Set<Integer> colors = new HashSet<Integer>();
        for(int w=0;w<image.getWidth();w++) {
            for(int h=0;h<image.getHeight();h++) {
                colors.add(image.getRGB(w, h));
            }
        }
        return colors.size();
    }
    
    private static int numberOfImages(int colors) {
        int n = 1;
        while((colors = colors/2) >= 1)
            n++;
        return n;
    }
    
    private static BufferedImage makeItBlack(BufferedImage image) {
        for(int w=0;w<image.getWidth();w++) {
            for(int h=0;h<image.getHeight();h++) {
                if(image.getRGB(w,h) != Color.WHITE.getRGB())
                    image.setRGB(w, h, Color.BLACK.getRGB());
            }
        }
        return image;
    }

}
