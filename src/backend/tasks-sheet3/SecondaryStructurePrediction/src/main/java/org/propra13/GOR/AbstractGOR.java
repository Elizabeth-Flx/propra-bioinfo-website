package org.propra13.GOR;

import org.json.JSONArray;
import org.propra13.HelperClasses.Enums.AminoAcid;
import org.propra13.HelperClasses.Enums.SecondaryStructure;
import org.propra13.HelperClasses.InnerMatrix;
import org.propra13.HelperClasses.Sequence.Sequence;
import org.propra13.HelperClasses.Sequence.SequenceProbabilities;
import org.propra13.HelperClasses.Sequence.SequenceSecondary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.propra13.HelperClasses.HelpMethods.loadSequencesFromFasta;
import static org.propra13.HelperClasses.HelpMethods.loadSequencesFromSeqLib;

public abstract class AbstractGOR<T extends EnumMap<?, ?>> extends EnumMap<SecondaryStructure, T> {
    /**
     * The distance between the center of the window and the start/end of the window.
     */
    public static final int M = 8;

    protected static final double PSEUDO_COUNT = 0.001;

    /**
     * Trains a new GOR model from the sequences of a seclib-file.
     */
    public AbstractGOR(File source) throws IOException {
        this();
        loadSequencesFromSeqLib(source).forEach(this::add);
    }

    /**
     * Creates an empty GOR model.
     */
    protected AbstractGOR() {
        super(SecondaryStructure.class);
    }

    /**
     * Reads the model file and loads the counts from it.
     */
    public AbstractGOR(String content) {
        this();
        var blocks = new ArrayList<>(List.of(content.split("="))); // split the file into matrix blocks
        blocks.remove(0); // Remove the first element, which is useless or empty.


        IntStream.range(0, blocks.size() / 2).map(i -> i * 2).forEach(i -> {
            String descriptor = blocks.get(i); // get the descriptor of the matrix
            InnerMatrix innerMatrix = new InnerMatrix(blocks.get(i + 1)); // get the matrix

            add(descriptor, innerMatrix); // set the inner matrix for the sequence of keys given in the descriptor
        });
    }

    /**
     * Creates a new GOR model by summing up the counts of the given models.
     *
     * @param models The models to sum up.
     */
    public AbstractGOR(Collection<? extends AbstractGOR<T>> models) {
        this();
        for (SecondaryStructure structure : SecondaryStructure.getValues()) {
            put(structure, sum(models.stream().map(model -> model.get(structure)).collect(Collectors.toList())));
        }
    }

    /**
     * Writes the predictions to a unified output format.
     * and prints them to the console.
     *
     * @throws IOException If an I/O error occurs.
     */
    public static void writeOutput(List<SequenceProbabilities> sequenceProbabilitiesSequences, boolean probabilities)
            throws IOException {
        StringBuilder outputBuilder = new StringBuilder();
        for (SequenceProbabilities sequenceProbabilities : sequenceProbabilitiesSequences.stream().sorted(
                Comparator.comparing(Sequence::getName)).toList()) {
            outputBuilder.append(">").append(sequenceProbabilities.getName()).append("\n"); //Name
            outputBuilder.append("AS ").append(
                    sequenceProbabilities.getSequence().stream().map(AminoAcid::toString).collect(
                            Collectors.joining())).append("\n"); //Sequence
            outputBuilder.append("PS ");
            if (sequenceProbabilities.getSequence().size() > 16) {
                outputBuilder.append("--------").append(sequenceProbabilities.createPredictionSequence()).append(
                        "--------").append("\n"); //PSS-Sequence
            } else {
                outputBuilder.append("-".repeat(sequenceProbabilities.getSequence().size())).append("\n");
            }

            if (probabilities) {
                outputBuilder.append("PH ").append("00000000").append(
                        sequenceProbabilities.createProbabilitySequenceForSS(SecondaryStructure.H)).append(
                        "00000000").append("\n"); //PH-Sequence
                outputBuilder.append("PE ").append("00000000").append(
                        sequenceProbabilities.createProbabilitySequenceForSS(SecondaryStructure.E)).append(
                        "00000000").append("\n"); //PE-Sequence
                outputBuilder.append("PC ").append("00000000").append(
                        sequenceProbabilities.createProbabilitySequenceForSS(SecondaryStructure.C)).append(
                        "00000000").append("\n"); //PC-Sequence
            }

            outputBuilder.append("\n\n");
        }
        System.out.println(outputBuilder);
    }

    public static void writeJson(List<SequenceProbabilities> sequenceProbabilitiesSequences, boolean probabilities) {
        JSONArray json = new JSONArray();

        for (SequenceProbabilities sequenceProbabilities : sequenceProbabilitiesSequences.stream().sorted(
                Comparator.comparing(Sequence::getName)).toList()) {
            json.put(sequenceProbabilities.toJSON(probabilities));
        }

        System.out.println(json.toString(4));
    }

    /**
     * Adds the counts of a sequence to the model.
     *
     * @param sequence The sequence to add.
     */
    protected abstract void add(SequenceSecondary sequence);

    /**
     * Sets the inner Matrix for the given descriptor.
     *
     * @param descriptor  The descriptor of the matrix.
     * @param innerMatrix The inner matrix.
     */

    protected abstract void add(String descriptor, InnerMatrix innerMatrix);

    /**
     * Sums up the counts of the given models.
     *
     * @param models The models to sum up.
     * @return The sum of the models.
     */
    protected abstract T sum(Collection<T> models);

    /**
     * Converts the matrices, which are in the generic type of the class, to the unified output format.
     * The resulting map is a map of descriptors to matrices.
     *
     * @return The matrices in the unified output format.
     */
    protected abstract Map<String, InnerMatrix> toOutputFormat();

    /**
     * Saves the model to a file.
     * Default is to overwrite the file.
     *
     * @param output The file to save the model to.
     * @throws IOException If an I/O error occurs.
     */
    public void save(File output) throws IOException {
        save(output, false);
    }

    /**
     * Saves the model to a file.
     *
     * @param output The file to save the model to.
     * @param append If true, the model will be appended to the file. If false, the file will be overwritten.
     * @throws IOException If an I/O error occurs.
     */
    public void save(File output, boolean append) throws IOException {
        // Convert the matrices, which are in the generic type of the class, to the unified output format.
        // The outermost map is a map of descriptors to matrices.
        Map<String, InnerMatrix> outputFormat = toOutputFormat();

        try (var writer = new BufferedWriter(new FileWriter(output, append))) {
            // Get a sorted list of the descriptors.
            List<String> entries = new ArrayList<>(outputFormat.keySet());
            entries.sort((string1, string2) -> {
                String[] split1 = string1.split(",");
                String[] split2 = string2.split(",");

                for (int i = 0; i < split1.length; i++) {
                    if (i == 3) {
                        int val1 = Integer.parseInt(split1[i]);
                        int val2 = Integer.parseInt(split2[i]);

                        if (val1 != val2) {
                            return val1 - val2;
                        }
                        continue;
                    }
                    if (split1[i].compareTo(split2[i]) != 0) {
                        return split1[i].compareTo(split2[i]);
                    }
                }
                return 0;
            });

            String firstEntry = entries.get(0);

            writer.write("// Matrix" + (firstEntry.split(",").length + 2) + "D");
            writer.newLine();
            writer.newLine();

            // Write the descriptors
            for (String key : entries) {
                writer.write("=" + key + "=");

                writer.newLine();
                writer.newLine();

                writer.write(outputFormat.get(key).toString());

                writer.newLine();
                writer.newLine();
            }
        }
    }

    /**
     * Predicts the secondary structure of the given FASTA file.
     *
     * @param fasta The FASTA file to predict the secondary structure of.
     * @return The predictions in {@link SequenceProbabilities} format.
     * @throws IOException If an I/O error occurs.
     */
    public List<SequenceProbabilities> predict(File fasta) throws IOException {
        List<Sequence> sequences = loadSequencesFromFasta(fasta);

        List<SequenceProbabilities> sequenceProbabilities = new ArrayList<>();
        for (Sequence sequence : sequences) {
            sequenceProbabilities.add(predict(sequence));
        }

        return sequenceProbabilities;
    }

    public abstract SequenceProbabilities predict(Sequence sequence);

    protected abstract void fillArrays();
}
