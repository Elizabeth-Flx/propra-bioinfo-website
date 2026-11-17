package org.propra13.HelperClasses.Sequence;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.propra13.HelperClasses.Enums.AminoAcid;
import org.propra13.HelperClasses.Enums.SecondaryStructure;
import org.propra13.HelperClasses.Validation.Segment;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.propra13.GOR.AbstractGOR.M;

/**
 * class that represents a secondary structure sequence
 * contains the sequence name, the sequence and the secondary structure
 */
public class SequenceSecondary extends Sequence {
    private final List<SecondaryStructure> structure;

    /**
     * Creates a SequenceSecondary object from Strings containing the name, the sequence and the secondary structure
     */
    public SequenceSecondary(String name, String sequence, String structure) {
        this(name, convertSequence(sequence), convertStructure(structure));
    }

    public SequenceSecondary(JSONObject json) {
        this(json.getString("name"), json.getString("sequence"), json.getString("prediction"));
    }

    public SequenceSecondary(String name, List<AminoAcid> sequence, List<SecondaryStructure> structure) {
        super(name, sequence);
        if (structure == null) {
            throw new IllegalArgumentException("Name, sequence and structure must not be null");
        }

        if (sequence.size() != structure.size()) {
            throw new IllegalArgumentException("Sequence and structure must have the same length");
        }
        this.structure = structure;
    }

    /**
     * Creates a SequenceSecondary object from a block. The block must contain the name, the sequence and the secondary structure.
     */
    public SequenceSecondary(String block) {
        this(block.split("\n")[0].strip(), block.split("\n")[1].substring(2).strip(),
                block.split("\n")[2].substring(2).strip());
    }

    /**
     * Converts a string containing the secondary structure to a list of {@link SecondaryStructure}objects
     */
    private static List<SecondaryStructure> convertStructure(String structureString) {
        return structureString.chars().mapToObj(c -> {
            if (c != '-') {
                return SecondaryStructure.valueOf(String.valueOf((char) c));
            } else {
                return SecondaryStructure.N;
            }
        }).collect(Collectors.toList());
    }

    public List<SecondaryStructure> getStructure() {
        return structure;
    }

    /**
     * Returns a map containing delta and their amino acid found at delta relative to position k
     *
     * @param k the position
     */
    public Map<Integer, AminoAcid> getDeltaAminoAcids(int k) {
        if (k < M) {
            throw new IllegalArgumentException("k must be at least M");
        }
        if (k > getSequence().size() - M) {
            throw new IllegalArgumentException("k must be at most sequence length - M");
        }

        return IntStream.rangeClosed(-M, +M).boxed().map(delta -> Pair.of(delta, getSequence().get(k + delta))).collect(
                Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    public EnumMap<SecondaryStructure, TreeSet<Segment>> splitToSegments() {
        EnumMap<SecondaryStructure, TreeSet<Segment>> segments = new EnumMap<>(SecondaryStructure.class);
        for (SecondaryStructure ss : SecondaryStructure.values()) {
            segments.put(ss, new TreeSet<>());
        }

        int start = 0;
        int end;
        SecondaryStructure currentSS = structure.get(0);
        for (int i = 1; i < structure.size(); i++) {
            if (structure.get(i) != currentSS) {
                end = i - 1;
                segments.get(currentSS).add(new Segment(currentSS, start, end));
                start = i;
                currentSS = structure.get(i);
            }
        }
        end = structure.size() - 1;
        segments.get(currentSS).add(new Segment(currentSS, start, end));
        return segments;
    }

    @Override
    public SequenceSecondary getSubSequence(int start, int end) {
        return new SequenceSecondary(getName(), getSequence().subList(start, end + 1),
                getStructure().subList(start, end + 1));
    }
}