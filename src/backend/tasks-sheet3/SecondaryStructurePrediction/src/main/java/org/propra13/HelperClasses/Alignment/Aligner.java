package org.propra13.HelperClasses.Alignment;

import org.propra13.HelperClasses.Enums.AminoAcid;

public abstract class Aligner {
    protected String seq1;
    protected String seq2;
    protected String seqID1;
    protected String seqID2;

    protected SubstitutionMatrix subMatrix;

    protected int go;
    protected int ge;

    public Aligner(String seq1, String seq2, String seqID1, String seqID2, SubstitutionMatrix subMatrix, int go,
                   int ge) {
        this.seq1 = removeNonCanonical(seq1);
        this.seq2 = removeNonCanonical(seq2);
        this.seqID1 = seqID1;
        this.seqID2 = seqID2;

        this.subMatrix = subMatrix;

        this.go = go;
        this.ge = ge;
    }

    private static String removeNonCanonical(String sequence) {
        for (AminoAcid aa : AminoAcid.nonCanonical) {
            sequence = sequence.replace(aa.toString(), "");
        }
        return sequence;
    }

    public abstract Alignment alignGlobal();

    public abstract Alignment alignLocal();

    public abstract Alignment alignFreeShift();

    public abstract String matrixesToString();

    public String[] getNonPaddedAlignment(Alignment alignment) {
        int start = 0;
        int end = 0;
        for (int i = alignment.seq1.length() - 1; i >= 0; i--) {
            if (alignment.seq1.charAt(i) != '-' && alignment.seq2.charAt(i) != '-') {
                end = i;
                break;
            }
        }
        for (int i = 0; i < alignment.seq1.length(); i++) {
            if (alignment.seq1.charAt(i) != '-' && alignment.seq2.charAt(i) != '-') {
                start = i;
                break;
            }
        }
        return new String[]{alignment.seq1.substring(start, end + 1), alignment.seq2.substring(start, end + 1)};
    }

    public double calcScore(Alignment alignment) {
        String[] a = getNonPaddedAlignment(alignment);
        String align1 = a[0];
        String align2 = a[1];

        int score = 0;
        boolean wasLastGap = false;
        for (int i = 0; i < align1.length(); i++) {
            char c1 = align1.charAt(i);
            char c2 = align2.charAt(i);
            if (c1 == '-' || c2 == '-') {
                score += ge;
                if (!wasLastGap && this instanceof Gotoh) {
                    score += go;
                    wasLastGap = true;
                }
            } else {
                score += subMatrix.getValue(c1, c2);
                wasLastGap = false;
            }
        }
        return score / 100.0;
    }
}
