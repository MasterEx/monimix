/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package monimix;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import no.geosoft.cc.color.ui.ColorUtil;

/**
 *
 * @author periklis
 */
public class Monimix {
    
    static void getHelp() {
        System.out.print("Monimix v.0.1 (2012 Oct 30)- Periklis Ntanasis <pntanasis@gmail.com>\n"
                + "\n"
                + "usage: monimix [arguments] -i [file ...] -o [file ...]\n"
                + "\n"
                + "Arguments:\n"
                + "-i\tInput, maybe white space separated files\n"
                + "-o\tOutput, in case of demux it's the filename prefix\n"
                + "-h\tPrints this help message\n");
    }
    
    //http://ganeshtiwaridotcomdotnp.blogspot.gr/2011/12/java-reflection-getting-name-of-color.html
     public static String getNameReflection(Color colorParam) {
        try {
            //first read all fields in array
            Field[] field = Class.forName("java.awt.Color").getDeclaredFields();
            for (Field f : field) {
                String colorName = f.getName();
                Class<?> t = f.getType();
                // System.out.println(f.getType());
                // check only for constants - "public static final Color"
                if (t == java.awt.Color.class) {
                    Color defined = (Color) f.get(null);
                    if (defined.equals(colorParam)) {
                        System.out.println(colorName);
                        return colorName.toUpperCase();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error... " + e.toString());
        }
        return "NO_MATCH";
    }
     
static Color[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE
        , Color.PINK, Color.RED, Color.YELLOW, Color.WHITE};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        
        ArrayList<File> files = new ArrayList<File>();
        String outputName = "";
        
        int i=0;
        while(i<args.length) {
            if(args[i].equalsIgnoreCase("-h")) {
                getHelp();
                System.exit(0);
            } else if(args[i].equalsIgnoreCase("-i")) {
                i++;
                while(i<args.length && args[i].charAt(0) != '-') {
                    files.add(new File(args[i++]));
                }
            } else if(args[i].equalsIgnoreCase("-o")) {
                i++;
                 if(args[i].charAt(0) != '-') {
                    outputName = args[i++];
                }
            } else {
                System.out.println("Uknown option: "+args[i]+"\nUse monimix -h for help.");
                System.exit(1);
            }
        }
        
        if(args.length == 0) {
            System.out.println("You haven't given any arguments.\nUse monimix -h for help.");
            System.exit(1);
        } else if(outputName.equalsIgnoreCase("")) {
            System.out.println("Output should be defined.\nUse monimix -h for help.");
            System.exit(1);
        } else if(files.size() == 0) {
            System.out.println("Input should be defined.\nUse monimix -h for help.");
            System.exit(1);
        }
        
        if(files.size()>1) {
            Utils.saveImage(Utils.multiImageEncoding(files.toArray(new File[files.size()])),outputName);
        } else {
            BufferedImage[] images = Utils.multiImageDecoding(files.get(0));
            for(i=0;i<images.length;i++)
                Utils.saveImage(images[i], outputName+i+".png");
        }
//        
//         File[] files = {new File("qr1.png"),new File("qr2.png"),new File("qr3.png")};
//         Utils utils = new Utils();
////         
//         utils.saveImage(utils.multiImageEncoding(files), "qr-imagelast.png");
//         
//         BufferedImage[] images = utils.multiImageDecoding(new File("qr-imagelast.png"));
//         for(int i=0;i<images.length;i++)
//             utils.saveImage(images[i], "decoded-qrimage"+i+".png");
        
//        File img = new File("testimg.png");
//        BufferedImage fis = ImageIO.read(img);
//        BufferedImage newImg = new BufferedImage(fis.getWidth(),fis.getHeight(),BufferedImage.TYPE_INT_RGB);
//        Scanner in = new Scanner(img);
//        int b;
//        String x ="";
//        
//        
//        
//        
//       
//        
//        for(int i=0;i<fis.getWidth();i++) {
//            for(int j=0;j<fis.getHeight();j++) {
////                System.out.print(""+fis.getRGB(i, j)+" ");
//                  java.awt.Color c;
//                    c= new java.awt.Color(fis.getRGB(i, j), true);
//                    //System.out.println("--->"+ColorUtil.colorDistance(c, java.awt.Color.BLACK));
//                    int q = possibleColor(c);
//                if(fis.getRGB(i, j)==-2694629) {
//                    newImg.setRGB(i, j, java.awt.Color.BLUE.getRGB());
//                    
////                    int y = possibleColor(c);
//                }
//                else {
//                    newImg.setRGB(i, j, fis.getRGB(i, j));
//                    //int q = possibleColor(fis.getRGB(i, j));
//                }
//                //x += ""+fis.getRGB(i, j)+" ";
//            }
//             //System.out.println("");
//            //x +="\n";
//        }
//        File f = new File("image.png");
//        ImageIO.write(newImg, "png", f);
////        System.out.println(x);
//        
//    }
//    
//    static int possibleColor(java.awt.Color RGB) {
//        java.awt.Color thisColor = java.awt.Color.BLACK;
//        double dif = ColorUtil.colorDistance(thisColor, RGB);
//        //System.out.print("*"+dif+"*");
//        for(int i=0;i<colors.length;i++) {
//            if(ColorUtil.colorDistance(colors[i], RGB) < dif) {
//                thisColor = colors[i];
//                dif = ColorUtil.colorDistance(colors[i], RGB);
//            }
//        }
//        System.out.println(dif+" "+getNameReflection(thisColor));
//        return thisColor.getRGB();
    }
}
