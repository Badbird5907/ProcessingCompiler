package dev.badbird.processing.compiler.strategy;

import dev.badbird.processing.compiler.CompilerState;

public interface CompilationStrategy {
    String compile(CompilerState compilerState);
}
