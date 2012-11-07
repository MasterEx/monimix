/**
 *  monimix - monochrome image mix: it combines monochrome images to one
 *  Copyright (C) 2012  Periklis Ntanasis <pntanasis@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package monimix;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Periklis Ntanasis
 */
public class Utils {

    /**
     * It reads an image
     *
     * @param image The images location
     * @return The image as a BufferedImage object
     * @throws IOException
     */
    public static BufferedImage readImage(File image) throws IOException {
        BufferedImage bfimg = ImageIO.read(image);
        if (Options.ALL_BLACK) {
            bfimg = makeItBlack(bfimg);
        }
        return bfimg;
    }

    /**
     * Saves an image to the specified location
     *
     * @param image The image to be saved
     * @param destination The destination file
     * @param type The image type that will be used for the image to be saved
     * @throws IOException
     */
    public static void saveImage(BufferedImage image, String destination, String type) throws IOException {
        File file = new File(destination);
        if (file.exists() && !Options.FORCE_REWRITE) {
            System.out.print("The file " + file.getName() + " exists.\n"
                    + "Do you want ot rewrite it? (Y/N)");
            Scanner in = new Scanner(System.in);
            if (in.next().equalsIgnoreCase("y")) {
                ImageIO.write(image, type, file);
            }
        } else {
            ImageIO.write(image, type, file);
        }
    }

    /**
     * Combines the given File array of images to one image. We make as many
     * combination as the images. For example we set a temp image to the 1st
     * image and temp image with the second to a temp image, this with the 3rd
     * to the temp, with the 4th and so on. In case we want to combine 2 images
     * we are going to need as many colors as getColorNumber() will return. This
     * will be 3 for 2 images. One for image A, one for image B and one for the
     * combination of AB. The 2nd image will be colored as the midColor color
     * and the combo will be colored in midColor+5000. 5000 is the step between
     * the different colors that will be used.
     *
     * This could be just one in case we used only the original computer
     * generated images because they wouldn't be corrupted by external noise.
     * However the visual result wouldn't be so visible.
     *
     * Ideally one should use visually distinct colors only. * In case we would
     * like to produce images to be used with cameras, smart phones etc we
     * should use only visually distinct colors and at the decoding stage (see
     * multiImageDecoding()) we should normalize the image by changing the
     * colors to the most near by calculating the distance from a set of
     * acceptable colors.
     *
     * OK, after the first combination, we will combine the temp image with the
     * 3rd, let's say C image. C image will be colored with the midColor's color
     * and every combination will be colored with midColor * 5000 +
     * tmpMixedImg.getRGB(w, h) + 5000. This means that for 3 images we need 7
     * colors (we know this from getColorNumber()). The first 3 were already
     * used for the combo of the first 2 images. The C image will be colored as
     * midColor = 4th color wherever it doesn't collide with the temp image.
     * Then the AC will be colored with the 5th color, BC with the 6th and ABC
     * with the 7th.
     *
     * This continues like that until we produce the final image.
     *
     * @param images A File array of images that will be read with the
     * readImage() and combined to one
     * @return The combined image
     */
    public static BufferedImage multiImageEncoding(File[] images) {
        BufferedImage[] bfimages = new BufferedImage[images.length];
        int width = 0, height = 0;
        for (int i = 0; i < images.length; i++) {
            try {
                bfimages[i] = readImage(images[i]);
                if (width == 0 || height == 0) {
                    width = bfimages[i].getWidth();
                    height = bfimages[i].getHeight();
                } else if (width != bfimages[i].getWidth() || height != bfimages[i].getHeight()) {
                    System.out.println("All the images should be the same size.");
                    System.exit(1);
                }
            } catch (IOException ex) {
                if (Options.DEBUG) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        BufferedImage tmpMixedImg = (!Options.ALL_BLACK) ? makeItBlack(bfimages[0]) : bfimages[0];
        for (int i = 1; i < bfimages.length; i++) {
            int numberOfColors = getColorNumber(i + 1);
            int midColor = (int) Math.floor(numberOfColors / 2);
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    if ((bfimages[i].getRGB(w, h) == Color.WHITE.getRGB()
                            || bfimages[i].getRGB(w, h) == 0)
                            && (tmpMixedImg.getRGB(w, h) == Color.WHITE.getRGB()
                            || tmpMixedImg.getRGB(w, h) == 0)) {
                        if (tmpMixedImg.getRGB(w, h) == 0) {
                            tmpMixedImg.setRGB(w, h, Color.WHITE.getRGB());
                        }
                    } else if (bfimages[i].getRGB(w, h) == Color.WHITE.getRGB()
                            || bfimages[i].getRGB(w, h) == 0) {
                        if (i == 0) {
                            tmpMixedImg.setRGB(w, h, Color.BLACK.getRGB());
                        }
                    } else if (tmpMixedImg.getRGB(w, h) == Color.WHITE.getRGB()
                            || tmpMixedImg.getRGB(w, h) == 0) {
                        tmpMixedImg.setRGB(w, h, midColor * 5000 + Color.BLACK.getRGB());
                    } else {
                        tmpMixedImg.setRGB(w, h, midColor * 5000 + tmpMixedImg.getRGB(w, h) + 5000);
                    }
                }
            }
        }
        return tmpMixedImg;
    }

    /**
     * Calculates the max number of different colors that we need to use to
     * combine N images. If we want to combine N images we will need
     * <pre>
     * N   / N \     N      N!
     * Σ   |   |  =  Σ   --------
     * i=1 \ i /     i=1 i!(N-i)!
     * </pre> This is why we need N distinct colors for every different image,
     * N!/(2!(N-2)!) colors for every different color pair, ... and eventually 1
     * color for the combination of all the different colors
     *
     * @param numberOfImages The number of images we want to combine
     * @return The number of the different colors that we want in order to
     * combine N images
     */
    private static int getColorNumber(int numberOfImages) {
        int n = 0;
        for (int i = 1; i <= numberOfImages; i++) {
            n += factorial(numberOfImages) / (factorial(i) * factorial(numberOfImages - i));
        }
        return n;
    }

    /**
     * A trivial recursive calculation of a number\'s factorial
     *
     * @param number The number of which we want to calculate the factorial
     * @return The given number\'s factorial
     */
    private static int factorial(int number) {
        if (number <= 1) {
            return 1;
        } else {
            return number * factorial(number - 1);
        }
    }

    /**
     * Returns the original images that were combined to one.
     *
     * Firstly it calculates the number of the max possible colors that were
     * used. Counting only the visible colors isn't enough because the combined
     * images could not collide and the visible colors could be less than
     * expected. So we calculate the max possible colors by the visible color
     * with the max RGB value and so we find the number images that are
     * combined.
     *
     * After that we use the opposite way from which described in
     * multiImageDecoding() in order to get the original images.
     *
     * @param image The combined image
     * @return The array of the original images
     */
    public static BufferedImage[] multiImageDecoding(File image) {

        BufferedImage originalImage = null;
        try {
            originalImage = readImage(image);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        // not counting zero
        BufferedImage[] bfimages = new BufferedImage[numberOfImages(getDifferentColors(originalImage) - 1)];
        for (int i = bfimages.length - 1; i >= 0; i--) {
            bfimages[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            int numberOfColors = getColorNumber(i + 1);
            int midColor = (int) Math.floor(numberOfColors / 2);
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    if (i == 0) {
                        if (originalImage.getRGB(w, h) != Color.WHITE.getRGB()
                                && originalImage.getRGB(w, h) != 0) {
                            originalImage.setRGB(w, h, Color.BLACK.getRGB());
                        }
                        continue;
                    }
                    if (originalImage.getRGB(w, h) == Color.WHITE.getRGB()
                            || originalImage.getRGB(w, h) == 0) {
                        bfimages[i].setRGB(w, h, Color.WHITE.getRGB());
                    } else if (originalImage.getRGB(w, h) > Color.BLACK.getRGB() + (midColor * 5000)) {
                        bfimages[i].setRGB(w, h, Color.BLACK.getRGB());
                        originalImage.setRGB(w, h, originalImage.getRGB(w, h) - midColor * 5000 - 5000);
                    } else if (originalImage.getRGB(w, h) == Color.BLACK.getRGB() + (midColor * 5000)) {
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

    /**
     * Calculates the possible max value contained colors of a combined image
     *
     * @param image A combined image
     * @return The possible max value contained colors of a combined image
     */
    private static int getDifferentColors(BufferedImage image) {
        int maxColor = Color.BLACK.getRGB();
        for (int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {
                if (image.getRGB(w, h) != Color.WHITE.getRGB()
                        && image.getRGB(w, h) != 0) {
                    if (image.getRGB(w, h) > maxColor) {
                        maxColor = image.getRGB(w, h);
                    }
                }
            }
        }
        int trueMaxColor = 0;
        for (int i = 1; trueMaxColor < (maxColor - Color.BLACK.getRGB()); i++) {
            trueMaxColor = 5000 * getColorNumber(i) * 2;
        }
        return ((trueMaxColor / 5000) + 1);
    }

    /**
     * Calculates the number of images that are combined to one by the given
     * possible max contained colors of the combined image
     *
     * @param colors The possible max contained colors of the image
     * @return The number of images that are combined to one
     */
    private static int numberOfImages(int colors) {
        int n = 1;
        while ((colors = colors / 2) >= 1) {
            n++;
        }
        return n;
    }

    /**
     * Changes the foreground colors to black
     *
     * @param image The original image
     * @return The converted image
     */
    private static BufferedImage makeItBlack(BufferedImage image) {
        for (int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {
                if (image.getRGB(w, h) != Color.WHITE.getRGB()
                        && image.getRGB(w, h) != 0) {
                    image.setRGB(w, h, Color.BLACK.getRGB());
                }
            }
        }
        return image;
    }

    /**
     * Changes the foreground colors to the color equivalent to the given hex
     * value
     *
     * @param image The original image
     * @param hexval The hexadecimal value of a color without a leading #
     * @return The converted image
     */
    public static BufferedImage changeColor(BufferedImage image, String hexval) {
        int red = Integer.valueOf(hexval.substring(0, 2), 16);
        int green = Integer.valueOf(hexval.substring(2, 4), 16);
        int blue = Integer.valueOf(hexval.substring(4, 6), 16);
        int RGBval = red;
        RGBval = (RGBval << 8) + green;
        RGBval = (RGBval << 8) + blue;
        RGBval = RGBval + Color.BLACK.getRGB();
        for (int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {
                if (image.getRGB(w, h) != Color.WHITE.getRGB()
                        && image.getRGB(w, h) != 0) {
                    image.setRGB(w, h, RGBval);
                }
            }
        }
        return image;
    }

    /**
     * Returns the same image with transparent background instead of white or
     * vice versa. Creates a temp image to surpass the transparency issues.
     *
     * @param image The original image
     * @param transparent True in case we want the output image to have
     * transparent background or false if we want it white
     * @return The converted image
     */
    public static BufferedImage transparentBG(BufferedImage image, Boolean transparent) {
        BufferedImage tmpImg = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_4BYTE_ABGR );
        for (int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {                
                if (image.getRGB(w, h) == Color.WHITE.getRGB() && transparent) {
                    tmpImg.setRGB(w, h, 0);
                } else if (image.getRGB(w, h) == 0 && !transparent) {
                    tmpImg.setRGB(w, h, Color.WHITE.getRGB());
                } else {
                    tmpImg.setRGB(w, h, image.getRGB(w, h));
                }
            }
        }
        return tmpImg;
    }
}
