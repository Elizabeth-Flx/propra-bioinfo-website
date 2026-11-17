package AlignmentAlgorithms;

import Enums.Mode;
import picocli.CommandLine;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DataAnalysisMain implements Runnable {

    @CommandLine.Option(names = "--go", required = false, defaultValue = "-12")
    int gapopen;

    //@CommandLine.Option(names = "--ge", required = false, defaultValue = "-1")
    //int gapextend;

    @CommandLine.Option(names = "--pairs", required = true)
    File pairfile;

    @CommandLine.Option(names = "--seqlib", required = true)
    File seqlibfile;

    @CommandLine.Option(names = "-m", required = false)
    String matrixname;

    @CommandLine.Option(names = {"--mode"}, required = true, description = "Specifies the mode (local|global|freeshift)")
    Mode mode;

    @CommandLine.Option(names = "--nw", required = false)
    boolean nw;

    @CommandLine.Option(names = "--outfile", required = false)
    File outFileDirectory;


    public static void main(String[] args) {
        new CommandLine(new DataAnalysisMain()).execute(args);
    }

    public void run() {
        try {
            SeqIdToSeq sequences = new SeqIdToSeq(seqlibfile);
            SubstitutionMatrix matrix = new SubstitutionMatrix(matrixname);

            BufferedWriter writer = null;
            if (outFileDirectory != null) {
                writer = new BufferedWriter(new FileWriter(outFileDirectory + "\\NWFreeshiftAverage.txt"));
                writer.write("gapextend Sensitivity Specitivity Coverage MSE InvertedMSE "+ "\n");
            }
            //for range of parameter to investigate
            for (int i = 1; i <= 20; i++) {
                int gapextend = -i;
                Scanner myReader = new Scanner(pairfile);
                ArrayList<Double> allSens = new ArrayList<>();
                ArrayList<Double> allSpec = new ArrayList<>();
                ArrayList<Double> allCov = new ArrayList<>();
                ArrayList<Double> allMSE = new ArrayList<>();
                ArrayList<Double> allinvMSE = new ArrayList<>();
                while (myReader.hasNextLine()) {

                    String data = myReader.nextLine();
                    String[] ids = data.split(" ");
                    String seq1id = ids[0];
                    String seq2id = ids[1];

                    String seq1ForAlignment = sequences.getSeq(seq1id).replace("-", "");
                    String seq2ForAlignment = sequences.getSeq(seq2id).replace("-", "");

                    Aligner aligner = nw ? new NWSW(seq1ForAlignment, seq2ForAlignment, seq1id, seq2id, matrix, gapopen, gapextend) :
                            new Gotoh(seq1ForAlignment, seq2ForAlignment, seq1id, seq2id, matrix, gapopen, gapextend);

                    Alignment candidate = switch (mode) {
                        case global -> aligner.alignGlobal();
                        case local -> aligner.alignLocal();
                        case freeshift -> aligner.alignFreeShift();
                    };
                    Alignment reference = new Alignment(sequences.getSeq(seq1id), sequences.getSeq(seq2id), "", "", -1);

                    //all values to a list appending for each metric
                    Double alignmentSensitivity = Metrics.calcSensitivity(reference, candidate);
                    allSens.add(alignmentSensitivity);
                    Double alignmentSpecitivity = Metrics.calcSpecificity(reference, candidate);
                    allSpec.add(alignmentSpecitivity);
                    Double alignmentMSE = Metrics.calcMSE(reference, candidate);
                    allMSE.add(alignmentMSE);
                    Double alignmentInvMSE = Metrics.calcInvMSE(reference, candidate);
                    allinvMSE.add(alignmentInvMSE);
                    Double alignmentCoverage = Metrics.calcCoverage(reference, candidate);
                    allCov.add(alignmentCoverage);
                    Double seqIdentRef = Metrics.calcSeqIdentity(reference);
                    Double seqIdentCand = Metrics.calcSeqIdentity(candidate);
                    Double seqSimRef = Metrics.calcSeqSimilarity(reference);
                    Double seqSimCand = Metrics.calcSeqSimilarity(candidate);
                    //String headerAndScore = candidate.toStringScore() + " " + seq1ForAlignment.length() + " " + seq2ForAlignment.length() + " " + alignmentSensitivity + " " + alignmentSpecitivity + " " + alignmentCoverage + " " + alignmentMSE + " " + alignmentInvMSE + " " + seqIdentCand + " " + seqIdentRef + " " + seqSimCand + " " + seqSimRef;
                    //System.out.println(headerAndScore);
                }
                //List of list for all metrics
                String[] averageAllMet = new String[5];
                averageAllMet[0] = Metrics.findAverage(allSens).toString();
                averageAllMet[1] = Metrics.findAverage(allSpec).toString();
                averageAllMet[2] = Metrics.findAverage(allCov).toString();
                averageAllMet[3] = Metrics.findAverage(allMSE).toString();
                averageAllMet[4] = Metrics.findAverage(allinvMSE).toString();

                String entryAverages = averageAllMet[0] + " " + averageAllMet[1] + " " + averageAllMet[2] + " " + averageAllMet[3] + " " + averageAllMet[4];
                System.out.println(gapextend + " " + entryAverages);
                if (outFileDirectory != null) {
                    writer.write(gapextend + " " + entryAverages + "\n");
                }
                myReader.close();
            }
            if (outFileDirectory != null) writer.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
