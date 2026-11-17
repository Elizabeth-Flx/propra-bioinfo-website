package org.propra13.HelperClasses.Alignment;

import java.util.ArrayList;

public class Metrics {

    public Alignment candidate;
    public Alignment reference;
    protected int allMatchesCandidate;
    int trueMatchesCandidate;
    int allMatchesReference;

    public Metrics(Alignment candidate, Alignment reference) {
        this.candidate = candidate;
        this.reference = reference;
    }

    public static void main(String[] args) {
        Alignment cand = new Alignment(
                "----------RRNLMIVDGTNLGFR-------------------FPFASSYVSTIQSLAKSYSARTTIVLGDKGKSVFRLEHLPEY---------AF---FEYLKDAFELCKTTFPTFTIRGVEADDMAAYIVKLIGHLYDHVWLISTDGDWDTLLTDKVSRF---SFTTRREYHLRDMYEHHNVDDVEQFISLKAIMGDLGDNIRGVEGIGAKRGYNIIREFGN---VLDIIDQLPLPGKQKYIQNLNASEELLFRNL--ILVDLPTYCVDAIAAVGQDVLDKFTKDILEIAE--------",
                "MRGMLPLFEPKGRVLLVDGHHLAYRTFHALKGLTTSRGEPVQAVYGFAKSLLKALKE-----DGDAVIVVFDAKAPSFRHEAYGGYKAGRAPTPEDFPRQLALIKELVDL--LGLARLEVPGYEADDVLASLAKKAEKEGYEVRILTADKDLYQLLSDRIHVLHPEGYLITPAW-LWEKYGLRP----DQWADYRALTGDESDNLPGVKGIGEKTARKLLEEWGSLEALLKNLDRLKPAIREKILAHMDDLK--LSWDLAKVRTDLP---LEVDFAKRREPDRERLRAFLERLEFGSLLHEF",
                "", "1wq2B00", 999999);
        Alignment ref = new Alignment(
                "----------RRNLMIVDGTNLGFRFP--------------FASSYVSTIQSLAKSYSARTTIVLGDKG-KSVFRLEHLP--------EYAFFEYLKDAFELCKT-TFPTFTIRGVEADDMAAYIVKLIGHLYDHVWLISTDGDWDTLLTDKVSRFSFTTRREYHLRDMYEHHNVDDVEQFISLKAIMGDLGDNIRGVEG----IGAKRGYNIIREFGNVLDIIDQLPLPGKQKYIQNLNASEELLFRNLILVDLPTYCVDAIAAVG--------QDVLDKFTKDILEIAE-----",
                "MRGMLPLFEPKGRVLLVDGHHLAYRTFHALKGLTTSRGEPVQAVYGFAKSLLKALKEDGDAVIVVFDAKAPSFRHEAYGGYKAGRAPTPEDFPRQLALIKELVDLLGLARLEVPGYEADDVLASLAKKAEKEGYEVRILTADKDLYQLLSDRIHVLHPEG-YLITPAWLWEKYGL-RPDQWADYRALTGDESDNLPGVKGIGEKTARKLLEEWG-SLEALL---KNLD-RLKPAIREKILAHMDDLKLSWDLAKVRT---DLPLEVDFAKRREPDRERLRAFLERL-EFGSLLHEF",
                "1j2xA00", "1wq2B00", 999999);

        Metrics n = new Metrics(cand, ref);

        System.out.println(n.sensitivity());
        System.out.println(n.specitivity());
        System.out.println(n.calcMSE(ref, cand));
        System.out.println(n.calcInvMSE());
    }

    public ArrayList<Integer>[] calculateCharacterIndexes(Alignment align) {
        ArrayList<Integer> characterIndexes1 = new ArrayList<>();
        ArrayList<Integer> characterIndexes2 = new ArrayList<>();
        for (int i = 0; i < align.seq1.length(); i++) {
            if (align.seq1.charAt(i) != '-') {
                characterIndexes1.add(i);
            }
            if (align.seq2.charAt(i) != '-') {
                characterIndexes2.add(i);
            }
        }
        ArrayList<Integer>[] vals = new ArrayList[2];
        vals[0] = characterIndexes1;
        vals[1] = characterIndexes2;
        return vals;
    }

    public double calcMSE(Alignment reference, Alignment candidate) {
        ArrayList<Integer>[] referenceIndexes = calculateCharacterIndexes(reference);
        ArrayList<Integer>[] candidateIndexes = calculateCharacterIndexes(candidate);
        int sum = 0;
        for (int k = 0; k < Math.max(referenceIndexes[0].size(), referenceIndexes[1].size()); k++) {
            int i = referenceIndexes[0].indexOf(k);
            int j = referenceIndexes[1].indexOf(k);

            if (i != -1 && j != -1) {
                sum += Math.abs(candidateIndexes[0].get(i) - candidateIndexes[1].get(j));
            }
        }
        return sum / (double) allMatchesReference;
    }

    public double calcInvMSE() {
        ArrayList<Integer>[] referenceIndexes = calculateCharacterIndexes(reference);
        ArrayList<Integer>[] candidateIndexes = calculateCharacterIndexes(candidate);
        int sum = 0;
        for (int k = 0; k < Math.max(referenceIndexes[0].size(), referenceIndexes[1].size()); k++) {
            int i = referenceIndexes[0].indexOf(k);
            int j = referenceIndexes[1].indexOf(k);

            if (i != -1 && j != -1) {
                sum += Math.abs(candidateIndexes[0].get(i) - candidateIndexes[1].get(j));
            }
        }
        System.out.println(sum);
        System.out.println(allMatchesCandidate);
        return sum / (double) allMatchesCandidate;
    }

    public int getTrueMatchesCandidate() {
        trueMatchesCandidate = 0;
        String candidateSeq1 = candidate.seq1;
        String candidateSeq2 = candidate.seq2;
        String referenceSeq1 = reference.seq1;
        String referenceSeq2 = reference.seq2;

        // getting trueMatchesCandidate
        ArrayList<Integer> candseq1Indices = new ArrayList<>();
        ArrayList<Integer> candseq2Indices = new ArrayList<>();
        ArrayList<Integer> refseq1Indices = new ArrayList<>();
        ArrayList<Integer> refseq2Indices = new ArrayList<>();

        //to lists of indices where
        for (int i = 0; i < candidateSeq1.length(); i++) {
            char cCan1 = candidateSeq1.charAt(i);
            char cCan2 = candidateSeq2.charAt(i);
            if (cCan1 != '-') {
                candseq1Indices.add(i);
            }
            if (cCan2 != '-') {
                candseq2Indices.add(i);
            }
        }
        for (int i = 0; i < referenceSeq1.length(); i++) {
            char cRef1 = referenceSeq1.charAt(i);
            char cRef2 = referenceSeq2.charAt(i);
            if (cRef2 != '-') {
                refseq2Indices.add(i);
            }
            if (cRef1 != '-') {
                refseq1Indices.add(i);
            }
        }

        //suche jew chars
        for (int charInt1 = 0; charInt1 < candidateSeq1.length(); charInt1++) {
            //if(candidateSeq1.charAt(i) != '-' && candidateSeq2.charAt(i) != '-'){
            int i = candseq1Indices.indexOf(charInt1);
            int j = candseq2Indices.indexOf(charInt1);

            // wenn gleich: score erhöhen
            try {
                if (refseq1Indices.get(i) == refseq2Indices.get(j)) {
                    trueMatchesCandidate += 1;
                    //System.out.println(candidateSeq1.charAt(refseq1Indices.get(i)) + " " +  candidateSeq2.charAt(refseq2Indices.get(j)));
                }
            } catch (Exception e) {

            }
        }
        return trueMatchesCandidate;
    }

    public double sensitivity() {
        // all initialisation
        allMatchesCandidate = 0;
        trueMatchesCandidate = getTrueMatchesCandidate();
        String candidateSeq1 = candidate.seq1;
        String candidateSeq2 = candidate.seq2;

        //getting allMatchesCandidate
        for (int i = 0; i < candidateSeq1.length(); i++) {
            if (candidateSeq1.charAt(i) != '-' && candidateSeq2.charAt(i) != '-') {
                allMatchesCandidate += 1;
            }
        }
        return trueMatchesCandidate / (double) allMatchesCandidate;
    }

    public double specitivity() {
        // all initialisation
        allMatchesReference = 0;
        trueMatchesCandidate = getTrueMatchesCandidate();
        String referenceSeq1 = reference.seq1;
        String referenceSeq2 = reference.seq2;

        //getting allMatchesReference
        for (int i = 0; i < referenceSeq1.length(); i++) {
            if (referenceSeq1.charAt(i) != '-' && referenceSeq2.charAt(i) != '-') {
                allMatchesReference += 1;
            }
        }
        return trueMatchesCandidate / (double) allMatchesReference;
    }

    public double mse() {

        return 0;
    }

    public double invertedMse() {

        return 0;
    }
}
