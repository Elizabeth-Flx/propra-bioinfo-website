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

public class GOR_4 extends AbstractGOR<EnumMap<AminoAcid, EnumMap<AminoAcid, HashMap<Integer, InnerMatrix>>>> {
    private final GOR_3 gor_3;
    private final int[][][][][][] array =
            new int[SecondaryStructure.getValues().size()][AminoAcid.values().length][AminoAcid.values().length][2 * M +
                    1][AminoAcid.values().length][2 * M + 1];
    private final int[][][][] gor_3_array =
            new int[SecondaryStructure.getValues().size()][AminoAcid.values().length][AminoAcid.values().length][2 * M +
                    1];

    public GOR_4(File source) throws IOException {
        super(source);
        gor_3 = new GOR_3(source);
        fillArrays();
    }

    private GOR_4(String[] split) {
        super(split[1]);
        gor_3 = new GOR_3(split[2]);
        fillArrays();
    }

    public GOR_4(String content) {
        this(content.split("// Matrix[2-6]D"));
        fillArrays();
    }

    public GOR_4(Collection<GOR_4> models) {
        super(models);
        gor_3 = new GOR_3(models.stream().map(gor4 -> gor4.gor_3).collect(Collectors.toList()));
        fillArrays();
    }

    @Override
    protected void fillArrays() {
        gor_3.fillArrays();

        int[][][][] gor_3_array = gor_3.getArray();

        for (SecondaryStructure structure : SecondaryStructure.getValues()) {
            this.gor_3_array[structure.i] = gor_3_array[structure.i];

            for (AminoAcid centralAmino : AminoAcid.values()) {
                for (AminoAcid deltaAmino1 : AminoAcid.values()) {
                    for (int delta1 = -M; delta1 <= M; delta1++) {
                        this.array[structure.i][centralAmino.i][deltaAmino1.i][delta1 + M] =
                                computeIfAbsent(structure, $ -> new EnumMap<>(AminoAcid.class)).computeIfAbsent(
                                        centralAmino, $ -> new EnumMap<>(AminoAcid.class)).computeIfAbsent(deltaAmino1,
                                        $ -> new HashMap<>(2 * M + 1)).computeIfAbsent(delta1,
                                        $ -> new InnerMatrix()).toArray();
                    }
                }
            }
        }
    }

    @Override
    protected void add(SequenceSecondary sequence) {
        IntStream.range(M, sequence.length() - M).forEach(i -> {
            SecondaryStructure structure = sequence.getStructure().get(i);
            AminoAcid centralAmino = sequence.getSequence().get(i);

            IntStream.rangeClosed(-M, M).forEach(delta1 -> {
                AminoAcid deltaAmino1 = sequence.getSequence().get(i + delta1);
                IntStream.rangeClosed(delta1 + 1, M).forEach(delta2 -> {
                    AminoAcid deltaAmino2 = sequence.getSequence().get(i + delta2);
                    computeIfAbsent(structure, $ -> new EnumMap<>(AminoAcid.class)).computeIfAbsent(centralAmino,
                            $ -> new EnumMap<>(AminoAcid.class)).computeIfAbsent(deltaAmino1,
                            $ -> new HashMap<>(2 * M + 1)).computeIfAbsent(delta1,
                            $ -> new InnerMatrix()).computeIfAbsent(deltaAmino2,
                            $ -> new HashMap<>(2 * M + 1)).computeIfAbsent(delta2,
                            $ -> new AtomicInteger()).incrementAndGet();
                });
            });
        });
    }

    @Override
    protected void add(String descriptor, InnerMatrix innerMatrix) {
        String[] parts = descriptor.split(",");
        SecondaryStructure structure = SecondaryStructure.valueOf(parts[0]);
        AminoAcid centralAmino = AminoAcid.valueOf(parts[1]);
        AminoAcid deltaAmino = AminoAcid.valueOf(parts[2]);
        int delta = Integer.parseInt(parts[3]);

        computeIfAbsent(structure, k -> new EnumMap<>(AminoAcid.class)).computeIfAbsent(centralAmino,
                k -> new EnumMap<>(AminoAcid.class)).computeIfAbsent(deltaAmino, k -> new HashMap<>()).put(delta,
                innerMatrix);
    }

    @Override
    protected EnumMap<AminoAcid, EnumMap<AminoAcid, HashMap<Integer, InnerMatrix>>> sum(
            Collection<EnumMap<AminoAcid, EnumMap<AminoAcid, HashMap<Integer, InnerMatrix>>>> models) {
        EnumMap<AminoAcid, EnumMap<AminoAcid, HashMap<Integer, InnerMatrix>>> result = new EnumMap<>(AminoAcid.class);

        for (AminoAcid centralAmino : AminoAcid.values()) {
            for (AminoAcid deltaAmino : AminoAcid.values()) {
                for (int delta : IntStream.rangeClosed(-M, M).boxed().toList()) {
                    InnerMatrix innerMatrix = new InnerMatrix(
                            models.stream().map(model -> model.get(centralAmino).get(deltaAmino).get(delta)).collect(
                                    Collectors.toList()));
                    result.computeIfAbsent(centralAmino, k -> new EnumMap<>(AminoAcid.class)).computeIfAbsent(
                            deltaAmino, k -> new HashMap<>(2 * M + 1)).put(delta, innerMatrix);
                }
            }
        }

        return result;
    }

    @Override
    protected Map<String, InnerMatrix> toOutputFormat() {
        Map<String, InnerMatrix> output = new HashMap<>();

        for (SecondaryStructure structure : SecondaryStructure.getValues()) {
            for (AminoAcid centralAmino : AminoAcid.getSortedCanonical()) {
                for (AminoAcid deltaAmino : AminoAcid.getSortedCanonical()) {
                    for (int delta : IntStream.rangeClosed(-M, M).boxed().toList()) {
                        output.put(structure.toString() + "," + centralAmino.toString() + "," + deltaAmino.toString() +
                                        "," + delta,
                                getOrDefault(structure, new EnumMap<>(AminoAcid.class)).getOrDefault(centralAmino,
                                        new EnumMap<>(AminoAcid.class)).getOrDefault(deltaAmino,
                                        new HashMap<>(0)).getOrDefault(delta, new InnerMatrix()));
                    }
                }
            }
        }

        return output;
    }

    @Override
    public void save(File output) throws IOException {
        super.save(output);
        gor_3.save(output, true);
    }

    @Override
    public SequenceProbabilities predict(Sequence sequence) {
        List<Probability> probabilities = new ArrayList<>();
        for (int i = M; i < sequence.length() - M; i++) {
            Map<SecondaryStructure, Double> probabilityAtPos = new EnumMap<>(SecondaryStructure.class);
            AminoAcid centralAmino = sequence.getSequence().get(i);

            if (centralAmino == AminoAcid.GAP) {
                for (SecondaryStructure secondaryStructure : SecondaryStructure.getValues()) {
                    probabilityAtPos.put(secondaryStructure, 1. / 3);
                }
                probabilities.add(new Probability(probabilityAtPos));
                continue;
            }

            for (SecondaryStructure structure : SecondaryStructure.getValues()) {
                Set<SecondaryStructure> notCurrentStructures = SecondaryStructure.getOthers(structure);
                double sum_6d = 0;
                double sum_4d = 0;
                for (int delta1 = -M; delta1 <= M; delta1++) {
                    AminoAcid deltaAmino1 = sequence.getSequence().get(i + delta1);
                    int count_4d = gor_3_array[structure.i][centralAmino.i][deltaAmino1.i][delta1 + M];
                    int count_4d_i = 0;
                    for (SecondaryStructure notCurrentSS : notCurrentStructures) {
                        count_4d_i += gor_3_array[notCurrentSS.i][centralAmino.i][deltaAmino1.i][delta1 + M];
                    }

                    count_4d_i = Math.max(count_4d_i, 1);
                    count_4d = Math.max(count_4d, 1);

                    sum_4d += Math.log((double) count_4d / count_4d_i);
                    for (int delta2 = delta1 + 1; delta2 <= M; delta2++) {
                        AminoAcid deltaAmino2 = sequence.getSequence().get(i + delta2);
                        int count_6d =
                                array[structure.i][centralAmino.i][deltaAmino1.i][delta1 + M][deltaAmino2.i][delta2 +
                                        M];
                        int count_6d_i = 0;
                        for (SecondaryStructure notCurrentSS : notCurrentStructures) {
                            count_6d_i +=
                                    array[notCurrentSS.i][centralAmino.i][deltaAmino1.i][delta1 + M][deltaAmino2.i][
                                            delta2 + M];
                        }
                        count_6d_i = Math.max(count_6d_i, 1);
                        count_6d = Math.max(count_6d, 1);

                        sum_6d += Math.log((double) count_6d / count_6d_i);
                    }
                }
                double iValue = (2. / 17.) * sum_6d - (15. / 17.) * sum_4d;
                probabilityAtPos.put(structure, iValue);
            }

            Probability probability = new Probability(probabilityAtPos);
            probabilities.add(probability);
        }
        return new SequenceProbabilities(sequence, probabilities);
    }
}
