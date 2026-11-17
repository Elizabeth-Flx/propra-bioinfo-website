package AlignmentAlgorithms;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Metrics {

    private static ArrayList<Integer>[] calculateCharacterIndexes(Alignment align) {
        ArrayList<Integer> characterIndexes1 = new ArrayList<>();
        ArrayList<Integer> characterIndexes2 = new ArrayList<>();
        for (int i = 0; i < align.seq1.length(); i++) {
            if (align.seq1.charAt(i) != '-') characterIndexes1.add(i);
            if (align.seq2.charAt(i) != '-') characterIndexes2.add(i);
        }
        ArrayList<Integer>[] vals = new ArrayList[2];
        vals[0] = characterIndexes1;
        vals[1] = characterIndexes2;
        return vals;
    }

    public static double calcMSE(Alignment reference, Alignment candidate) {
        ArrayList<Integer>[] referenceIndexes = calculateCharacterIndexes(reference);
        ArrayList<Integer>[] candidateIndexes = calculateCharacterIndexes(candidate);
        int count = 0;
        int sum = 0;

        for (int i = 0; i <= Math.max(referenceIndexes[0].get(referenceIndexes[0].size() - 1), referenceIndexes[1].get(referenceIndexes[1].size() - 1)); i++) {
            int refSeq1Pos = referenceIndexes[0].indexOf(i);
            int refSeq2Pos = referenceIndexes[1].indexOf(i);
            if (refSeq1Pos != -1 && refSeq2Pos != -1) {

                int canAlign2Pos = candidateIndexes[1].get(refSeq2Pos);
                if (candidateIndexes[0].indexOf(canAlign2Pos) != -1) {
                    count++;
                    sum += Math.abs(candidateIndexes[0].indexOf(canAlign2Pos) - refSeq1Pos);
                }
            }
        }
        return sum / (double) count;
    }

    public static double calcInvMSE(Alignment reference, Alignment candidate) {
        Alignment r = new Alignment(reference.seq2, reference.seq1, "", "", 0);
        Alignment c = new Alignment(candidate.seq2, candidate.seq1, "", "", 0);
        return calcMSE(r, c);
    }

    public static int countCorrectMatches(Alignment reference, Alignment candidate) {
        int correctMatches = 0;
        ArrayList<Integer>[] referenceIndexes = calculateCharacterIndexes(reference);
        ArrayList<Integer>[] candidateIndexes = calculateCharacterIndexes(candidate);

        for (int i = 0; i < reference.seq1.length(); i++) {
            int refSeq1Pos = referenceIndexes[0].indexOf(i);
            int refSeq2Pos = referenceIndexes[1].indexOf(i);
            if (refSeq1Pos != -1 && refSeq2Pos != -1) {
                try {
                    if (candidateIndexes[0].get(refSeq1Pos) == candidateIndexes[1].get(refSeq2Pos)) {
                        correctMatches++;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }
        return correctMatches;
    }

    public static int countMatches(Alignment align) {
        int count = 0;
        for (int i = 0; i < align.seq1.length(); i++) {
            if (align.seq1.charAt(i) != '-' && align.seq2.charAt(i) != '-') count++;
        }
        return count;
    }

    public static int countCorrectMatches2(Alignment reference, Alignment candidate) {
        ArrayList<Integer>[] referenceIndexes = calculateCharacterIndexes(reference);
        ArrayList<Integer>[] candidateIndexes = calculateCharacterIndexes(candidate);
        int count = 0;
        int sum = 0;

        for (int i = 0; i <= Math.max(referenceIndexes[0].get(referenceIndexes[0].size() - 1), referenceIndexes[1].get(referenceIndexes[1].size() - 1)); i++) {
            int refSeq1Pos = referenceIndexes[0].indexOf(i);
            int refSeq2Pos = referenceIndexes[1].indexOf(i);
            if (refSeq1Pos != -1 && refSeq2Pos != -1) {

                int canAlign2Pos = candidateIndexes[1].get(refSeq2Pos);
                if (candidateIndexes[0].indexOf(canAlign2Pos) != -1 && (candidateIndexes[0].indexOf(canAlign2Pos) - refSeq1Pos) ==0) {
                    count++;
                }
            }
        }
        return count;
    }

    public static double calcSensitivity(Alignment reference, Alignment candidate) {
        return countCorrectMatches2(reference, candidate) / (double) countMatches(reference);
    }

    public static double calcSpecificity(Alignment reference, Alignment candidate) {
        return countCorrectMatches2(reference, candidate) / (double) countMatches(candidate);
    }

    public static double calcCoverage(Alignment reference, Alignment candidate) {
        int overlappingMatches = 0;
        ArrayList<Integer>[] referenceIndexes = calculateCharacterIndexes(reference);
        ArrayList<Integer>[] candidateIndexes = calculateCharacterIndexes(candidate);
        for(int i = 0; i< referenceIndexes[1].size(); i++){
            int refTargetIndex = referenceIndexes[1].get(i);
            int canTargetIndex = candidateIndexes[1].get(i);
            if(referenceIndexes[0].indexOf(refTargetIndex) != -1 && candidateIndexes[0].indexOf(canTargetIndex) != -1){
                overlappingMatches++;
            }
        }
        return overlappingMatches / (double) countMatches(candidate);
    }
    public static double calcSeqIdentity(Alignment reference){
        int sameCharacter = 0;
        for (int i = 0; i< reference.seq1.length(); i++){
            if (reference.seq1.charAt(i) == reference.seq2.charAt(i)){
                sameCharacter++;
            }
        }
        return (double) sameCharacter/ reference.seq1.length();
    }

    public static double calcSeqSimilarity(Alignment reference){
        return (double) countMatches(reference)/reference.seq1.length();
    }

    public static Double findAverage(ArrayList<Double> nums) {
        return nums.stream().mapToDouble(val -> val).filter(val -> !Double.isNaN(val)).average().orElseThrow();
    }

    //public static double roundTo4(double value){
    //    DecimalFormat df = new DecimalFormat("#.####");
    //    double rounded = (double) Double.parseDouble(df.format(value));
    //    return rounded;
    //}
    public static void main(String[] args) {
//        String cand1 = "--GDCCIIRVSLDV-DNGNMYKSILVTSQDKAPTVIRKAMDKHNLDEDEPEDYELLQIISEDHKLKIPENANVFYAMNSAANYDFILKKR-";
//        String cand2 = "GSSDCRIIRVQMELGEDGSVYKSILVTSQDKAPSVISRVLKKNNRDSAVASEFELVQLLPGDRELTIPHSANVFYAMDGASH-DFLLRQRR";
//        String ref1 = "G--DCCIIRVSLDVDNGN-MYKSILVTSQDKAPTVIRKAMDKHNLDEDEPEDYELLQIISEDHKLKIPENA--NVFYAMNSAANYDFILKKR-";
//        String ref2 = "GSSDCRIIRVQMELGEDGSVYKSILVTSQDKAPSVISRVLKKNNRDSAVASEFELVQLL--PGDRELTIPHSANVFYAMDG-ASHDFLLRQRR";
        String cand1 =  "GPLDVQVTEDAVRRYLTRKPMTTKDLLKKFQTKKTGLSSEQTVNVLA";
        String cand2 = "----MEEAKQKVVDFLNSKSK-SKFYFNDFTDLFPDMKQREVKKILTA";
        String ref1 = "GPLDVQVTEDAV-RRYLTRKPMTTK-DLLKKFQTKK-TGLSSEQTVNVLAQ";
        String ref2 = "----MEEAKQKVVDF-LNS-KSK-SKFYFNDFTDLFPDMKQR-EVKKILTA";

        Alignment cand = new Alignment(cand1, cand2, "", "", 0);
        Alignment ref = new Alignment(ref1, ref2, "", "", 0);

        //System.out.println(Metrics.calcSensitivity(ref, cand));
        //System.out.println(Metrics.calcSpecificity(ref, cand));
        //System.out.println(Metrics.calcMSE(ref, cand));
        //System.out.println(Metrics.calcInvMSE(ref, cand));
        //System.out.println(Metrics.calcCoverage(ref, cand));
        //System.out.println(ref.seq1.length());
        //System.out.println(countMatches(ref));
        //System.out.println(Metrics.calcSeqIdentity(ref));
        //System.out.println(Metrics.calcSeqSimilarity(cand));

    }
}