package AlignmentAlgorithms;

import Enums.Mode;

public class Gotoh extends Aligner {
    private int[][] matrixA;
    private int[][] matrixI;
    private int[][] matrixD;

    public Gotoh(String seq1, String seq2,
                 String seqID1, String seqID2,
                 SubstitutionMatrix subMatrix, int go, int ge) {

        super(seq1, seq2, seqID1, seqID2, subMatrix, go*100, ge*100);
        int len1 = seq1.length();
        int len2 = seq2.length();
        matrixA = new int[len1+1][len2+1];
        matrixD = new int[len1+1][len2+1];
        matrixI = new int[len1+1][len2+1];
    }

    public void initMatrixID() {
        for (int i = 1; i < matrixI[0].length; i++) {
            matrixI[0][i] = Integer.MIN_VALUE / 2;
        }
        for (int i = 1; i < matrixD.length; i++) {
            matrixD[i][0] = Integer.MIN_VALUE / 2;
        }
    }

    public void initMatrixA(Mode mode) {
        if (mode == Mode.local || mode == Mode.freeshift) return;
        if (matrixA[0].length > 1) {
            matrixA[0][1] = go + ge;
            for (int i = 2; i < matrixA[0].length; i++) {
                matrixA[0][i] = matrixA[0][i - 1] + ge;
            }
        }
        if (matrixA.length > 1) {
            matrixA[1][0] = go + ge;
            for (int i = 2; i < matrixA.length; i++) {
                matrixA[i][0] = matrixA[i - 1][0] + ge;
            }
        }
    }

    public void gotoh(Mode mode) {

        initMatrixID();
        initMatrixA(mode);

        int val1, val2;
        for (int i = 1; i < matrixA.length; i++) {
            for (int j = 1; j < matrixA[0].length; j++) {
                // Matrix I
                val1 = matrixA[i-1][j] + go + ge;
                val2 = matrixI[i-1][j] + ge;
                matrixI[i][j] = val1 > val2 ? val1 : val2;

                // Matrix D
                val1 = matrixA[i][j-1] + go + ge;
                val2 = matrixD[i][j-1] + ge;
                matrixD[i][j] = val1 > val2 ? val1 : val2;

                // Matrix A
                val1 = matrixA[i-1][j-1] + subMatrix.getValue(seq1.charAt(i-1), seq2.charAt(j-1));
                if (val1 < matrixI[i][j]) val1 = matrixI[i][j];
                if (val1 < matrixD[i][j]) val1 = matrixD[i][j];
                matrixA[i][j] = val1;
                if (mode == Mode.local && matrixA[i][j] < 0) matrixA[i][j] = 0;
            }
        }
    }

    private int[] getGlobalStart() {
        return new int[] {seq1.length(), seq2.length(), matrixA[seq1.length()][seq2.length()]};
    }
    private int[] getLocalStart() {
        int i = -1;
        int j = -1;
        int bestScore = -1;
        for (int m = 0; m < matrixA.length; m++) {
            for (int n = 0; n < matrixA[0].length; n++) {
                if (matrixA[m][n] > bestScore) {
                    bestScore = matrixA[m][n];
                    i = m;
                    j = n;
                }
            }
        }
        return new int[] {i, j, bestScore};
    }
    private int[] getFrameshiftStart() {
        int i = -1;
        int j = -1;
        int bestScore = -1;
        for (int m = 0; m < matrixA.length; m++) {
            if (matrixA[m][seq2.length()] > bestScore) {
                bestScore = matrixA[m][seq2.length()];
                i = m;
                j = seq2.length();
            }
        }
        for (int n = 0; n < matrixA[0].length; n++) {
            if (matrixA[seq1.length()][n] > bestScore) {
                bestScore = matrixA[seq1.length()][n];
                i = seq1.length();
                j = n;
            }
        }
        return new int[] {i, j, bestScore};
    }

    public Alignment traceback(Mode mode) {

        int[] startIndex = switch (mode) {
            case global     -> getGlobalStart();
            case local      -> getLocalStart();
            case freeshift  -> getFrameshiftStart();
        };

        int i = startIndex[0];
        int j = startIndex[1];
        int score = startIndex[2];

        String[] pad = generatePadding(seq1.length(), seq2.length(), i, j);
        String alignedSeq1 = pad[0];
        String alignedSeq2 = pad[1];

        while (i > 0 && j > 0) {

            if ((mode == Mode.local ) && matrixA[i][j] == 0) break;

            if (mode == Mode.freeshift && (i == 0 || j == 0)) break;

            // Match/Mismatch
            if (matrixA[i][j] == matrixA[i-1][j-1] + subMatrix.getValue(seq1.charAt(i-1), seq2.charAt(j-1))) {
                alignedSeq1 = seq1.charAt(i-1) + alignedSeq1;
                alignedSeq2 = seq2.charAt(j-1) + alignedSeq2;
                i--;
                j--;

            // Insertion
            } else if (matrixA[i][j] == matrixI[i][j]) {
                int k = 0;
                while ((matrixA[i-k][j] + go + k*ge) != matrixA[i][j]) {
                    k++;
                    alignedSeq1 = seq1.charAt(i-k) + alignedSeq1;
                    alignedSeq2 = '-' + alignedSeq2;
                }
                i = i - k;

            // Deletion
            } else if (matrixA[i][j] == matrixD[i][j]) {
                int k = 0;
                while ((matrixA[i][j-k] + go + k*ge) != matrixA[i][j]) {
                    k++;
                    alignedSeq1 = '-' + alignedSeq1;
                    alignedSeq2 = seq2.charAt(j-k) + alignedSeq2;
                }
                j = j - k;
            } else {
                System.out.println("problem");
                System.out.println(i + ", " + j);
                System.out.println(matrixA[i][j]);
                System.out.println();
            }
        }
        pad = generatePadding(i, j, 0, 0);
        alignedSeq1 = pad[0] + alignedSeq1;
        alignedSeq2 = pad[1] + alignedSeq2;

        return new Alignment(alignedSeq1, alignedSeq2, seqID1, seqID2, score);
    }

    public String[] generatePadding(int iStart, int jStart, int iEnd, int jEnd) {
        String pad1 = "";
        String pad2 = "";
        for (; jStart > jEnd; jStart--) {
            pad1 = "-" + pad1;
            pad2 = seq2.charAt(jStart-1) + pad2;
        }
        for (; iStart > iEnd; iStart--) {
            pad1 = seq1.charAt(iStart-1) + pad1;
            pad2 = "-" + pad2;
        }
        return new String[] {pad1, pad2};
    }


    public int[][] getMatrixA() {
        return matrixA;
    }
    public int[][] getMatrixI() {
        return matrixI;
    }
    public int[][] getMatrixD() {
        return matrixD;
    }





    @Override
    public Alignment alignGlobal() {
        gotoh(Mode.global);
        return traceback(Mode.global);
    }

    @Override
    public Alignment alignLocal() {
        gotoh(Mode.local);
        return traceback(Mode.local);
    }

    @Override
    public Alignment alignFreeShift() {
        gotoh(Mode.freeshift);
        return traceback(Mode.freeshift);
    }

    @Override
    public String matrixesToString() {
        StringBuilder scoreMatrix = new StringBuilder();

        scoreMatrix.append("Score Matrix: \n");
        for (int[] row : matrixA) {
            for (int value : row) {
                scoreMatrix.append((value/100.0) + " ");
            }
            scoreMatrix.append("\n");
        }
        scoreMatrix.append("\n");
        scoreMatrix.append("Insertion Matrix: \n");
        for (int[] row : matrixI) {
            for (int value : row) {
                scoreMatrix.append((value/100.0) + " ");
            }
            scoreMatrix.append("\n");
        }
        scoreMatrix.append("\n");
        scoreMatrix.append("Deletion Matrix: \n");
        for (int[] row : matrixD) {
            for (int value : row) {
                scoreMatrix.append((value/100.0) + " ");
            }
            scoreMatrix.append("\n");
        }
        scoreMatrix.append("\n");
        scoreMatrix.append("\n");
        return scoreMatrix.toString();
    }
}
