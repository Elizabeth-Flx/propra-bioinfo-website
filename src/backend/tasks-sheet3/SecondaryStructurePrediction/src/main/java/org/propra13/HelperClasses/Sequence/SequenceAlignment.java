package org.propra13.HelperClasses.Sequence;

import java.util.ArrayList;
import java.util.List;

public class SequenceAlignment extends Sequence {

    private final List<Sequence> alignments;

    public SequenceAlignment(String name, String sequence, List<Sequence> alignments) {
        super(name, sequence);
        this.alignments = alignments;
    }

    public SequenceAlignment(String block) {
        super(block);
        String[] lines = block.split("\n");

        List<Sequence> alignments = new ArrayList<>();
        for (String line : lines) {
            if (!line.startsWith("SS") && !line.startsWith("AS") && !line.startsWith(">")) {
                alignments.add(new Sequence(getName(), line.split(" ")[1]));
            }
        }
        this.alignments = alignments;
    }

    public List<Sequence> getAlignments() {
        return alignments;
    }
}
