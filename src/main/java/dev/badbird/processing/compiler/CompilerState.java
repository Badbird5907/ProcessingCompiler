package dev.badbird.processing.compiler;

import dev.badbird.processing.compiler.processor.PreProcessor;
import dev.badbird.processing.compiler.processor.impl.CheckInvalidVariablesProcessor;
import dev.badbird.processing.compiler.processor.impl.CompilerOverrideProcessor;
import dev.badbird.processing.compiler.processor.impl.RemoveTypeImportProcessor;
import lombok.Data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class CompilerState {
    private static final PreProcessor[] preProcessors = new PreProcessor[]{
            new CompilerOverrideProcessor(),
            new CheckInvalidVariablesProcessor(),
            new RemoveTypeImportProcessor(),
    };
    // order by number of files depending, then by actual order on
    private final File main;
    private final File output;

    private Map<String, PythonFile> pythonFiles = new HashMap<>();

    public void discoverFiles(boolean recurse) {
        for (File file : Objects.requireNonNull(main.getParentFile().listFiles())) {
            if (file.isDirectory() && !recurse) continue;
            discoverFiles(file);
        }
        System.out.println("Discovered files: (" + pythonFiles.size() + ")");
        pythonFiles.forEach((s, pythonFile) -> System.out.println(s + " " + pythonFile));
    }

    private void discoverFiles(File file) {
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                discoverFiles(f);
            }
        } else {
            if (file.getName().endsWith(".py") && !file.getName().equals(output.getName())) {
                boolean isOnRoot = file.getParentFile().equals(main.getParentFile());
                String name = isOnRoot ? file.getName() : main.toPath().relativize(file.toPath()).toString()
                        .replace("." + File.separatorChar, "")
                        .replace(File.separatorChar, '.');
                if (name.startsWith(".")) {
                    name = name.substring(1);
                }
                System.out.println("Discovered file: " + name + " | " + file);
                pythonFiles.put(name, new PythonFile(file, name));
            }
        }
    }

    public void preProcess() {
        for (PreProcessor preProcessor : preProcessors) {
            preProcessor.process(this);
        }
    }

    public PythonFile getMainPythonFile() {
        return pythonFiles.get(main.getName());
    }
}
