package org.propra13;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.propra13.HelperClasses.Enums.OutputFormat;
import org.propra13.HelperClasses.Sequence.SequenceSecondary;
import org.propra13.HelperClasses.Validation.Summary;
import org.propra13.HelperClasses.Validation.Validator;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.propra13.HelperClasses.HelpMethods.*;

@CommandLine.Command(name = "Validate GOR", mixinStandardHelpOptions = true)

public class Validate implements Runnable {
    @CommandLine.Option(names = {"-p"}, required = true, description = "Predictions")
    File predictions;

    @CommandLine.Option(names = {"-r"}, required = true, description = "SecLib file")
    File seclib;

    @CommandLine.Option(names = {"-f"}, required = true, description = "Output format")
    OutputFormat format;

    @CommandLine.Option(names = {"-s"}, required = true, description = "Summary file")
    File summaryFile;

    @CommandLine.Option(names = {"-d"}, required = true, description = "Detailed file")
    File detailedFile;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Validate()).execute(args);
        System.exit(exitCode);
    }

    private List<Validator> createValidators(List<SequenceSecondary> predictedStructures,
                                             List<SequenceSecondary> databaseStructures) {
        return predictedStructures.stream().map(predictedStructure -> {
            SequenceSecondary databaseStructure = databaseStructures.stream().filter(
                    dbStructure -> dbStructure.getName().equals(predictedStructure.getName()) &&
                            dbStructure.getSequence().equals(predictedStructure.getSequence())).findFirst().orElse(
                    null);
            return Pair.of(predictedStructure, databaseStructure);
        }).filter(pair -> pair.getRight() != null).filter(
                pair -> pair.getLeft().length() == pair.getRight().length()).map(
                pair -> new Validator(pair.getLeft(), pair.getRight())).toList();
    }

    @Override
    public void run() {
        try {
            String predictionContent = readFile(predictions);
            List<SequenceSecondary> predictedStructures;

            if (predictionContent.startsWith("[\n")) {
                predictedStructures = loadPredictionsFromJson(predictions);
            } else {
                predictedStructures = loadPredictions(predictions);
            }

            List<SequenceSecondary> databaseStructures = loadSequencesFromSeqLib(seclib);
            List<Validator> validators = createValidators(databaseStructures, predictedStructures);

            try (var writer = new BufferedWriter(new FileWriter(summaryFile))) {
                if (format == OutputFormat.txt) {
                    writer.write(new Summary(validators).toString());
                } else {
                    writer.write(new Summary(validators).toJson().toString(4));
                }
            }

            try (var writer = new BufferedWriter(new FileWriter(detailedFile))) {
                if (format == OutputFormat.txt) {
                    for (Validator validator : validators) {
                        writer.write(validator.toString());
                        writer.newLine();
                    }
                } else {
                    JSONArray validatorsJson = new JSONArray() {{
                        for (Validator validator : validators) {
                            put(validator.toJson());
                        }
                    }};

                    writer.write(validatorsJson.toString(4));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
