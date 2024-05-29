package dev.badbird.processing.compiler.strategy.impl;

import dev.badbird.processing.compiler.CompilerException;
import dev.badbird.processing.compiler.CompilerState;
import dev.badbird.processing.compiler.PythonFile;
import dev.badbird.processing.compiler.strategy.CompilationStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * This strategy will compile the files blindly, meaning it will not check for imports, it will just compile the files in the order they are found.
 */
public class BlindCompilationStrategy implements CompilationStrategy {
    @Override
    public String compile(CompilerState state) {
        StringBuilder sb = new StringBuilder();
        List<PythonFile> files = new ArrayList<>();
        state.getPythonFiles().values().forEach(pythonFile -> {
            String[] lines = pythonFile.getContent().split("\n");
            StringBuilder newContent = new StringBuilder("# COMPILER_BEGIN: " + pythonFile.getName() + "\n");
            for (String line : lines) {
                if (line.startsWith("from ")) { // check the import
                    String[] split = line.split(" ");
                    String fileName = split[1];
                    PythonFile file = state.getPythonFiles().get(fileName + ".py");
                    if (file == null) {
                        throw new CompilerException("File " + fileName + ".py not found!");
                    }
                    if (!file.isMain(state))
                        files.add(file);
                } else {
                    newContent.append(line).append("\n");
                }
            }
            newContent.append("# COMPILER_END: ").append(pythonFile.getName()).append("\n");
            pythonFile.setContent(newContent.toString());
        });
        for (PythonFile file : files) {
            sb.append(file.getContent()).append("\n");
        }
        PythonFile pythonFile = state.getMainPythonFile();
        if (pythonFile == null) {
            throw new CompilerException("Main file not found!");
        }
        sb.append(pythonFile.getContent());
        return sb.toString();
    }
}
