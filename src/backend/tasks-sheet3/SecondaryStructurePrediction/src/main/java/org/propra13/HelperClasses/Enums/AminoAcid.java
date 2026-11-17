package org.propra13.HelperClasses.Enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Enum for all amino acids
 */
public enum AminoAcid {
    A(0), R(1), N(2), D(3), C(4), Q(5), E(5), G(7), H(8), I(9), L(10), K(11), M(12), F(13), P(14), S(15), T(16), W(17),
    Y(18), V(19), X(20), B(21), Z(22), U(23), GAP(24);

    public static final Set<AminoAcid> nonCanonical = Set.of(B, Z, X, U, GAP);
    private static final List<AminoAcid> sortedCanonical =
            Arrays.stream(values()).filter(value -> !nonCanonical.contains(value)).sorted(
                    Comparator.comparing(AminoAcid::name)).toList();
    public final int i;

    AminoAcid(int i) {
        this.i = i;
    }

    public static List<AminoAcid> getSortedCanonical() {
        return sortedCanonical;
    }

    @Override
    public String toString() {
        return this == GAP ? "-" : name();
    }
}
