package org.propra13;

import org.propra13.GOR.AbstractGOR;
import org.propra13.GOR.GOR_Version;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;

@CommandLine.Command(name = "Train", mixinStandardHelpOptions = true)
public class Train implements Runnable {
    @CommandLine.Option(names = {"--db"}, required = true, description = "Seclib database file")
    File db;

    @CommandLine.Option(names = {"--method"}, required = true, description = "GOR version to use")
    GOR_Version method;

    @CommandLine.Option(names = {"--model"}, required = true, description = "Model file output")
    File model;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Train()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        AbstractGOR<?> gor = method.trainGOR(db); // Training
        try {
            gor.save(model); // Save model
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}