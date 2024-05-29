package dev.badbird.processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.badbird.processing.bullshit.Launcher;
import dev.badbird.processing.compiler.CompilerException;
import dev.badbird.processing.compiler.CompilerState;
import dev.badbird.processing.compiler.strategy.CompilationStrategy;
import dev.badbird.processing.compiler.strategy.impl.BlindCompilationStrategy;
import dev.badbird.processing.compiler.strategy.impl.graph.GraphCompilationStrategy;
import dev.badbird.processing.objects.Config;
import lombok.SneakyThrows;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Config config;
    @SneakyThrows
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("c", "config", true, "Config file path");
        options.addOption("o", "output", true, "Output file path");
        options.addOption("s", "strategy", true, "Compilation strategy");
        options.addOption("m", "main", true, "Main file path");
        options.addOption("l", "launch", false, "Launch the compiled file with jycessing");
        options.addOption("h", "help", false, "Show this help message");
        CommandLineParser parser = new DefaultParser();
        CommandLine cli = parser.parse(options, args);
        if (cli.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("processing-compiler", options);
            return;
        }

        // get the directory the jar is in
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File configFile;
        if (cli.hasOption("config")) {
            configFile = new File(cli.getOptionValue("config"));
        } else {
            File dir = new File(path).getParentFile();
            configFile = new File(dir, "config.json");
        }
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
                config = new Config();
                String json = gson.toJson(config);
                System.out.println("Creating config file with default values.");
                Files.writeString(configFile.toPath(), json);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            String json = Files.readString(configFile.toPath());
            config = gson.fromJson(json, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File output = new File(cli.getOptionValue("output", "_output.pyde"));
            CompilerState compilerState = new CompilerState(new File(cli.getOptionValue("main", "./main.py")), output);
            compilerState.discoverFiles();
            compilerState.preProcess();

            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
            CompilationStrategy strategy;
            if (cli.hasOption("strategy")) {
                strategy = switch (cli.getOptionValue("strategy").toLowerCase()) {
                    case "graph" -> new GraphCompilationStrategy();
                    case "blind" -> new BlindCompilationStrategy();
                    default -> {
                        System.err.println("Invalid strategy, falling back to graph strategy.");
                        yield new GraphCompilationStrategy();
                    }
                };
            } else {
                strategy = new GraphCompilationStrategy();
            }
            String content = strategy.compile(compilerState);
            String finalContent = "# Compilation Strategy: " + strategy.getClass().getName() + "\n" + content;
            Files.writeString(output.toPath(), finalContent);
        } catch (CompilerException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (cli.hasOption("launch")) {
            Launcher.launch(cli.getOptionValue("o", "_output.pyde"));
        }
    }
}