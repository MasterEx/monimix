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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Periklis Ntanasis
 */
public class Monimix {

    static void getHelp() {
        System.out.print("monimix v.0.1 (2012 Nov 07) Copyright (C) 2012  Periklis Ntanasis <pntanasis@gmail.com>\n\n"
                + "This program comes with ABSOLUTELY NO WARRANTY; for details visit http://www.gnu.org/licenses/.\n"
                + "This is free software, and you are welcome to redistribute it\n"
                + "under certain conditions; visit http://www.gnu.org/licenses/ for details.\n"
                + "\n"
                + "usage: monimix <command> [arguments] -i [file ...] -o [file ...]\n"
                + "\n"
                + "Commands:\n"
                + "-i\tInput, white space separated files\n"
                + "-o\tOutput, in case of demux it's the filename prefix\n"
                + "-b\tMakes all the input images black\n"
                + "-c\tChanges input image's color to the given color hex value\n"
                + "-t\tTurns white background transparent\n"
                + "-w\tTurns transparent background white\n"
                + "-f\tForce file to be written even if already exists\n"
                + "-d\tDebug mode\n"
                + "-h\tPrints this help message\n");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here        

        ArrayList<File> files = new ArrayList<File>();
        String outputName = "";
        String hexColor = "";
        Boolean transparent = false, getTransparent = false;
        String suffix = "png";

        int i = 0;
        while (i < args.length) {
            if (args[i].equalsIgnoreCase("-h")) {
                getHelp();
                System.exit(0);
            } else if (args[i].equalsIgnoreCase("-i")) {
                i++;
                while (i < args.length && args[i].charAt(0) != '-') {
                    files.add(new File(args[i++]));
                }
            } else if (args[i].equalsIgnoreCase("-o")) {
                i++;
                if (args[i].charAt(0) != '-') {
                    outputName = args[i++];
                    if (outputName.matches(".*\\.(png|gif|jpg)$")) {
                        suffix = outputName.substring(outputName.length() - 3);
                        outputName = outputName.substring(0, outputName.length() - 4);
                    } else if (outputName.matches(".*\\.(jpeg)$")) {
                        suffix = outputName.substring(outputName.length() - 4);
                        outputName = outputName.substring(0, outputName.length() - 5);
                    }
                }
            } else if (args[i].equalsIgnoreCase("-b")) {
                i++;
                Options.ALL_BLACK = true;
            } else if (args[i].equalsIgnoreCase("-c")) {
                hexColor = args[++i];
                i++;
            } else if (args[i].equalsIgnoreCase("-t")) {
                getTransparent = true;
                transparent = true;
                i++;
            } else if (args[i].equalsIgnoreCase("-w")) {
                getTransparent = true;
                transparent = false;
                i++;
            } else if (args[i].equalsIgnoreCase("-d")) {
                Options.DEBUG = true;
                i++;
            } else if (args[i].equalsIgnoreCase("-f")) {
                Options.FORCE_REWRITE = true;
                i++;
            } else {
                System.out.println("Uknown option: " + args[i] + "\nUse monimix -h for help.");
                System.exit(1);
            }
        }

        if (args.length == 0) {
            System.out.println("You haven't given any arguments.\nUse monimix -h for help.");
            System.exit(1);
        } else if (outputName.equalsIgnoreCase("")) {
            System.out.println("Output should be defined.\nUse monimix -h for help.");
            System.exit(1);
        } else if (!hexColor.equalsIgnoreCase("") && (hexColor.length() < 6 || hexColor.length() > 7)) {
            System.out.println("Color should be a valid hex value.\nUse monimix -h for help.");
            System.exit(1);
        } else if (files.isEmpty()) {
            System.out.println("Input should be defined.\nUse monimix -h for help.");
            System.exit(1);
        }

        ArrayList<BufferedImage> tempImage = new ArrayList<BufferedImage>();

        if (getTransparent) {
            for (File image : files) {
                try {
                    tempImage.add(Utils.transparentBG(Utils.readImage(image), transparent));
                } catch (IOException ex) {
                    if (Options.DEBUG) {
                        Logger.getLogger(Monimix.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.err.println("Image " + image.getName() + " doesn't exist");
                    System.exit(1);
                }
            }
        }

        if (!hexColor.equalsIgnoreCase("")) {
            if (hexColor.charAt(0) == '#') {
                hexColor = hexColor.substring(1);
            }
            if (tempImage.isEmpty()) {
                for (File image : files) {
                    try {
                        tempImage.add(Utils.changeColor(Utils.readImage(image), hexColor));
                    } catch (IOException ex) {
                        if (Options.DEBUG) {
                            Logger.getLogger(Monimix.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.err.println("Image " + image.getName() + " doesn't exist");
                        System.exit(1);
                    }
                }
            } else {
                ArrayList<BufferedImage> tmpImage = new ArrayList<BufferedImage>();
                for (BufferedImage image : tempImage) {
                    tmpImage.add(Utils.changeColor(image, hexColor));
                }
                tempImage = tmpImage;
            }
        }

        if (getTransparent || !hexColor.equalsIgnoreCase("")) {
            i = 0;
            if (tempImage.size() == 1) {
                try {
                    Utils.saveImage(tempImage.get(0), outputName + "." + suffix, suffix);
                } catch (IOException ex) {
                    if (Options.DEBUG) {
                        Logger.getLogger(Monimix.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                for (BufferedImage image : tempImage) {
                    try {
                        Utils.saveImage(image, outputName + (i++) + "." + suffix, suffix);
                    } catch (IOException ex) {
                        if (Options.DEBUG) {
                            Logger.getLogger(Monimix.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.err.println("Unable to write " + outputName + (i++) + "." + suffix + " image");
                        if (suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("jpg")) {
                            System.err.println("If you use OpenJDK there isn't JPEG support built in");
                        }
                        System.exit(1);
                    }
                }
            }
            System.exit(0);
        }

        if (files.size() > 1) {
            try {
                Utils.saveImage(Utils.multiImageEncoding(files.toArray(new File[files.size()])), outputName + "." + suffix, suffix);
            } catch (IOException ex) {
                if (Options.DEBUG) {
                    Logger.getLogger(Monimix.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.err.println("Unable to write " + outputName + (i++) + "." + suffix + " image");
                if (suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("jpg")) {
                    System.err.println("If you use OpenJDK there isn't JPEG support built in");
                }
                System.exit(1);
            }
        } else {
            BufferedImage[] images = Utils.multiImageDecoding(files.get(0));
            for (i = 0; i < images.length; i++) {
                try {
                    Utils.saveImage(images[i], outputName + i + "." + suffix, suffix);
                } catch (IOException ex) {
                    if (Options.DEBUG) {
                        Logger.getLogger(Monimix.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.err.println("Unable to write " + outputName + (i++) + "." + suffix + " image");
                    if (suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("jpg")) {
                        System.err.println("If you use OpenJDK there isn't JPEG support built in");
                    }
                    System.exit(1);
                }
            }
        }

    }
}
