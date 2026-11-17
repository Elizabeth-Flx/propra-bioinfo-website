package org.propra13.HelperClasses;

import org.json.JSONObject;
import org.propra13.HelperClasses.Enums.SecondaryStructure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a data structure for saving the probabilities of secondary structures.
 * It is a HashMap of a SecondaryStructure and a Double.
 * The key is the secondary structure and the value is the probability for this Secondary Structure to appear.
 */
public class Probability extends HashMap<SecondaryStructure, Double> {

    public Probability(Map<SecondaryStructure, Double> probabilities) {
        super(probabilities);
    }

    public Probability(Collection<Probability> probabilities) {
        for (Probability probability : probabilities) {
            for (SecondaryStructure ss : probability.keySet()) {
                if (this.containsKey(ss)) {
                    this.put(ss, this.get(ss) + probability.get(ss));
                } else {
                    this.put(ss, probability.get(ss));
                }
            }
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        for (SecondaryStructure ss : this.keySet()) {
            Double value = this.get(ss);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                json.put(ss.toString(), 0);
            } else {
                json.put(ss.toString(), this.get(ss));
            }
        }
        return json;
    }
}
