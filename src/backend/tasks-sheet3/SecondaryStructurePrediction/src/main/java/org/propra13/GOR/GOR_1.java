package org.propra13.GOR;

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

public class GOR_1 extends AbstractGOR<InnerMatrix> {
    private final int[][][] array =
            new int[SecondaryStructure.getValues().size()][AminoAcid.values().length][2 * M + 1];

    private final double[] fsi_fs = new double[SecondaryStructure.getValues().size()];
    private final double[] log_fsi_fs = new double[SecondaryStructure.getValues().size()];

    public GOR_1(File source) throws IOException {
        super(source);
        fillArrays();
    }

    public GOR_1(String content) {
        super(content);
        fillArrays();
    }

    public GOR_1(Collection<GOR_1> models) {
        super(models);
        fillArrays();
    }

    @Override
    protected void add(SequenceSecondary sequence) {
        IntStream.range(M, sequence.length() - M).forEach(i -> {
            SecondaryStructure structure = sequence.getStructure().get(i);

            Map<Integer, AminoAcid> deltaAminoAcids = sequence.getDeltaAminoAcids(i);
            deltaAminoAcids.forEach((delta, aminoAcid) -> {
                computeIfAbsent(structure, $ -> new InnerMatrix()).computeIfAbsent(aminoAcid,
                        $ -> new HashMap<>()).computeIfAbsent(delta, $ -> new AtomicInteger()).incrementAndGet();
            });
        });
    }

    @Override
    protected void add(String descriptor, InnerMatrix innerMatrix) {
        SecondaryStructure structure = SecondaryStructure.valueOf(descriptor);
        put(structure, innerMatrix);
    }

    @Override
    protected InnerMatrix sum(Collection<InnerMatrix> models) {
        return new InnerMatrix(models);
    }

    @Override
    protected Map<String, InnerMatrix> toOutputFormat() {
        return entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));
    }

    @Override
    public SequenceProbabilities predict(Sequence sequence) {
        List<Probability> probabilities = new ArrayList<>();
        for (int i = M; i < sequence.getSequence().size() - M; i++) {
            Map<SecondaryStructure, Double> probabilityAtPos = new EnumMap<>(SecondaryStructure.class);

            for (SecondaryStructure secondaryStructure : SecondaryStructure.getValues()) {
                Set<SecondaryStructure> otherStructures = SecondaryStructure.getOthers(secondaryStructure);

                double sum_i_delta = 0;
                for (int delta = -M; delta <= M; delta++) {
                    AminoAcid aminoAtDelta = sequence.getSequence().get(i + delta);

                    int count_s_a = array[secondaryStructure.i][aminoAtDelta.i][delta + M];

                    int count_si_a = 0;
                    for (SecondaryStructure notCurrent : otherStructures) {
                        count_si_a += array[notCurrent.i][aminoAtDelta.i][delta + M];
                    }

                    count_s_a = Math.max(count_s_a, 1);
                    count_si_a = Math.max(count_si_a, 1);

                    double single_i_delta =
                            Math.log((double) count_s_a / count_si_a) + this.log_fsi_fs[secondaryStructure.i];
                    sum_i_delta += single_i_delta;
                }

                double factor = Math.exp(sum_i_delta) * (1 / this.fsi_fs[secondaryStructure.i]);
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
        for (SecondaryStructure secondaryStructure : SecondaryStructure.getValues()) {
            this.array[secondaryStructure.i] = get(secondaryStructure).toArray();

            int currentSum = get(secondaryStructure).getSum();
            int otherSum =
                    SecondaryStructure.getOthers(secondaryStructure).stream().mapToInt(ss -> get(ss).getSum()).sum();
            double ratio = (double) otherSum / currentSum;

            this.fsi_fs[secondaryStructure.i] = ratio;
            this.log_fsi_fs[secondaryStructure.i] = Math.log(ratio);
        }
    }
}
