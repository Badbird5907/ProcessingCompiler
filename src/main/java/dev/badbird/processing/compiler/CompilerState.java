package dev.badbird.processing.compiler;

import dev.badbird.processing.compiler.processor.PreProcessor;
import dev.badbird.processing.compiler.processor.impl.CheckInvalidVariablesProcessor;
import dev.badbird.processing.compiler.processor.impl.RemoveTypeImportProcessor;
import dev.badbird.processing.compiler.strategy.CompilationStrategy;
import dev.badbird.processing.compiler.strategy.impl.graph.GraphCompilationStrategy;
import lombok.Data;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class CompilerState {
    private static final CompilationStrategy strategy = new GraphCompilationStrategy();
    private static final PreProcessor[] preProcessors = new PreProcessor[]{
            new CheckInvalidVariablesProcessor(),
            new RemoveTypeImportProcessor()
    };
    // order by number of files depending, then by actual order on
    private final File main;
    private final File output;

    private Map<String, PythonFile> pythonFiles = new HashMap<>();

    public void discoverFiles(){
        List.of(Objects.requireNonNull(main.getParentFile().listFiles()))
                .stream().filter(file -> file.getName().endsWith(".py") && !file.getName().equals(output.getName())).forEach(file ->
                        pythonFiles.put(file.getName(), new PythonFile(file, file.getName())));
    }

    public void preProcess(){
        for (PreProcessor preProcessor : preProcessors) {
            preProcessor.process(this);
        }
    }

    public PythonFile getMainPythonFile() {
        return pythonFiles.get(main.getName());
    }
}
