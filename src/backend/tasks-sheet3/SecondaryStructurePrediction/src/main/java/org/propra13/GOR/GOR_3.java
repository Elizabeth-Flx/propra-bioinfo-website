package org.propra13.GOR;

import org.apache.commons.lang3.tuple.Pair;
import org.propra13.HelperClasses.Enums.AminoAcid;
import org.propra13.HelperClasses.Enums.SecondaryStructure;
import org.propra13.HelperClasses.InnerMatrix;
import org.propra13.HelperClasses.Probability;
import org.propra13.HelperClasses.Sequence.Sequence;
import org.propra13.HelperClasses.Sequence.SequenceProbabilities;
import org.propra13.HelperClasses.Sequence.SequenceSecondary;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GOR_3 extends AbstractGOR<EnumMap<AminoAcid, InnerMatrix>> {
    private final int[][][][] array =
            new int[SecondaryStructure.getValues().size()][AminoAcid.values().length][AminoAcid.values().length][2 * M +
                    1];
    private final double[][] fsi_fs = new double[SecondaryStructure.getValues().size()][AminoAcid.values().length];
    private final double[][] log_fsi_fs = new double[SecondaryStructure.getValues().size()][AminoAcid.values().length];

    public GOR_3(File source) throws IOException {
        super(source);
        fillArrays();
    }

    public GOR_3(String content) {
        super(content);
        fillArrays();
    }

    public GOR_3(Collection<? extends AbstractGOR<EnumMap<AminoAcid, InnerMatrix>>> models) {
        super(models);
        fillArrays();
    }

    @Override
    protected void add(SequenceSecondary sequence) {
        IntStream.range(M, sequence.length() - M).forEach(i -> {
            SecondaryStructure structure = sequence.getStructure().get(i);
            AminoAcid centralAmino = sequence.getSequence().get(i);

            Map<Integer, AminoAcid> deltaAminoAcids = sequence.getDeltaAminoAcids(i);
            deltaAminoAcids.forEach((delta, aminoAcid) -> computeIfAbsent(structure,
                    $ -> new EnumMap<>(AminoAcid.class)).computeIfAbsent(centralAmino,
                    $ -> new InnerMatrix()).computeIfAbsent(aminoAcid, $ -> new HashMap<>()).computeIfAbsent(delta,
                    $ -> new AtomicInteger()).incrementAndGet());
        });
    }

    @Override
    protected void add(String descriptor, InnerMatrix innerMatrix) {
        String[] parts = descriptor.split(",");
        SecondaryStructure structure = SecondaryStructure.valueOf(parts[1]);
        AminoAcid aminoAcid = AminoAcid.valueOf(parts[0]);

        computeIfAbsent(structure, s -> new EnumMap<>(AminoAcid.class)).put(aminoAcid, innerMatrix);
    }

    @Override
    protected EnumMap<AminoAcid, InnerMatrix> sum(Collection<EnumMap<AminoAcid, InnerMatrix>> models) {
        return Arrays.stream(AminoAcid.values()).map(aminoAcid -> {
            InnerMatrix innerMatrix =
                    new InnerMatrix(models.stream().map(model -> model.get(aminoAcid)).collect(Collectors.toList()));
            return Pair.of(aminoAcid, innerMatrix);
        }).collect(Collectors.toMap(Pair::getKey, Pair::getValue, (x, y) -> x, () -> new EnumMap<>(AminoAcid.class)));
    }

    @Override
    protected Map<String, InnerMatrix> toOutputFormat() {
        Map<String, InnerMatrix> output = new HashMap<>();

        for (SecondaryStructure structure : SecondaryStructure.getValues()) {
            for (AminoAcid aminoAcid : AminoAcid.getSortedCanonical()) {
                output.put(aminoAcid.toString() + "," + structure.toString(),
                        getOrDefault(structure, new EnumMap<>(AminoAcid.class)).getOrDefault(aminoAcid,
                                new InnerMatrix()));
            }
        }

        return output;
    }

    @Override
    public SequenceProbabilities predict(Sequence sequence) {
        List<Probability> probabilities = new ArrayList<>();
        for (int i = M; i < sequence.getSequence().size() - M; i++) {
            Map<SecondaryStructure, Double> probabilityAtPos = new HashMap<>();
            AminoAcid centralAmino = sequence.getSequence().get(i);

            if (centralAmino == AminoAcid.GAP) {
                for (SecondaryStructure secondaryStructure : SecondaryStructure.getValues()) {
                    probabilityAtPos.put(secondaryStructure, 1. / 3);
                }
                probabilities.add(new Probability(probabilityAtPos));
                continue;
            }

            for (SecondaryStructure secondaryStructure : SecondaryStructure.getValues()) {
                Set<SecondaryStructure> notCurrentSS = SecondaryStructure.getOthers(secondaryStructure);

                double sum_i_delta = 0;
                for (int delta = -M; delta <= M; delta++) {
                    AminoAcid aminoAtDelta = sequence.getSequence().get(i + delta);

                    double count_s_a = array[secondaryStructure.i][centralAmino.i][aminoAtDelta.i][delta + M];

                    double count_si_a = 0;
                    for (SecondaryStructure notCurrent : notCurrentSS) {
                        count_si_a += array[notCurrent.i][centralAmino.i][aminoAtDelta.i][delta + M];
                    }

                    count_s_a = Math.max(count_s_a, 1);
                    count_si_a = Math.max(count_si_a, 1);

                    double single_i_delta =
                            Math.log(count_s_a / count_si_a) + log_fsi_fs[secondaryStructure.i][centralAmino.i];
                    sum_i_delta += single_i_delta;
                }

                double factor = Math.exp(sum_i_delta) * (1 / fsi_fs[secondaryStructure.i][centralAmino.i]);
                double probability = factor / (1 + factor);
                probabilityAtPos.put(secondaryStructure, probability);
            }
            Probability probabilitiesForSS = new Probability(probabilityAtPos);
            probabilities.add(probabilitiesForSS);
        }

        return new SequenceProbabilities(sequence, probabilities);
    }

    @Override
    protected void fillArrays() {
        for (SecondaryStructure structure : SecondaryStructure.getValues()) {
            for (AminoAcid aminoAcid : AminoAcid.values()) {
                this.array[structure.i][aminoAcid.i] =
                        getOrDefault(structure, new EnumMap<>(AminoAcid.class)).getOrDefault(aminoAcid,
                                new InnerMatrix()).toArray();

                int currentSum = getOrDefault(structure, new EnumMap<>(AminoAcid.class)).getOrDefault(aminoAcid,
                        new InnerMatrix()).getSum();
                int otherSum = SecondaryStructure.getOthers(structure).stream().mapToInt(
                        ss -> getOrDefault(ss, new EnumMap<>(AminoAcid.class)).getOrDefault(aminoAcid,
                                new InnerMatrix()).getSum()).sum();
                double ratio = (double) otherSum / currentSum;

                this.fsi_fs[structure.i][aminoAcid.i] = ratio;
                this.log_fsi_fs[structure.i][aminoAcid.i] = Math.log(ratio);
            }
        }
    }

    public int[][][][] getArray() {
        return array;
    }
}
