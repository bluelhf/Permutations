package io.github.bluelhf.permutations;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CPUGenerator extends Generator {

    private String charset;
    public CPUGenerator(String charset, OutputStream outputStream) {
        super(outputStream);
        this.charset = charset;
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
    public GenerationData permutate(long length) {
        long max = (long) Math.pow(charset.length(), length);

        long threadCount = Utility.getCPUCores();
        long threadSize = (long) Math.ceil(max / (double) threadCount);

        String zeroPad = ("" + charset.charAt(0)).repeat((int) length);
        ArrayList<CompletableFuture<Void>> futures = new ArrayList<>();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(getOutputStream()));

        long started = System.currentTimeMillis();

        GenerationData data = new GenerationData()
                .setGenerator(this)
                .setMax(max)
                .setTimeStarted(started)
                .setThreadCount(threadCount)
                .setThreadSize(threadSize);

        ExecutorService executor = Executors.newFixedThreadPool((int) threadCount);

        for (long b = 0; b < threadCount; b++) {
            final long id = b;
            CompletableFuture<Void> c = CompletableFuture.runAsync(() -> {
                for (long i = id*threadSize; i < (id + 1) * threadSize; i++) {
                    if (i >= max) break;
                    String v = Utility.toBase(i, charset.length(), charset);
                    v = zeroPad.substring(v.length()) + v;

                    double iIncr = i-(id*threadSize);
                    double progress = iIncr/ (double) threadSize;
                    data.setProgress(id, progress);

                    try {
                        writer.write(v + "\n");
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }, executor);

            futures.add(c);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            try {
                writer.close();
                getOutputStream().close();
                executor.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        data.setThreads(futures);
        return data;

    }
}
