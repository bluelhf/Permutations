package io.github.bluelhf.permutations;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CPUGenerator {

    private OutputStream outputStream;
    private String charset;
    public CPUGenerator(String charset, OutputStream outputStream) {
        this.outputStream = outputStream;
        this.charset = charset;
    }

    /**
     * @return This CPUGenerator's output stream
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }
    /**
     * Sets the output stream of this CPUGenerator
     * @return This CPUGenerator, after modifying the output stream
     */
    public CPUGenerator setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }
    /**
     * @return This CPUGenerator's charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the charset of this CPUGenerator
     * @return This CPUGenerator, after modifying the charset
     */
    public CPUGenerator setCharset() {
        this.charset = charset;
        return this;
    }



    /**
     * Calculates permutations in parallel on the CPU, and writes them to the OutputStream as they are generated.
     * Individual permutations are followed by a newline.
     *
     * @param length The length of the permutations
     * @return A Completable Future to be completed when the operation is complete.
     * */
    private CompletableFuture<Void> permutations(int length) throws IOException {
        int max = (int) Math.pow(charset.length(), length);

        int bucketCount = Utility.getCPUCores();
        int bucketSize = (int) Math.ceil(max / (double) bucketCount);

        String zeroPad = ("" + charset.charAt(0)).repeat(length);
        ArrayList<CompletableFuture<Void>> futures = new ArrayList<>();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        for (int b = 0; b < bucketCount; b++) {
            final int id = b;
            CompletableFuture<Void> c = CompletableFuture.runAsync(() -> {
                for (int i = id * bucketSize; i < (id + 1) * bucketSize; i++) {
                    if (i >= max) break;
                    String v = Utility.toBase(i, charset.length(), charset);
                    v = zeroPad.substring(v.length()) + v;


                    try {
                        writer.write(v + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

            futures.add(c);
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

    }
}
