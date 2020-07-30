package io.github.bluelhf.permutations;

import com.aparapi.Kernel;
import com.aparapi.Range;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class GPUGenerator {

    private OutputStream outputStream;
    private String charset;
    public GPUGenerator(String charset, OutputStream outputStream) {
        this.outputStream = outputStream;
        this.charset = charset;
    }

    /**
     * @return This GPUGenerator's output stream
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }
    /**
     * Sets the output stream of this GPUGenerator
     * @return This GPUGenerator, after modifying the output stream
     */
    public GPUGenerator setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }
    /**
     * @return This GPUGenerator's charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the charset of this GPUGenerator
     * @return This GPUGenerator, after modifying the charset
     */
    public GPUGenerator setCharset() {
        this.charset = charset;
        return this;
    }



    /**
     * Calculates permutations in parallel on the CPU, and writes them to the OutputStream as they are generated.
     * Individual permutations are followed by a newline.
     *
     * @param length The length of the permutations
     * */
    public void permutations(int length) throws IOException {
        int max = (int) Math.pow(charset.length(), length);

        int bucketCount = Math.min(max, 512);
        int bucketSize = (int) Math.ceil(max / (double) bucketCount);

        String zeroPad = ("" + charset.charAt(0)).repeat(length);
        WriteQueue writeQueue = new WriteQueue(outputStream);

        final int[] readyDevices = {0};
        char[] out = new char[bucketCount*length];
        char[] chars = charset.toCharArray();
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int id = getGlobalId();
                readyDevices[0]++;
                for(int i = id*bucketSize; i < (id+1)*bucketSize;i++) {
                    if (i >= max) i = (id+1)*bucketSize; else {
                        int quotient = (int) Math.floor(i / (double) length);
                        int remainder = i % length;

                        out[id] = chars[remainder];
                        while (quotient != 0) {
                            remainder = quotient % length;
                            quotient = (int) Math.floor(quotient / (double) length);
                            for(int j = length-1; j > 0; j--) {
                                if (out[id+j] == 0) continue;
                                out[id+j+1] = out[id+j];
                            }
                            out[id] = chars[remainder];
                        }
                    }

                }
                /*for (int i = id * bucketSize; i < (id + 1) * bucketSize; i++) {
                    if (i >= max) break;

                    String v = Utility.toBase(i, charset.length(), charset);
                    v = zeroPad.substring(v.length()) + v;

                    writeQueue.write(v);
                }*/
            }
        };

        Range range = Range.create(bucketCount);

        (new Thread(() -> {
            while (readyDevices[0] < bucketCount) {
                System.out.print("\rReady devices: " + readyDevices[0]);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        })).start();
        kernel.execute(range);


    }
}
