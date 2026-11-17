package org.propra13.HelperClasses.Sequence;

import org.propra13.HelperClasses.Enums.AminoAcid;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A class representing a protein sequence.
 * Contains a name and a list of {@link AminoAcid}s.
 */
public class Sequence {
    /**
     * The name of the sequence.
     */
    private final String name;
    /**
     * The list of amino acids.
     */
    private final List<AminoAcid> sequence;

    /**
     * Creates a new sequence with the given name and sequence as a String.
     *
     * @param name     The name of the sequence as a String.
     * @param sequence The sequence as a string of amino acids as a String.
     */
    public Sequence(String name, String sequence) {
        this(name, convertSequence(sequence));
    }

    /**
     * Creates a new sequence with the given name and sequence as a list of amino acids.
     *
     * @param name     The name of the sequence as a String.
     * @param sequence The sequence as a list of amino acids.
     */
    public Sequence(String name, List<AminoAcid> sequence) {
        if (name == null || sequence == null) {
            throw new IllegalArgumentException("Name, sequence and structure must not be null");
        }
        this.name = name;
        this.sequence = sequence;
    }

    /**
     * Creates a new sequence from a block of text.
     * The block must have two lines, the first being the name and the second being the sequence.
     *
     * @param block The block of text as a String.
     */
    public Sequence(String block) {
        String[] lines = block.split("\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Sequence block must have at least 2 lines");
        }
        this.name = lines[0].replace(">", "").strip();
        this.sequence = convertSequence(lines[1].strip());
    }

    /**
     * Converts a string of amino acids to a list of amino acids.
     *
     * @param sequenceString The string of amino acids as a String.
     * @return The list of amino acids.
     */
    static List<AminoAcid> convertSequence(String sequenceString) {
        if (sequenceString.split(" ").length > 1) {
            sequenceString = sequenceString.split(" ")[1];
        }
        sequenceString = sequenceString.strip();
        return sequenceString.chars().mapToObj(c -> {
            if (c != '-') {
                return AminoAcid.valueOf(String.valueOf((char) c));
            } else {
                return AminoAcid.GAP;
            }
        }).toList();
    }

    public String getName() {
        return name;
    }

    public List<AminoAcid> getSequence() {
        return sequence;
    }

    public int length() {
        return sequence.size();
    }

    @Override
    public String toString() {
        return "> " + name + "\n" + "AS " + sequence.stream().map(AminoAcid::toString).collect(Collectors.joining());
    }

    public Sequence getSubSequence(int start, int end) {
        return new Sequence(name, sequence.subList(start, end));
    }
}
