package org.propra13.HelperClasses.Validation;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.propra13.HelperClasses.Enums.AminoAcid;
import org.propra13.HelperClasses.Enums.SecondaryStructure;
import org.propra13.HelperClasses.Sequence.SequenceSecondary;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Validator {
    private final String name;
    private final EnumMap<SecondaryStructure, Double> qValues;
    private final double q3;
    private final EnumMap<SecondaryStructure, Double> sovPerSS;
    private final double sov;
    private final SequenceSecondary observed;
    private final SequenceSecondary predicted;

    private final int length;
    private final int predictedLength;

    public Validator(SequenceSecondary observed, SequenceSecondary predicted) {
        if (predicted == null || observed == null) {
            throw new IllegalArgumentException("Predicted and observed secondary structure must not be null");
        }
        if (predicted.length() != observed.length()) {
            throw new IllegalArgumentException("Predicted and observed secondary structure must have the same length");
        }
        if (!predicted.getName().equals(observed.getName())) {
            throw new IllegalArgumentException(
                    "Predicted and observed secondary structure must have the same name. Got " + predicted.getName() +
                            " and " + observed.getName() + " instead.");
        }

        this.observed = observed;
        this.predicted = predicted;

        this.length = observed.length();
        this.predictedLength =
                (int) predicted.getStructure().stream().filter(struct -> struct != SecondaryStructure.N).count();

        Pair<SequenceSecondary, SequenceSecondary> trimmed = trimSequences(observed, predicted);
        observed = trimmed.getLeft();
        predicted = trimmed.getRight();

        this.name = predicted.getName();

        var aValues = getAValues(observed, predicted);
        var bValues = getBValues(observed, predicted);

        this.qValues = SecondaryStructure.getValues().stream().reduce(new EnumMap<>(SecondaryStructure.class),
                (map, structure) -> {
                    map.put(structure,
                            100. * aValues.get(structure).get(structure).get() / bValues.get(structure).get());
                    return map;
                }, (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                });

        this.q3 = 100. * SecondaryStructure.getValues().stream().mapToInt(
                structure -> aValues.get(structure).get(structure).get()).sum() /
                bValues.values().stream().mapToInt(AtomicInteger::get).sum();

        Map<SecondaryStructure, TreeSet<Segment>> observedSegments = observed.splitToSegments();
        Map<SecondaryStructure, TreeSet<Segment>> predictedSegments = predicted.splitToSegments();

        Map<SecondaryStructure, Set<Pair<Segment, Segment>>> sPerSS = getS(observedSegments, predictedSegments);
        Map<SecondaryStructure, Set<Segment>> siPerSS = getSi(observedSegments, predictedSegments);
        Map<SecondaryStructure, Integer> nPerSS = getN(sPerSS, siPerSS);

        EnumMap<SecondaryStructure, Double> sovPerSS = new EnumMap<>(SecondaryStructure.class);

        double sov = 0;

        for (SecondaryStructure structure : SecondaryStructure.getValues()) {
            double recipN = 1. / nPerSS.get(structure);

            double overlapSum = sPerSS.get(structure).stream().mapToDouble(pair -> {
                Segment observedSegment = pair.getLeft();
                Segment predictedSegment = pair.getRight();

                int dividend =
                        observedSegment.minOverlap(predictedSegment) + observedSegment.getDelta(predictedSegment);
                int divisor = observedSegment.maxOverlap(predictedSegment);

                int length = observedSegment.getLength();

                return ((double) dividend / divisor) * length;
            }).sum();

            sov += overlapSum;

            double result = 100 * recipN * overlapSum;

            sovPerSS.put(structure, result);
        }

        this.sovPerSS = sovPerSS;

        this.sov = 100 * sov / nPerSS.values().stream().mapToInt(Integer::intValue).sum();
    }

    private static Map<SecondaryStructure, Integer> getN(
            Map<SecondaryStructure, Set<Pair<Segment, Segment>>> overlappingPairs,
            Map<SecondaryStructure, Set<Segment>> observedWithoutOverlap) {
        Map<SecondaryStructure, Integer> n = new EnumMap<>(SecondaryStructure.class);

        for (SecondaryStructure structure : SecondaryStructure.getValues()) {
            int sum = overlappingPairs.get(structure).stream().mapToInt(pair -> pair.getLeft().getLength()).sum() +
                    observedWithoutOverlap.get(structure).stream().mapToInt(Segment::getLength).sum();

            n.put(structure, sum);
        }

        return n;
    }

    private static Map<SecondaryStructure, Set<Pair<Segment, Segment>>> getS(
            Map<SecondaryStructure, TreeSet<Segment>> observedSegments,
            Map<SecondaryStructure, TreeSet<Segment>> predictedSegments) {
        Map<SecondaryStructure, Set<Pair<Segment, Segment>>> s = new EnumMap<>(SecondaryStructure.class);

        for (SecondaryStructure structure : SecondaryStructure.getValues()) {
            s.put(structure, new HashSet<>());

            for (Segment observedSegment : observedSegments.get(structure)) {
                Collection<Segment> overlapping = observedSegment.findOverlaps(predictedSegments.get(structure));
                for (Segment predictedSegment : overlapping) {
                    s.get(structure).add(Pair.of(observedSegment, predictedSegment));
                }
            }
        }
        return s;
    }

    private static Map<SecondaryStructure, Set<Segment>> getSi(
            Map<SecondaryStructure, TreeSet<Segment>> observedSegments,
            Map<SecondaryStructure, TreeSet<Segment>> predictedSegments) {
        Map<SecondaryStructure, Set<Segment>> si = new EnumMap<>(SecondaryStructure.class);

        for (SecondaryStructure structure : SecondaryStructure.getValues()) {
            si.put(structure, new HashSet<>());

            for (Segment observedSegment : observedSegments.get(structure)) {
                Collection<Segment> overlapping = observedSegment.findOverlaps(predictedSegments.get(structure));
                if (overlapping.isEmpty()) {
                    si.get(structure).add(observedSegment);
                }
            }
        }

        return si;
    }

    private static EnumMap<SecondaryStructure, EnumMap<SecondaryStructure, AtomicInteger>> getAValues(
            SequenceSecondary observed, SequenceSecondary predicted) {
        EnumMap<SecondaryStructure, EnumMap<SecondaryStructure, AtomicInteger>> aValues =
                new EnumMap<>(SecondaryStructure.class) {{
                    SecondaryStructure.getValues().forEach(
                            structure -> put(structure, new EnumMap<>(SecondaryStructure.class) {{
                                SecondaryStructure.getValues().forEach(
                                        structure -> put(structure, new AtomicInteger(0)));
                            }}));
                }};

        for (int i = 0; i < predicted.length(); i++) {
            SecondaryStructure predictedStructure = predicted.getStructure().get(i);

            if (predictedStructure == SecondaryStructure.N) {
                continue;
            }

            SecondaryStructure observedStructure = observed.getStructure().get(i);

            aValues.get(observedStructure).get(predictedStructure).incrementAndGet();
        }

        return aValues;
    }

    private static EnumMap<SecondaryStructure, AtomicInteger> getBValues(SequenceSecondary observed,
                                                                         SequenceSecondary predicted) {
        EnumMap<SecondaryStructure, AtomicInteger> bValues = new EnumMap<>(SecondaryStructure.class) {{
            SecondaryStructure.getValues().forEach(structure -> put(structure, new AtomicInteger(0)));
        }};

        for (int i = 0; i < predicted.length(); i++) {
            SecondaryStructure predictedStructure = predicted.getStructure().get(i);

            if (predictedStructure == SecondaryStructure.N) {
                continue;
            }

            SecondaryStructure observedStructure = observed.getStructure().get(i);

            bValues.get(observedStructure).incrementAndGet();
        }

        return bValues;
    }

    private static Pair<SequenceSecondary, SequenceSecondary> trimSequences(SequenceSecondary observed,
                                                                            SequenceSecondary predicted) {
        int start = 0;
        int end = predicted.length() - 1;

        while (predicted.getStructure().get(start) == SecondaryStructure.N && start < end) {
            start++;
        }

        while (predicted.getStructure().get(end) == SecondaryStructure.N && start < end) {
            end--;
        }

        return Pair.of(observed.getSubSequence(start, end), predicted.getSubSequence(start, end));
    }

    @Override
    public String toString() {
        String format = "%.1f";

        return ">" + name + " " +
                Stream.of(q3, sov, qValues.get(SecondaryStructure.H), qValues.get(SecondaryStructure.E),
                        qValues.get(SecondaryStructure.C), sovPerSS.get(SecondaryStructure.H),
                        sovPerSS.get(SecondaryStructure.E), sovPerSS.get(SecondaryStructure.C)).map(
                        value -> Double.isNaN(value) ? "-" : String.format(format, value)).collect(
                        Collectors.joining(" ")) + "\n" + "AS " +
                predicted.getSequence().stream().map(AminoAcid::toString).collect(Collectors.joining()) + "\n" + "PS " +
                predicted.getStructure().stream().map(SecondaryStructure::toString).collect(Collectors.joining()) +
                "\n" + "SS " +
                observed.getStructure().stream().map(SecondaryStructure::toString).collect(Collectors.joining()) + "\n";
    }

    public String getName() {
        return name;
    }

    public EnumMap<SecondaryStructure, Double> getqValues() {
        return qValues;
    }

    public double getQ3() {
        return q3;
    }

    public EnumMap<SecondaryStructure, Double> getSovPerSS() {
        return sovPerSS;
    }

    public double getSov() {
        return sov;
    }

    public int getLength() {
        return length;
    }

    public int getPredictedLength() {
        return predictedLength;
    }

    public JSONObject toJson() {
        return new JSONObject() {{
            put("name", name);
            put("length", length);
            put("predictedLength", predictedLength);
            put("reference",
                    observed.getStructure().stream().map(SecondaryStructure::toString).collect(Collectors.joining()));

            if (!Double.isNaN(q3) && !Double.isInfinite(q3)) {
                put("q3", q3);
            }

            if (!Double.isNaN(sov) && !Double.isInfinite(sov)) {
                put("sov", sov);
            }

            put("qPerSS", new JSONObject() {{
                for (SecondaryStructure structure : SecondaryStructure.getValues()) {
                    if (!Double.isNaN(qValues.get(structure)) && !Double.isInfinite(qValues.get(structure))) {
                        put(structure.toString(), qValues.get(structure));
                    }
                }
            }});

            put("sovPerSS", new JSONObject() {{
                for (SecondaryStructure structure : SecondaryStructure.getValues()) {
                    if (!Double.isNaN(sovPerSS.get(structure)) && !Double.isInfinite(sovPerSS.get(structure))) {
                        put(structure.toString(), sovPerSS.get(structure));
                    }
                }
            }});
        }};
    }
}
