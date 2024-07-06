package pl.mrstudios.proxy.logger.stream;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public class LoggerFilteringStream extends OutputStream {

    private final OutputStream original;
    private final Collection<String> filter;
    private final StringBuilder currentLine;

    public LoggerFilteringStream(
            @NotNull OutputStream original,
            @NotNull Collection<String> filter
    ) {
        this.filter = filter;
        this.original = original;
        this.currentLine = new StringBuilder();
    }

    @Override
    public void write(int b) throws IOException {

        char c = (char) b;
        if (c != '\n') {
            this.currentLine.append(c);
            return;
        }

        if (this.filter.stream().noneMatch((string) -> this.currentLine.toString().contains(string) || this.currentLine.toString().startsWith(string))) {
            this.original.write(this.currentLine.toString().getBytes());
            this.original.write(b);
        }

        this.currentLine.setLength(0);

    }

    @Override
    public void flush() throws IOException {
        this.original.flush();
    }

    @Override
    public void close() throws IOException {
        this.original.close();
    }

}
