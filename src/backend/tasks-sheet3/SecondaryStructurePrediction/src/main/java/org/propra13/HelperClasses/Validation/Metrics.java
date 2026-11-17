package org.propra13.HelperClasses.Validation;

import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

public class Metrics {
    private final double mean, median, min, max, stdDev, q5, q25, q75, q95;

    public Metrics(Collection<Double> values) {
        values = values.stream().filter(val -> !Double.isNaN(val)).toList();

        mean = values.stream().mapToDouble(Double::doubleValue).average().orElseThrow(
                () -> new RuntimeException("No values found"));
        median = values.stream().mapToDouble(Double::doubleValue).sorted().skip((values.size() - 1) / 2).limit(
                2 - values.size() % 2).average().orElseThrow(() -> new RuntimeException("No values found"));
        min = values.stream().mapToDouble(Double::doubleValue).min().orElseThrow(
                () -> new RuntimeException("No values found"));
        max = values.stream().mapToDouble(Double::doubleValue).max().orElseThrow(
                () -> new RuntimeException("No values found"));
        stdDev = Math.sqrt(values.stream().mapToDouble(Double::doubleValue).map(
                value -> Math.pow(value - mean, 2)).average().orElseThrow(
                () -> new RuntimeException("No values found")));
        q5 = getPercentile(values, 5);
        q25 = getPercentile(values, 25);
        q75 = getPercentile(values, 75);
        q95 = getPercentile(values, 95);
    }

    private static double getPercentile(Collection<Double> values, int percentile) {
        List<Double> sorted = values.stream().mapToDouble(Double::doubleValue).sorted().boxed().toList();

        int floor = (int) Math.floor((sorted.size()) * percentile / 100.0);
        int ceil = (int) Math.ceil((sorted.size()) * percentile / 100.0);

        return (sorted.get(floor) + sorted.get(ceil)) / 2;
    }

    @Override
    public String toString() {
        return String.format(
                "Mean:\t%.1f\tDev:\t%.1f\tMin:\t%.1f\tMax:\t%.1f\tMedian:\t%.1f\tQ5:\t%.1f\tQ25:\t%.1f\tQ75:\t%.1f\tQ95:\t%.1f",
                mean, stdDev, min, max, median, q5, q25, q75, q95);
    }

    public JSONObject toJson() {
        return new JSONObject() {{
            put("mean", mean);
            put("median", median);
            put("min", min);
            put("max", max);
            put("stdDev", stdDev);
            put("q5", q5);
            put("q25", q25);
            put("q75", q75);
            put("q95", q95);
        }};
    }
}
