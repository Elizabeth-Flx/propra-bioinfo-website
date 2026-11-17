package AlignmentAlgorithms;

import Enums.Format;
import Enums.Mode;
import org.json.JSONObject;
import picocli.CommandLine;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class ValidationMain implements Runnable {
    @CommandLine.Option(names = "-f", required = true)
    File pairfile;

    public static void main(String[] args) {
        new CommandLine(new ValidationMain()).execute(args);
    }

    public void run() {


        try {
            Scanner myReader = new Scanner(pairfile);
            while (myReader.hasNextLine()) {
                String headerScore = myReader.nextLine();
                String candSeqAndID1 = myReader.nextLine();
                String[] candSeq1 = candSeqAndID1.split(" ");
                String candSeqAndID2 = myReader.nextLine();
                String[] candSeq2 = candSeqAndID2.split(" ");
                String refSeqAndID1 = myReader.nextLine();
                String[] refSeq1 = refSeqAndID1.split(" ");
                String refSeqAndID2 = myReader.nextLine();
                String[] refSeq2 = refSeqAndID2.split(" ");


                Alignment candidate = new Alignment(candSeq1[1], candSeq2[1], "", "", -1);
                Alignment reference = new Alignment(refSeq1[1], refSeq2[1], "", "", -1);
                //Metrics2 error = new Metrics2();

                String alignmentSensitivity = String.format("%.4f", Metrics.calcSensitivity(reference, candidate));
                String alignmentSpecitivity = String.format("%.4f", Metrics.calcSpecificity(reference, candidate));
                String alignmentMSE = String.format("%.4f", Metrics.calcMSE(reference, candidate));
                String alignmentInvMSE = String.format("%.4f", Metrics.calcInvMSE(reference, candidate));
                String alignmentCoverage = String.format("%.4f", Metrics.calcCoverage(reference, candidate));

                String headerAndScore = headerScore + " " + alignmentSensitivity + " " + alignmentSpecitivity + " " + alignmentCoverage+ " " + alignmentMSE + " " + alignmentInvMSE;
                System.out.println(headerAndScore + "\n" + candSeqAndID1 + "\n" + candSeqAndID2 + "\n" + refSeqAndID1 + "\n" + refSeqAndID2);
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("Exception while trying to extract information.");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject createJSON(Aligner aligner, Alignment alignment) {
        JSONObject json = new JSONObject();
        json.put("seq1", aligner.seq1);
        json.put("seq2", aligner.seq2);
        json.put("seqID1", aligner.seqID1);
        json.put("seqID2", aligner.seqID2);
        json.put("align1", alignment.seq1);
        json.put("align2", alignment.seq2);

        json.put("score", alignment.score);
        json.put("matches", alignment.countMatches());

        if (aligner instanceof NWSW nwsw) {
            int[] array1D = Arrays.stream(nwsw.matrixScore)
                    .flatMapToInt(Arrays::stream)
                    .toArray();
            json.put("scoreMatrixNWSW", array1D);
        } else if (aligner instanceof  Gotoh gotoh) {
            int[] array1D = Arrays.stream(gotoh.getMatrixA())
                    .flatMapToInt(Arrays::stream)
                    .toArray();
            json.put("scoreMatrixGotoh", array1D);
            array1D = Arrays.stream(gotoh.getMatrixI())
                    .flatMapToInt(Arrays::stream)
                    .toArray();
            json.put("insertionMatrixGotoh", array1D);
            array1D = Arrays.stream(gotoh.getMatrixD())
                    .flatMapToInt(Arrays::stream)
                    .toArray();
            json.put("deletionMatrixGotoh", array1D);
        }
        return json;
    }

}

