package org.propra13.HelperClasses.Enums;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Set;

/**
 * This enum represents the secondary structure of a protein.
 * It can be either helix (H), sheet (E) or coil (C).
 */
public enum SecondaryStructure {
    H(0), E(1), C(2), N(3);
    private static final EnumMap<SecondaryStructure, Set<SecondaryStructure>> others =
            new EnumMap<>(SecondaryStructure.class) {{
                put(H, Set.of(E, C));
                put(E, Set.of(H, C));
                put(C, Set.of(H, E));
            }};
    public final int i;

    SecondaryStructure(int i) {
        this.i = i;
    }

    public static Set<SecondaryStructure> getOthers(SecondaryStructure structure) {
        return others.get(structure);
    }

    public static Collection<SecondaryStructure> getValues() {
        return Set.of(H, E, C);
    }

    @Override
    public String toString() {
        return this == N ? "-" : name();
    }
}
