package org.propra13.HelperClasses.Alignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.SplittableRandom;

public class SubstitutionMatrix {
    private int[][] subMatrix;
    private HashMap<Character, Integer> keys = new HashMap<Character, Integer>();

    public SubstitutionMatrix(String path) {
        // https://www.w3schools.com/java/java_files_read.asp
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);

            int matrixPosition = 0;

            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();

                if (line.startsWith("NUMROW")) {
                    int matrixSize = Integer.parseInt(line.split(" ")[1]);
                    subMatrix = new int[matrixSize][matrixSize];
                } else if (line.startsWith("ROWINDEX")) {
                    String matrixLabels = line.split(" ")[1];
                    for (int i = 0; i < matrixLabels.length(); i++) {
                        keys.put(matrixLabels.charAt(i), i);
                    }
                } else if (line.startsWith("MATRIX")) {
                    String[] matrixValues = line.split("\\s+|\\t");

                    for (int i = 0; i < matrixValues.length - 1; i++) {

                        int substValue = (int) Math.round((Double.parseDouble(matrixValues[i + 1]) * 100));
                        subMatrix[matrixPosition][i] = substValue;
                        subMatrix[i][matrixPosition] = substValue;
                    }
                    matrixPosition++;
                }
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public SubstitutionMatrix(int[][] matrix, String keys) {
        subMatrix = matrix;
        for (int i = 0; i < keys.length(); i++) {
            this.keys.put(keys.charAt(i), i);
        }
    }

    public SubstitutionMatrix(int[][] matrix, HashMap<Character, Integer> keys) {
        subMatrix = matrix;
        this.keys = keys;
    }

    public static SubstitutionMatrix breedMatrixes(SubstitutionMatrix parent1, SubstitutionMatrix parent2,
                                                   double maxChange, double changeChance) {
        SplittableRandom rand = new SplittableRandom();
        int[][] newSubMatrix = new int[parent1.subMatrix.length][parent1.subMatrix.length];

        for (int i = 0; i < parent1.subMatrix.length; i++) {
            for (int j = 0; j < parent1.subMatrix.length; j++) {
                newSubMatrix[i][j] = (int) ((parent1.subMatrix[i][j] + parent2.subMatrix[i][j]) / 2.0 + 0.5);
                if (rand.nextDouble() < changeChance) {
                    newSubMatrix[i][j] += (int) (maxChange * rand.nextDouble(-1, 1) + 0.5);
                }
            }
        }
        return new SubstitutionMatrix(newSubMatrix, parent1.keys);
    }

    public SubstitutionMatrix getMutation(double maxChange, double changeChance) {
        SplittableRandom rand = new SplittableRandom();
        int[][] newSubMatrix = new int[subMatrix.length][subMatrix.length];

        for (int i = 0; i < subMatrix.length; i++) {
            for (int j = 0; j < subMatrix.length; j++) {
                newSubMatrix[i][j] = subMatrix[i][j];
                if (rand.nextDouble() < changeChance) {
                    newSubMatrix[i][j] += (int) (maxChange * rand.nextDouble(-1, 1) + 0.5);
                }
            }
        }
        return new SubstitutionMatrix(newSubMatrix, keys);
    }

    public int getValue(char key1, char key2) {
        return subMatrix[keys.get(key1)][keys.get(key2)];
    }

    public int[][] getSubMatrix() {
        return subMatrix;
    }
}
