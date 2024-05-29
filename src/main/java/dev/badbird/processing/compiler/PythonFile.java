package dev.badbird.processing.compiler;

import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;

@Data
public class PythonFile {
    private final File file;
    private final String name;

    private String content;

    @SneakyThrows
    public PythonFile(File file, String name) {
        this.file = file;
        this.name = name;

        this.content = new String(Files.readAllBytes(file.toPath()));
    }

    public boolean isMain(CompilerState state) {
        return name.equals(state.getMain().getName());
    }

    @Override
    public String toString() {
        return name;
    }
}
