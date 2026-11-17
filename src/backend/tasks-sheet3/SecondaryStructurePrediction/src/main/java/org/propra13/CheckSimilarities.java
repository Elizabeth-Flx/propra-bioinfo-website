package org.propra13;

import org.propra13.HelperClasses.Alignment.Alignment;
import org.propra13.HelperClasses.Alignment.Gotoh;
import org.propra13.HelperClasses.Alignment.SubstitutionMatrix;
import org.propra13.HelperClasses.Enums.AminoAcid;
import org.propra13.HelperClasses.Sequence.Sequence;
import org.propra13.HelperClasses.Sequence.SequenceSecondary;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.propra13.HelperClasses.HelpMethods.loadSequencesFromFasta;
import static org.propra13.HelperClasses.HelpMethods.loadSequencesFromSeqLib;

@CommandLine.Command(name = "CheckSimilarities", mixinStandardHelpOptions = true)
public class CheckSimilarities implements Runnable {
    @CommandLine.Option(names = {"--seq"}, required = true, description = "Fasta file")
    File seq;

    @CommandLine.Option(names = {"--exclude"}, required = true, description = "Sequences to exclude")
    File exclude;

    @CommandLine.Option(names = {"--matrix"}, required = true, description = "Substitution matrix")
    File matrix;

    @CommandLine.Option(names = {"--output"}, required = true, description = "Output file")
    File output;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new CheckSimilarities()).execute(args);
        System.exit(exitCode);
    }

    private boolean matches(Sequence sequence, SequenceSecondary excludeSequence) {
        Gotoh gotoh = new Gotoh(sequence.getSequence().stream().map(String::valueOf).collect(Collectors.joining()),
                excludeSequence.getSequence().stream().map(String::valueOf).collect(Collectors.joining()),
                sequence.getName(), excludeSequence.getName(), new SubstitutionMatrix(matrix.getAbsolutePath()), -10,
                -1);
        Alignment alignment = gotoh.alignLocal();
        return alignment.score / alignment.seq1.length() > 0.4;
    }

    @Override
    public void run() {
        try {
            Collection<Sequence> sequences = loadSequencesFromFasta(seq);
            Collection<SequenceSecondary> excludeSequences = loadSequencesFromSeqLib(exclude);

            List<Sequence> filtered = sequences.parallelStream().filter(sequence -> .15 >
                    (double) sequence.getSequence().stream().filter(AminoAcid.nonCanonical::contains).count() /
                            sequence.length()).filter(sequence -> excludeSequences.stream().noneMatch(
                    excludeSequence -> matches(sequence, excludeSequence))).toList();

            System.out.println("Filtered " + sequences.size() + " sequences to " + filtered.size() + " sequences.");

            try (var writer = new BufferedWriter(new FileWriter(output))) {
                for (Sequence sequence : filtered) {
                    writer.write(sequence.toString());
                    writer.newLine();
                    writer.newLine();
                }
            }
            System.out.println("Wrote " + filtered.size() + " sequences to " + output.getAbsolutePath() + ".");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
