package org.propra13.GOR;

import org.propra13.HelperClasses.Sequence.SequenceAlignment;
import org.propra13.HelperClasses.Sequence.SequenceProbabilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.propra13.HelperClasses.HelpMethods.loadSequenceAlignmentsFromMaf;


public class GOR_5 {
    private final AbstractGOR<?> gor;

    public GOR_5(AbstractGOR<?> gor) {
        this.gor = gor;
    }

    public List<SequenceProbabilities> predict(File mafDir) throws IOException {
        List<SequenceAlignment> alignments = loadSequenceAlignmentsFromMaf(mafDir);

        List<SequenceProbabilities> SequenceAlignmentSummedProbabilities = new ArrayList<>();

        for (SequenceAlignment multipleAlignment : alignments) {
            SequenceProbabilities probabilitiesSequenceAS = gor.predict(multipleAlignment);

            List<SequenceProbabilities> alignedProbabilities =
                    multipleAlignment.getAlignments().stream().map(gor::predict).toList();

            SequenceProbabilities summedProbabilitiesSequence =
                    new SequenceProbabilities(probabilitiesSequenceAS, alignedProbabilities);
            SequenceAlignmentSummedProbabilities.add(summedProbabilitiesSequence);
        }
        
        return SequenceAlignmentSummedProbabilities;
    }
}
