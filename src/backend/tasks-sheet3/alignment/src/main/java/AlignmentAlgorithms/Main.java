package AlignmentAlgorithms;

import Enums.Format;
import Enums.Mode;
import org.json.JSONObject;
import picocli.CommandLine;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main implements Runnable {

    @CommandLine.Option(names = "--go", required = false, defaultValue = "-12")
    int gapopen;

    @CommandLine.Option(names = "--ge", required = false, defaultValue = "-1")
    int gapextend;

    @CommandLine.Option(names = "--dpmatrices", required = false)
    File directory;

    @CommandLine.Option(names = "--check", required = false)
    boolean check;

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

    @CommandLine.Option(names = {"--format"}, required = true, description = "Specifies the mode (scores|ali|html)")
    Format format;


    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }

    public void run() {
        try {
            SeqIdToSeq sequences = new SeqIdToSeq(seqlibfile);
            SubstitutionMatrix matrix = new SubstitutionMatrix(matrixname);

            BufferedWriter writer = null;
            if (directory != null) writer = new BufferedWriter(new FileWriter(directory + "\\alignmentMatrixes.txt"));

            Scanner myReader = new Scanner(pairfile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] ids = data.split(" ");
                String seq1id = ids[0];
                String seq2id = ids[1];

                String seq1 = sequences.getSeq(seq1id);
                String seq2 = sequences.getSeq(seq2id);

                Aligner aligner = nw ?  new  NWSW(seq1, seq2, seq1id, seq2id, matrix, gapopen, gapextend):
                                        new Gotoh(seq1, seq2, seq1id, seq2id, matrix, gapopen, gapextend);

                Alignment alignedSequences = switch (mode) {
                    case global     -> aligner.alignGlobal();
                    case local      -> aligner.alignLocal();
                    case freeshift  -> aligner.alignFreeShift();

                };
                switch (format) {
                    case scores -> System.out.println(alignedSequences.toStringScore());
                    case ali    -> System.out.println(alignedSequences.toStringAli());
                    case html   -> System.out.println(createJSON(aligner, alignedSequences).toString());
                }
                if (directory != null) {
                    writer.write(">" + seq1id + " " + seq2id + "\n");
                    writer.write( aligner.matrixesToString());
                }
            }
            myReader.close();
            if (directory != null) writer.close();

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
