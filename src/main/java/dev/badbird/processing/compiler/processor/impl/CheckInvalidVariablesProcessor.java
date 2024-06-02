package dev.badbird.processing.compiler.processor.impl;

import dev.badbird.processing.compiler.CompilerException;
import dev.badbird.processing.compiler.CompilerState;
import dev.badbird.processing.compiler.processor.PreProcessor;

public class CheckInvalidVariablesProcessor implements PreProcessor {
    private static final String[] WHITELIST = {
            " ", "\t", "#",
            "def ", "class ",
            "from ", "import "
    };

    @Override
    public void process(CompilerState state) {
        state.getPythonFiles().values().forEach(pythonFile -> {
            String[] lines = pythonFile.getContent().split("\n");
            for (String line : lines) {
                if (isVariableDeclaration(line)) {
                    throw new CompilerException("Invalid variable declaration in file " + pythonFile.getName() + " at line " + line);
                }
            }
        });
    }

    public boolean isVariableDeclaration(String line) {
        if (line.isEmpty() || line.isBlank())
            return false;
        for (String s : WHITELIST) {
            if (line.startsWith(s))
                return false;
        }
        return line.contains("=");
    }
}
