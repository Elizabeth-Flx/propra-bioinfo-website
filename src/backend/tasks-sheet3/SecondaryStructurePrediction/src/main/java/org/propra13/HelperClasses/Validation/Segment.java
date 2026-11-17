package org.propra13.HelperClasses.Validation;

import org.propra13.HelperClasses.Enums.SecondaryStructure;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class Segment implements Comparable<Segment> {
    private final SecondaryStructure structure;
    private final int start;
    private final int end;

    public Segment(SecondaryStructure structure, int start, int end) {
        this.structure = structure;
        this.start = Math.min(start, end);
        this.end = Math.max(start, end);
    }

    @Override
    public int compareTo(Segment o) {
        if (structure != o.structure) {
            throw new IllegalArgumentException("Cannot compare segments of different structures");
        }

        if (this.overlaps(o)) {
            return 0;
        }

        return Integer.compare(start, o.start);
    }

    public int maxOverlap(Segment other) {
        checkStructure(other);

        return Math.max(end, other.end) - Math.min(start, other.start) + 1;
    }

    public int getDelta(Segment other) {
        checkStructure(other);

        int ovDiff = maxOverlap(other) - minOverlap(other);
        int ovMin = minOverlap(other);
        int lenThis = getLength() / 2;
        int lenOther = other.getLength() / 2;

        return Collections.min(Arrays.asList(ovDiff, ovMin, lenThis, lenOther));
    }


    public int minOverlap(Segment other) {
        checkStructure(other);

        return Math.max(Math.min(end, other.end) - Math.max(start, other.start) + 1, 0);
    }

    private void checkStructure(Segment other) {
        if (structure != other.structure) {
            throw new IllegalArgumentException("Cannot compare segments of different structures");
        }
    }

    public boolean overlaps(Segment other) {
        checkStructure(other);

        return minOverlap(other) > 0;
    }

    public int getLength() {
        return end - start + 1;
    }

    @Override
    public String toString() {
        return "Segment{" + "structure=" + structure + ", start=" + start + ", end=" + end + '}';
    }

    public Collection<Segment> findOverlaps(Collection<Segment> segments) {
        return segments.stream().filter(this::overlaps).collect(Collectors.toList());
    }
}
