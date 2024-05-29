package dev.badbird.processing.compiler.processor.impl;

import dev.badbird.processing.compiler.CompilerState;
import dev.badbird.processing.compiler.processor.PreProcessor;

public class RemoveTypeImportProcessor implements PreProcessor {

    @Override
    public void process(CompilerState state) {
        state.getPythonFiles().values().forEach(pythonFile -> {
            pythonFile.setContent(
                    pythonFile.getContent().replaceAll("from Processing3 import \\*", "")
            );
        });
    }
}
