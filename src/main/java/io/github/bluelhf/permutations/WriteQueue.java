package io.github.bluelhf.permutations;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class WriteQueue {
    private OutputStream stream;
    private BufferedWriter writer;
    public WriteQueue(OutputStream stream) {
        this.stream = stream;
        writer = new BufferedWriter(new OutputStreamWriter(stream));
    }

    public void close()  {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String s) {
        try {
            writer.write(s + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
