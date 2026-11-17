package Recursive;

import AlignmentAlgorithms.NWSW;
import AlignmentAlgorithms.SubstitutionMatrix;
import picocli.CommandLine;
import java.util.Random;

public class MainRecursive implements Runnable {

    @CommandLine.Option(names = "--ge", required = false, defaultValue = "-2")
    int ge;

    @CommandLine.Option(names = "--seqlength", required = true)
    int seqLen;

    @CommandLine.Option(names = "--nTests", required = true)
    int nTests;

    @CommandLine.Option(names = "-m", required = true)
    String matrixname;

    public static void main(String[] args) {
        new CommandLine(new MainRecursive()).execute(args);
    }

    public void run() {
        SubstitutionMatrix matrix = new SubstitutionMatrix(matrixname);

        NWSW nwsw;
        NWRec nwRec;
        String s1, s2;

        long timeNWSW = 0;
        long timeNWRec = 0;

        long startTime, endTime;

        for (int i = 0; i < nTests; i++) {
            if (i % (nTests / 5) == 0) {
                System.out.println((i / (nTests / 5) * 20) + "% ");
            }
            s1 = generateRandomSequence(seqLen);
            s2 = generateRandomSequence(seqLen);

            // Test NW dynamic
            startTime = System.currentTimeMillis();

            nwsw = new NWSW(s1, s2, "", "", matrix, ge, ge);
            nwsw.matrixInit("global");
            int scoreD = nwsw.getMatrix()[s1.length()][s2.length()];

            endTime = System.currentTimeMillis();
            timeNWSW += endTime - startTime;

            // Test NW recursive
            startTime = System.currentTimeMillis();

            nwRec = new NWRec(s1, s2, ge, matrix);
            int scoreR = nwRec.startRec();

            endTime = System.currentTimeMillis();
            timeNWRec += endTime - startTime;
        }

        System.out.println("Total time dynamic:   " + timeNWSW + "ms");
        System.out.println("Total time recursive: " + timeNWRec + "ms");
        System.out.println();
        System.out.println("Average time dynamic:   " + (int) (timeNWSW / nTests + .5) + "ms");
        System.out.println("Average time recursive: " + (int) (timeNWRec / nTests + .5) + "ms");
    }

    public String generateRandomSequence(int length) {
        Random random = new Random();
        String[] bases = {"A", "C", "G", "T"};

        StringBuilder sequenceBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(bases.length);
            String randomBase = bases[randomIndex];
            sequenceBuilder.append(randomBase);
        }
        return sequenceBuilder.toString();
    }
}
