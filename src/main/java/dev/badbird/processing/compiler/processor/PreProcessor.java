package dev.badbird.processing.compiler.processor;

import dev.badbird.processing.compiler.CompilerState;

public interface PreProcessor {
    void process(CompilerState state);
}
