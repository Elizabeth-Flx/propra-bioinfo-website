package org.propra13.HelperClasses.Alignment;

public class Alignment {
    public String seq1;
    public String seq2;
    public String seqID1;
    public String seqID2;
    public double score;

    public Alignment(String seq1, String seq2, String seqID1, String seqID2, int score) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.seqID1 = seqID1;
        this.seqID2 = seqID2;
        this.score = score / 100.0;
    }

    public String toStringScore() {
        String s = seqID1 + " " + seqID2 + " " + String.format("%.4f", score);
        return s;
    }

    public String toStringAli() {
        String s = ">" + toStringScore() + "\n";
        s += seqID1 + ": " + seq1 + "\n";
        s += seqID2 + ": " + seq2;
        return s;
    }

    public int countMatches() {
        int matches = 0;
        for (int i = 0; i < seq1.length(); i++) {
            if (seq1.charAt(i) == seq2.charAt(i)) {
                matches++;
            }
        }
        return matches;
    }
}
