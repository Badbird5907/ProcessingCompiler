package dev.badbird.processing.compiler.processor.impl;

import dev.badbird.processing.compiler.CompilerException;
import dev.badbird.processing.compiler.CompilerState;
import dev.badbird.processing.compiler.processor.PreProcessor;

public class CompilerOverrideProcessor implements PreProcessor {
    @Override
    public void process(CompilerState state) {
        // we'll just grab the first 10 lines
        state.getPythonFiles().values().forEach(pythonFile -> {
            String[] lines = pythonFile.getContent().split("\n");
            for (int i = 0; i < Math.min(lines.length, 10); i++) {
                String line = lines[i];
                // #!compiler_override 10
                if (line.startsWith("#!compiler_override ")) {
                    String split = line.split(" ")[1];
                    System.out.println("Found compiler override in " + pythonFile.getName() + " with priority " + split);
                    int priority;
                    try {
                        priority = Integer.parseInt(split.trim());
                    } catch (NumberFormatException e) {
                        throw new CompilerException("Priority must be a number");
                    }
                    if (priority < 0) {
                        throw new CompilerException("Priority cannot be less than 0");
                    }
                    System.out.println("Setting priority of " + pythonFile.getName() + " to " + priority);
                    pythonFile.setPriority(priority);
                }
            }
        });
    }
}
