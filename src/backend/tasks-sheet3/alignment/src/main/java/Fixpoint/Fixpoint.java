package Fixpoint;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import AlignmentAlgorithms.Alignment;
import AlignmentAlgorithms.Gotoh;
import AlignmentAlgorithms.SubstitutionMatrix;
import Enums.Mode;


public class Fixpoint {

    Gotoh left, right;
    String sequence1, sequence2;
    SubstitutionMatrix subMatrix;
    int go, ge;

    public Fixpoint(int go, int ge, String sequence1, String sequence2, SubstitutionMatrix subMatrix) {
        this.go = go;
        this.ge = ge;
        this.sequence1 = sequence1;
        this.sequence2 = sequence2;
        this.subMatrix = subMatrix;
    }

    public double calcFixpointScore(Mode mode, int i, int j) {

        left  = new Gotoh(sequence1.substring(0,i), sequence2.substring(0,j), "", "", subMatrix, go, ge);
        right = new Gotoh(sequence1.substring(i+1), sequence2.substring(j+1), "", "", subMatrix, go, ge);

        Alignment leftAlignment = switch (mode) {
            case global     -> left.alignGlobal();
            case local      -> left.alignLocal();
            case freeshift  -> left.alignFreeShift();
        };
        Alignment rightAlignment = switch (mode) {
            case global     -> right.alignGlobal();
            case local      -> right.alignLocal();
            case freeshift  -> right.alignFreeShift();
        };

        leftAlignment.seq1 += sequence1.charAt(i) + rightAlignment.seq1;
        leftAlignment.seq2 += sequence2.charAt(j) + rightAlignment.seq2;

        return left.calcScore(leftAlignment);
    }

    public double[][] calcAllFixpointScores(Mode mode) {
        double[][] scores = new double[sequence1.length()][sequence2.length()];

        System.out.println("Starting Fixpoint.Fixpoint calculations...");

        for (int i = 0; i < sequence1.length(); i++) {
            for (int j = 0; j < sequence2.length(); j++) {
                scores[i][j] = calcFixpointScore(mode, i, j);
            }
        }

        System.out.println("Finished!");
        return scores;
    }

    private static Color[][] createHeatMap(double[][] data, double midpoint) {
        int height = data.length;
        int width = data[0].length;
        Color[][] colors = new Color[height][width];

        // Determine the min and max values in the data
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (data[i][j] < min) min = data[i][j];
                if (data[i][j] > max) max = data[i][j];
            }
        }

        // Map the data to colors
        int r0=192, g0=0,   b0=0;       // Lowest score
        int r1=255, g1=235, b1=132;     // Midpoint
        int r2=0,   g2=126, b2=96;      // Highest score

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double value = (data[i][j] - min) / (max - min);
                if (value <= midpoint) {
                    int r = (int)( (r1 - r0)*(value/midpoint)+r0 );
                    int g = (int)( (g1 - g0)*(value/midpoint)+g0 );
                    int b = (int)( (b1 - b0)*(value/midpoint)+b0 );
                    colors[i][j] = new Color(r,g,b);
                } else {
                    int r = (int)( (r2-r1)*((value-midpoint)/(1-midpoint)) + r1);
                    int g = (int)( (g2-g1)*((value-midpoint)/(1-midpoint)) + g1);
                    int b = (int)( (b2-b1)*((value-midpoint)/(1-midpoint)) + b1);
                    colors[i][j] = new Color(r,g,b);
                }
            }
        }
        return colors;
    }
    public static void saveHeatMap(double[][] data, String filename, double midpoint) throws Exception {
        Color[][] colors = createHeatMap(data, midpoint);

        int height = colors.length;
        int width = colors[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = colors[i][j];
                int rgb = color.getRGB();
                image.setRGB(j, i, rgb);
            }
        }
        File file = new File(filename);
        ImageIO.write(image, "png", file);
        System.out.println("Scores saved to heatmap: " + filename);
    }
}
