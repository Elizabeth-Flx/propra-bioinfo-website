package org.propra13.GOR;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Enum to represent the different versions of the GOR algorithm.
 */
public enum GOR_Version {
    gor1(GOR_1.class, 3), gor3(GOR_3.class, 4), gor4(GOR_4.class, 6);
    private final Class<? extends AbstractGOR<?>> clazz;

    /**
     * The dimensionality of the matrices in the GOR version.
     */
    private final int dimensionality;

    /**
     * Creates a new GOR version.
     *
     * @param clazz          The class of the GOR version.
     * @param dimensionality The dimensionality of the matrices in the GOR version.
     */
    GOR_Version(Class<? extends AbstractGOR<?>> clazz, int dimensionality) {
        this.clazz = clazz;
        this.dimensionality = dimensionality;
    }

    /**
     * Returns the class of the GOR version given a GOR-algorithm class.
     *
     * @return The class of the GOR version.
     */
    public static GOR_Version getByClass(Class<? extends AbstractGOR<?>> clazz) {
        for (GOR_Version version : values()) {
            if (version.clazz == clazz) {
                return version;
            }
        }

        return null;
    }

    /**
     * Returns the class of the GOR version given a dimensionality.
     *
     * @return The class of the GOR version.
     */

    public static GOR_Version getByDimensionality(int dimensionality) {
        for (GOR_Version version : values()) {
            if (version.dimensionality == dimensionality) {
                return version;
            }
        }

        return null;
    }

    /**
     * Creates an Instance of the given GOR version by training on the sequence from the input file.
     */

    public AbstractGOR<?> trainGOR(File source) {
        try {
            return clazz.getDeclaredConstructor(File.class).newInstance(source);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates an Instance of the given GOR version by loading the given model String.
     */
    public AbstractGOR<?> loadGOR(String input) {
        try {
            return clazz.getDeclaredConstructor(String.class).newInstance(input);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
