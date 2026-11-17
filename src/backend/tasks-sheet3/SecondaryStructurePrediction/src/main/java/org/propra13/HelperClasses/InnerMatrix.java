package org.propra13.HelperClasses;

import org.propra13.HelperClasses.Enums.AminoAcid;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.propra13.GOR.AbstractGOR.M;

public class InnerMatrix extends EnumMap<AminoAcid, Map<Integer, AtomicInteger>> {
    public InnerMatrix() {
        super(AminoAcid.class);
    }

    /**
     * Creates a new InnerModel from the given matrix string.
     *
     * @param matrixString The matrix string.
     */
    public InnerMatrix(String matrixString) {
        this();
        Arrays.stream(matrixString.split("\n"))
              // Discard empty lines
              .filter(line -> !line.isBlank())
              // Split lines by tabs
              .map(line -> line.split("\t"))
              // Convert every line to a Pair of AminoAcid and Map of delta to count
              .forEach(splittedLine -> {
                  AminoAcid aminoAcid = AminoAcid.valueOf(splittedLine[0]);

                  Map<Integer, AtomicInteger> deltaCount = new HashMap<>(2 * M + 1);

                  IntStream.rangeClosed(-M, M).forEach(delta -> {
                      int count = Integer.parseInt(splittedLine[M + delta + 1].strip());
                      deltaCount.put(delta, new AtomicInteger(count));
                  });
                  put(aminoAcid, deltaCount);
              });
    }

    /**
     * Creates a new InnerModel by summing up the given models.
     *
     * @param models The models to sum up.
     */
    public InnerMatrix(Collection<InnerMatrix> models) {
        this();
        models.stream().flatMap(model -> model.entrySet().stream()) // get all entries from all models
              .forEach(entry -> {
                  // get amino acid from model
                  AminoAcid aminoAcid = entry.getKey();
                  // get delta count from model
                  Map<Integer, AtomicInteger> deltaCount = entry.getValue();
                  // save amino acid, delta and sum up delta count
                  deltaCount.forEach(
                          (delta, count) -> computeIfAbsent(aminoAcid, $ -> new HashMap<>()).computeIfAbsent(delta,
                                  $ -> new AtomicInteger()).addAndGet(count.get()));
              });
    }

    /**
     * creates the String representation of the matrix.
     *
     * @return The matrix as a string.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (AminoAcid aminoAcid : AminoAcid.getSortedCanonical()) {
            builder.append(aminoAcid).append("\t"); // adds the amino acid rows

            builder.append(
                    IntStream.rangeClosed(-M, M).mapToObj( // adds the delta columns ( tab seperated ) and the counts
                            delta -> getOrDefault(aminoAcid, new HashMap<>()).getOrDefault(delta,
                                    new AtomicInteger()).get()).map(Object::toString).collect(
                            Collectors.joining("\t")));

            builder.append("\n");
        }

        return builder.toString();
    }

    /**
     * Returns the sum of all counts in the matrix.
     *
     * @return The sum of all counts in the matrix.
     */
    public int getSum() {
        return values().stream().flatMapToInt(
                deltaCount -> deltaCount.values().stream().mapToInt(AtomicInteger::get)).sum();
    }

    public int[][] toArray() {
        int[][] array = new int[AminoAcid.values().length][2 * M + 1];
        for (AminoAcid aminoAcid : AminoAcid.values()) {
            for (int delta = -M; delta <= M; delta++) {
                array[aminoAcid.i][delta + M] =
                        getOrDefault(aminoAcid, new HashMap<>()).getOrDefault(delta, new AtomicInteger()).get();
            }
        }
        return array;
    }
}
