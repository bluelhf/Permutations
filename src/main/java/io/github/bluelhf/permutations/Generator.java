package io.github.bluelhf.permutations;

import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;

public class Generator {
    private OutputStream outputStream;
    public Generator(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public @Nullable GenerationData generate() {
        return null;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public Generator setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }
}
