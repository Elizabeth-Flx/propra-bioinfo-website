package org.propra13.HelperClasses;

import org.json.JSONArray;
import org.json.JSONObject;
import org.propra13.HelperClasses.Sequence.Sequence;
import org.propra13.HelperClasses.Sequence.SequenceAlignment;
import org.propra13.HelperClasses.Sequence.SequenceSecondary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class HelpMethods {

    /**
     * Reads a file and returns its content as a String
     *
     * @param file the file to read
     * @return the content of the file
     * @throws IOException if the file could not be read
     */
    public static String readFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    /**
     * Reads a file and returns its content as a List of strings, where the file contents are split by the given separator
     *
     * @param file the file to read
     * @param sep  the separator to split the file contents by
     * @return the content of the file as a List of strings
     * @throws IOException if the file could not be read
     */
    public static ArrayList<String> readAndSplitFile(File file, String sep) throws IOException {
        return new ArrayList<>(List.of(readFile(file).split(sep)));
    }

    /**
     * Loads the sequences from the given FASTA file.
     *
     * @param input The FASTA file.
     * @return The sequences in {@link Sequence} format.
     * @throws IOException If the file could not be read.
     */
    public static List<Sequence> loadSequencesFromFasta(File input) throws IOException {
        return readAndSplitFile(input, ">").stream()
                                           // Remove empty blocks
                                           .filter(block -> !block.isBlank())
                                           // Create sequence objects
                                           .map(Sequence::new)
                                           // Form a set
                                           .collect(Collectors.toList());
    }

    /**
     * Loads the sequences from the given SEQ_LIB file.
     *
     * @param input The SEQ_LIB file.
     * @return The sequences in {@link SequenceSecondary} format.
     * @throws IOException If the file could not be read.
     */
    public static List<SequenceSecondary> loadSequencesFromSeqLib(File input) throws IOException {
        return readAndSplitFile(input, ">").stream()
                                           // Remove empty blocks
                                           .filter(block -> !block.isBlank())
                                           // Create sequence objects (with secondary structure)
                                           .map(SequenceSecondary::new)
                                           // Form a set
                                           .toList();
    }

    public static List<SequenceSecondary> loadPredictions(File input) throws IOException {
        return readAndSplitFile(input, ">").stream()
                                           // Remove empty blocks
                                           .filter(block -> !block.isBlank())
                                           // Create sequence objects (with secondary structure)
                                           .map(block -> {
                                               var lines = block.lines().toList();
                                               var header = lines.get(0).replace(">", "").strip();
                                               var sequence = lines.get(1).split(" ")[1].strip();
                                               var secondary = lines.get(2).split(" ")[1].strip();
                                               return new SequenceSecondary(header, sequence, secondary);
                                           })
                                           // Form a set
                                           .toList();
    }

    public static List<SequenceSecondary> loadPredictionsFromJson(File input) throws IOException {
        JSONArray array = new JSONArray(readFile(input));
        return array.toList().stream().map(entry -> (HashMap<?, ?>) entry).map(JSONObject::new).map(
                SequenceSecondary::new).toList();
    }

    public static List<SequenceAlignment> loadSequenceAlignmentsFromMaf(File input) {
        return Arrays.stream(Objects.requireNonNull(input.listFiles())).map(file -> {
            try {
                return new SequenceAlignment(readFile(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
