package org.propra13.HelperClasses.Validation;

import org.json.JSONObject;
import org.propra13.HelperClasses.Enums.SecondaryStructure;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Summary {
    private final Metrics q3;
    private final Metrics sov;
    private final Map<SecondaryStructure, Metrics> q3PerSS;
    private final Map<SecondaryStructure, Metrics> sovPerSS;
    private final int numValidations;
    private final double meanLength;
    private final int totalLength;
    private final int predictedLength;

    public Summary(Collection<Validator> validators) {
        this.q3 = new Metrics(getValues(validators, Validator::getQ3));
        this.sov = new Metrics(getValues(validators, Validator::getSov));
        this.q3PerSS = getMetricsPerSS(validators, Validator::getqValues);
        this.sovPerSS = getMetricsPerSS(validators, Validator::getSovPerSS);
        this.numValidations = validators.size();

        Collection<Integer> lengths = getValues(validators, Validator::getLength);
        this.meanLength = lengths.stream().mapToDouble(Integer::doubleValue).average().orElseThrow(
                () -> new RuntimeException("No values found"));
        this.totalLength = lengths.stream().mapToInt(Integer::intValue).sum();
        this.predictedLength =
                getValues(validators, Validator::getPredictedLength).stream().mapToInt(Integer::intValue).sum();
    }

    private static Metrics getMetrics(Collection<Validator> validators, Function<Validator, Double> metric) {
        return new Metrics(getValues(validators, metric));
    }

    private static Map<SecondaryStructure, Metrics> getMetricsPerSS(Collection<Validator> validators,
                                                                    Function<Validator, Map<SecondaryStructure, Double>> metric) {
        return validators.stream().map(metric).flatMap(map -> map.entrySet().stream()).collect(
                Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList()))).entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, entry -> new Metrics(entry.getValue())));
    }

    private static <T> Collection<T> getValues(Collection<Validator> validators, Function<Validator, T> metric) {
        return validators.stream().map(metric).toList();
    }

    @Override
    public String toString() {
        return " # Statistics for protein validation:\n\n" + "Number of Proteins:\t" + numValidations +
                "\nMean Protein Length:\t" + String.format("%.2f", meanLength) + "\nSum of Protein Length:\t" +
                totalLength + "\nSum of Predicted Positions:\t" + predictedLength + "\n\n" + "Q_3:\t" + q3 +
                "\nQ_H:\t" + q3PerSS.get(SecondaryStructure.H) + "\nQ_E:\t" + q3PerSS.get(SecondaryStructure.E) +
                "\nQ_C:\t" + q3PerSS.get(SecondaryStructure.C) + "\n\nSOV:\t" + sov + "\nSOV_H:\t" +
                sovPerSS.get(SecondaryStructure.H) + "\nSOV_E:\t" + sovPerSS.get(SecondaryStructure.E) + "\nSOV_C:\t" +
                sovPerSS.get(SecondaryStructure.C);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("numValidations", numValidations);
        json.put("meanLength", meanLength);
        json.put("totalLength", totalLength);
        json.put("predictedLength", predictedLength);
        json.put("q3", q3.toJson());
        json.put("sov", sov.toJson());
        json.put("qPerSS", q3PerSS.entrySet().stream().collect(
                Collectors.toMap(entry -> entry.getKey().toString(), entry -> entry.getValue().toJson())));
        json.put("sovPerSS", sovPerSS.entrySet().stream().collect(
                Collectors.toMap(entry -> entry.getKey().toString(), entry -> entry.getValue().toJson())));
        return json;
    }
}
