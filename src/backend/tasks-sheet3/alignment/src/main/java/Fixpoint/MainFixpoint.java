package Fixpoint;

import AlignmentAlgorithms.SeqIdToSeq;
import AlignmentAlgorithms.SubstitutionMatrix;
import Enums.Mode;
import picocli.CommandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainFixpoint implements Runnable {

    @CommandLine.Option(names = "--go", required = false, defaultValue = "-12")
    int go;

    @CommandLine.Option(names = "--ge", required = false, defaultValue = "-1")
    int ge;

    @CommandLine.Option(names = "--pairs", required = true, description = "Specifies what sequences are used (only uses first 2)")
    File pairfile;

    @CommandLine.Option(names = "--seqlib", required = true)
    File seqlibfile;

    @CommandLine.Option(names = "-m", required = true)
    String matrixname;

    @CommandLine.Option(names = {"--mode"}, required = true, description = "Specifies the mode (local|global|freeshift)")
    Mode mode;

    @CommandLine.Option(names = {"--midpoint"}, required = false, defaultValue = "0.7", description = "Specifies midpoint of the heatmap")
    double midpoint;

    @CommandLine.Option(names = {"--output"}, required = true, description = "Specifies the output path for the heatmap")
    String output;


    public static void main(String[] args) {
        new CommandLine(new MainFixpoint()).execute(args);
    }

    public void run() {
        try {
            SeqIdToSeq sequences = new SeqIdToSeq(seqlibfile);
            SubstitutionMatrix matrix = new SubstitutionMatrix(matrixname);

            Scanner myReader = new Scanner(pairfile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] ids = data.split(" ");
                String seq1id = ids[0];
                String seq2id = ids[1];

                String seq1 = sequences.getSeq(seq1id);
                String seq2 = sequences.getSeq(seq2id);

                Fixpoint fpoint = new Fixpoint(go, ge, seq1, seq2, matrix);
                double[][] scores = fpoint.calcAllFixpointScores(mode);

                fpoint.saveHeatMap(scores, output, midpoint);
                break;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exception while trying to extract information.");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
