package org.propra13.HelperClasses.Sequence;

import org.json.JSONObject;
import org.propra13.HelperClasses.Enums.AminoAcid;
import org.propra13.HelperClasses.Enums.SecondaryStructure;
import org.propra13.HelperClasses.Probability;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class that can save the sequence name, the sequence and the List of {@link Probability}s
 * for each Position ( and each SecondaryStructure ).
 */
public class SequenceProbabilities extends Sequence {
    /**
     * The list of probabilities for each position.
     */
    private final List<Probability> probabilities;

    /**
     * Creates a new sequence with the given name and sequence as a String.
     *
     * @param name          The name of the sequence as a String.
     * @param sequence      The sequence as a string of amino acids as a String.
     * @param probabilities The list of probabilities for each position.
     */
    public SequenceProbabilities(String name, String sequence, List<Probability> probabilities) {
        super(name, sequence);
        this.probabilities = probabilities;
    }

    /**
     * Creates a new sequence with a given {@link Sequence} and a list of probabilities.
     */
    public SequenceProbabilities(Sequence sequence, List<Probability> probabilities) {
        super(sequence.getName(),
                sequence.getSequence().stream().map(AminoAcid::toString).collect(Collectors.joining()));
        this.probabilities = probabilities;
    }


    public SequenceProbabilities(SequenceProbabilities master, Collection<SequenceProbabilities> toMerge) {
        super(master.getName(), master.getSequence());
        this.probabilities = new ArrayList<>();
        for (int i = 0; i < master.getProbabilities().size(); i++) {
            final int index = i;
            Set<Probability> probabilitiesToMerge = toMerge.stream().map(SequenceProbabilities::getProbabilities).map(
                    probabilities -> probabilities.get(index)).collect(Collectors.toSet());
            probabilitiesToMerge.add(master.getProbabilities().get(index));
            this.probabilities.add(new Probability(probabilitiesToMerge));
        }
    }


    public List<Probability> getProbabilities() {
        return probabilities;
    }

    /**
     * Creates a prediction sequence ( out of Secondary Structures ) as a String from the probabilities by choosing the most likely.
     *
     * @return The prediction sequence as a String.
     */
    public String createPredictionSequence() {
        StringBuilder predictionSequenceBuilder = new StringBuilder();
        for (Probability probability : probabilities) {
            SecondaryStructure maxSS =
                    // get the key ( Secondary Structure ) of the entry with the highest probability
                    probability.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(
                            new AbstractMap.SimpleEntry<>(SecondaryStructure.C, .5)).getKey();
            predictionSequenceBuilder.append(maxSS.toString());
        }
        return predictionSequenceBuilder.toString();
    }

    /**
     * Creates a sequence of probabilities for each Secondary Structure as a String.
     *
     * @param ss The secondary structure to get the probabilities for.
     * @return The probability sequence as a String.
     */
    public String createProbabilitySequenceForSS(SecondaryStructure ss) {
        StringBuilder probabilitySequenceBuilder = new StringBuilder();

        for (Probability probability : probabilities) {
            double probabilitySum = probability.values().stream().mapToDouble(Double::doubleValue).sum();

            probabilitySequenceBuilder.append((int) Math.round((probability.get(ss) / probabilitySum) * 10));
        }
        return probabilitySequenceBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(super.toString()).append("\n");
        builder.append("PS ").append(createPredictionSequence()).append("\n");
        for (SecondaryStructure ss : List.of(SecondaryStructure.H, SecondaryStructure.E, SecondaryStructure.C)) {
            builder.append("P").append(ss).append(" ").append(createProbabilitySequenceForSS(ss)).append("\n");
        }
        return builder.toString();
    }

    public JSONObject toJSON(boolean includeProbabilities) {
        JSONObject json = new JSONObject();
        json.put("name", getName());
        json.put("sequence", getSequence().stream().map(AminoAcid::toString).collect(Collectors.joining()));

        if (getSequence().size() > 16) {
            json.put("prediction", "-".repeat(8) + createPredictionSequence() + "-".repeat(8));
        } else {
            json.put("prediction", "-".repeat(getSequence().size()));
        }

        if (includeProbabilities) {
            json.put("probabilities", probabilities.stream().map(Probability::toJSON).collect(Collectors.toList()));
        }
        return json;
    }
}

