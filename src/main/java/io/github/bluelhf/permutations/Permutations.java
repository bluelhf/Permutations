package io.github.bluelhf.permutations;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

class Permutations {

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        int length = Integer.parseInt(args[0]);
        String fileName = args[1];

        StringBuilder charsetBuilder = new StringBuilder(args[2]);
        for (int i = 3; i < args.length; i++) {
            charsetBuilder.append(" ").append(args[i]);
        }
        String charset = charsetBuilder.toString();

        try {
            CPUGenerator generator = new CPUGenerator(charset, new FileOutputStream(fileName));
            GenerationData result = generator.permutate(length);
            List<CompletableFuture<Void>> threads = result.getThreads();

            String info = "Max: " + result.getMax() + " | Thread Size: " + result.getThreadSize() + " | Threads: " + result.getThreadCount() + " | Base: " + ((CPUGenerator)result.getGenerator()).getCharset().length();
            System.out.println(info);
            Ansi ansi = new Ansi();
            ansi.a(Ansi.Attribute.STRIKETHROUGH_ON);
            ansi.a("─".repeat(info.length()));
            ansi.reset();
            System.out.println(ansi);

            int threadMax = ("" + result.getThreadCount()).length();

            String tableTop = "Thread - Status";
            String spacePad = " ".repeat(Math.max("Thread".length(), threadMax));
            System.out.println(tableTop);

            DecimalFormat df = new DecimalFormat("##0.00");


            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                private boolean hasPrinted = false;
                public void print() {
                    if (hasPrinted) {
                        for(int i = 0; i < threads.size(); i++) {
                            System.out.print(Ansi.ansi().cursorUpLine(1).eraseLine());
                        }
                    }

                    String zeroPad = " ".repeat(threadMax);
                    for (int i = 0; i < threads.size(); i++) {
                        String idx = "" + (i+1);
                        CompletableFuture<Void> cf = threads.get(i);
                        String threadID = "#" + zeroPad.substring(idx.length()) + idx;
                        threadID += spacePad.substring(threadID.length());
                        System.out.println(threadID + " - " + (cf.isDone() ? "Done!" : "Progress: " + df.format(result.getProgress(i)*100) + "%"));
                    }
                    hasPrinted = true;
                }

                @Override
                public void run() {
                    print();
                    if (threads.stream().allMatch(CompletableFuture::isDone)) {
                        for(int i = 0; i< result.getThreadCount(); i++) result.setProgress(i, 1);
                        print();
                        timer.cancel();
                        long time = System.currentTimeMillis() - result.getTimeStarted();
                        double sec = Math.round(time/100D)/10D;
                        System.out.println(Ansi.ansi().fgBrightGreen().a("Finished in " + sec + " seconds").reset().a(" (" + Math.round(result.getMax()/sec) + "iter/s)"));

                    }
                }

            }, 0, 250);

        } catch (IOException e) {
            e.printStackTrace();
        }
        AnsiConsole.systemUninstall();
    }
}