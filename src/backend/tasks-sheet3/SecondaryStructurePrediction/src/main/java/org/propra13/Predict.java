package org.propra13;

import org.propra13.GOR.AbstractGOR;
import org.propra13.GOR.GOR_5;
import org.propra13.GOR.GOR_Version;
import org.propra13.HelperClasses.Enums.OutputFormat;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@CommandLine.Command(name = "Predict", mixinStandardHelpOptions = true)
public class Predict implements Runnable {
    @CommandLine.Option(names = {"--model"}, required = true, description = "Model file output")
    File model;

    @CommandLine.Option(names = {"--seq"}, description = "Fasta file to make predictions for")
    File fasta;

    @CommandLine.Option(names = {"--maf"}, description = "Path to directory with .aln files (GOR 5)")
    File mafDir;

    @CommandLine.Option(names = {"--format"}, required = true, description = "Output format (txt | html)")
    OutputFormat format;

    @CommandLine.Option(names = {"--probabilities"}, description = "Include the probabilities in the output")
    boolean probabilities;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Predict()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        try {
            String content = new String(Files.readAllBytes(model.toPath()));
            int dimensionality = (int) content.split("=", 3)[1].chars().filter(ch -> ch == ',').count() + 3;
            GOR_Version version = GOR_Version.getByDimensionality(dimensionality);

            if (version == null) {
                throw new RuntimeException("Invalid model file");
            }

            AbstractGOR<?> gor = version.loadGOR(content);

            if (fasta != null) {
                if (format == OutputFormat.txt) {
                    AbstractGOR.writeOutput(gor.predict(fasta), probabilities);
                } else {
                    AbstractGOR.writeJson(gor.predict(fasta), probabilities);
                }
            } else {
                GOR_5 gor5 = new GOR_5(gor);
                if (format == OutputFormat.txt) {
                    AbstractGOR.writeOutput(gor5.predict(mafDir), probabilities);
                } else {
                    AbstractGOR.writeJson(gor5.predict(mafDir), probabilities);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}