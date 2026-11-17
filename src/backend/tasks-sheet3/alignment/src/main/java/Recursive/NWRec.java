package Recursive;

import AlignmentAlgorithms.SubstitutionMatrix;

public class NWRec {

    String seq1;
    String seq2;

    int go;

    SubstitutionMatrix subMatrix;

    public NWRec(String seq1, String seq2, int go, SubstitutionMatrix subMatrix) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.go = go*100;
        this.subMatrix = subMatrix;
    }

    public int recursiveNW(int i, int j) {

        int scoreMatMis, scoreLeft, scoreRight;
        // Base (left column and top row)
        if (i == -1) return go * (j+1);
        if (j == -1) return go * (i+1);

        scoreMatMis = recursiveNW(i-1, j-1) + subMatrix.getValue(seq1.charAt(i), seq2.charAt(j));
        scoreLeft   = recursiveNW(i, j-1) + go;
        scoreRight  = recursiveNW(i-1, j) + go;

        return Math.max(scoreMatMis, Math.max(scoreLeft, scoreRight));
    }
    public int startRec() {
        return recursiveNW(seq1.length()-1, seq2.length()-1);
    }
}
