package AlignmentAlgorithms;

import java.util.Objects;

public class NWSW extends Aligner{

    //public variables
    public int[][] matrixScore;
    public String [][] tracebackMatrix;
    public int score;

    public NWSW(String seq1, String seq2, String seqID1, String seqID2, SubstitutionMatrix subMatrix, int go, int ge) {

        super(seq1, seq2, seqID1, seqID2, subMatrix, go*100, ge*100);
        matrixScore = new int[seq1.length() + 1][seq2.length() + 1];
        tracebackMatrix = new String[seq1.length() + 1][seq2.length() + 1];
    }


    public void matrixInit(String mode) {
        if(mode.equals("global")){
            for(int i = 0; i < seq1.length() +1; i++){
                matrixScore[i][0] = ge * i;
                tracebackMatrix[i][0] = null;
            }
            for(int i = 0; i < seq2.length() +1; i++) {
                matrixScore[0][i] = ge * i;
                tracebackMatrix[0][i] = null;
            }
        }
        for (int i = 1; i < seq1.length() +1; i++) {
            for (int j = 1; j < seq2.length() +1; j++) {
                char seq1Pos = seq1.charAt(i-1);
                char seq2Pos = seq2.charAt(j-1);
                // all possible values

                int DIAGONAL = matrixScore[i - 1][j - 1] + subMatrix.getValue(seq1Pos, seq2Pos);
                int HORIZONTAL = matrixScore[i][j - 1] + ge;
                int VERTIKAL = matrixScore[i - 1][j] + ge;

                int max1 = Math.max(DIAGONAL, HORIZONTAL);
                int max2 = Math.max(HORIZONTAL, VERTIKAL);
                int max = Math.max(max1, max2);

                if (Objects.equals(mode, "local") && max <= 0) {
                    matrixScore[i][j] = 0;
                    tracebackMatrix[i][j] = null;
                } else {
                    if (max == DIAGONAL) {
                        matrixScore[i][j] = DIAGONAL;
                        tracebackMatrix[i][j] = "d";
                    } else if (max == HORIZONTAL) {
                        matrixScore[i][j] = HORIZONTAL;
                        tracebackMatrix[i][j] = "l";
                    } else {
                        matrixScore[i][j] = VERTIKAL;
                        tracebackMatrix[i][j] = "o";
                    }
                }

            }
        }
    }

    public int[] startAndScore(String mode){
        int score = 0;
        int seq2Index = -1;
        int seq1Index = -1;

        //needlemanWunsch
        if(mode.equals("global")){
            seq1Index = seq1.length();
            seq2Index = seq2.length();
            score = matrixScore[seq1.length()][seq2.length()];
            int[] positionAndScore = {seq1Index, seq2Index, score};
            return positionAndScore;

            //smithWaterman
        } else if(mode.equals("local")){
            for(int i = 0; i< seq1.length()+1; i++){
                for(int j = 0; j < seq2.length()+1; j++){
                    if(score<matrixScore[i][j]){
                        score = matrixScore[i][j];
                        seq1Index = i;
                        seq2Index = j;
                    }
                }
            }
            int[] positionAndScore = {seq1Index, seq2Index, score};
            return positionAndScore;

            //freeshift
        } else {
            score = 0;
            for(int i =0; i<= seq2.length(); i++){
                if(matrixScore[seq1.length()][i] > score){
                    score = matrixScore[seq1.length()][i];
                    seq1Index = seq1.length();
                    seq2Index = i;
                }
            }
            for(int j =0; j<= seq1.length(); j++){
                if(matrixScore[j][seq2.length()] >= score){
                    score = matrixScore[j][seq2.length()];
                    seq1Index = j;
                    seq2Index = seq2.length();
                }
            }
            int[] positionAndScore = {seq1Index, seq2Index, score};
            return positionAndScore;
        }
    }
    //for paddings
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

    //return right alignments from traceback
    public String[] generateAlignments(String mode, int i, int j){

        StringBuilder alignedSeq1 = new StringBuilder();
        StringBuilder alignedSeq2 = new StringBuilder();

        //padding back for local and freeshift
        if(!mode.equals("global")){
            alignedSeq1.append(generatePadding(seq1.length(), seq2.length(), i, j)[0]);
            alignedSeq2.append(generatePadding(seq1.length(), seq2.length(), i, j)[1]);
        }
        while (i > 0 && j > 0) {

            if (mode.equals("local") && matrixScore[i][j] <= 0) break;

            if (Objects.equals(tracebackMatrix[i][j], "d")) {
                alignedSeq1.insert(0, seq1.charAt(i-1));
                alignedSeq2.insert(0, seq2.charAt(j-1));
                i--;
                j--;
            } else if (Objects.equals(tracebackMatrix[i][j], "o")) {
                alignedSeq1.insert(0, seq1.charAt(i-1));
                alignedSeq2.insert(0, "-");
                i--;
            } else {
                alignedSeq1.insert(0, "-");
                alignedSeq2.insert(0, seq2.charAt(j-1));
                j--;
            }
        }
        while (j>0){
            alignedSeq1.insert(0, "-");
            alignedSeq2.insert(0, seq2.charAt(j-1));
            j--;
        }
        while (i>0){
            alignedSeq1.insert(0, seq1.charAt(i-1));
            alignedSeq2.insert(0, "-");
            i--;
        }
        //padding front local and freeshift
        if(!mode.equals("global")){
            alignedSeq1.insert(0, generatePadding(i, j, 1, 1)[0]);
            alignedSeq2.insert(0, generatePadding(i, j, 1, 1)[1]);
        }
        //builder to string and return
        String alignedSeq1Str = alignedSeq1.toString();
        String alignedSeq2Str = alignedSeq2.toString();
        String[] both = {alignedSeq1Str, alignedSeq2Str};
        return both;
    }
    public int[][] getMatrix(){
        return matrixScore;
    }
    public String[][] getTraceback(){
        return tracebackMatrix;
    }

    public Alignment alignGlobal() {
        String mode = "global";
        // matrixinit and scoring
        matrixInit(mode);
        int[] startAndScore = startAndScore(mode);
        int start = startAndScore[0];
        int stop = startAndScore[1];
        score = startAndScore[2];

        //for alignments traceback
        String[] bothAlignments = generateAlignments(mode, start, stop);
        Alignment nmw = new Alignment(bothAlignments[0], bothAlignments[1], seqID1, seqID2, score);
        return nmw;
    }

    @Override
    public Alignment alignLocal() {
        String mode = "local";

        //matrix and Position
        matrixInit(mode);
        int[] startAndScore = startAndScore(mode);
        int start = startAndScore[0];
        int stop = startAndScore[1];
        score = startAndScore[2];


        //for alignments traceback
        String[] bothAlignments = generateAlignments(mode, start, stop);
        Alignment swm = new Alignment(bothAlignments[0], bothAlignments[1], seqID1, seqID2, score);
        return swm;
    }

    @Override
    public Alignment alignFreeShift() {
        String mode = "freeshift";

        //for alignment traceback
        matrixInit(mode);
        int[] startAndScore = startAndScore(mode);
        int start = startAndScore[0];
        int stop = startAndScore[1];
        score = startAndScore[2];

        //for alignments traceback
        String[] bothAlignments = generateAlignments(mode, start, stop);
        Alignment freeshift = new Alignment(bothAlignments[0], bothAlignments[1], seqID1, seqID2, score);
        return freeshift;
    }

    @Override
    public String matrixesToString() {
        StringBuilder scoreMatrix = new StringBuilder();
        scoreMatrix.append("Score Matrix: \n");
        for (int[] row : matrixScore) {
            for (int value : row) {
                scoreMatrix.append((value/100.0) + " ");
            }
            scoreMatrix.append("\n");
        }
        scoreMatrix.append("\n");
        return scoreMatrix.toString();
    }
}