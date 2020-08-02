package io.github.bluelhf.permutations;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GenerationData {
    private int max;
    private int threadSize;
    private int threadCount;
    private long timeStarted;
    private @Nullable Generator generator;
    private List<CompletableFuture<Void>> threads = new ArrayList<>();
    private LinkedHashMap<Integer, Double> threadProgress = new LinkedHashMap<>();

    public GenerationData() {
        this.max = 0;
        this.threadSize = 0;
        this.threadCount = 0;
        this.timeStarted = 0;
        this.generator = null;
    }

    public GenerationData(Generator generator, List<CompletableFuture<Void>> threads, int max, int threadSize, int threadCount, long timeStarted) {
        for(int i = 0; i < threads.size(); i++) threadProgress.put(i, 0D);
        this.threads.addAll(threads);
        this.max = max;
        this.threadSize = threadSize;
        this.threadCount = threadCount;
        this.timeStarted = timeStarted;
        this.generator = generator;
    }

    public @Nullable Generator getGenerator() {
        return generator;
    }

    public GenerationData setGenerator(Generator newGenerator) {
        this.generator = newGenerator;
        return this;
    }

    public double getProgress(int threadId) {
        return threadProgress.get(threadId);
    }
    public GenerationData setProgress(int threadId, double progress) {
        this.threadProgress.put(threadId, progress);
        return this;
    }

    public List<CompletableFuture<Void>> getThreads() {
        return this.threads;
    }

    public GenerationData setThreads(List<CompletableFuture<Void>> newThreads) {
        this.threads.clear();
        this.threads.addAll(newThreads);
        for(int i = 0; i < this.threads.size(); i++) threadProgress.put(i, 0D);
        return this;
    }

    public GenerationData setThreadProgress(LinkedHashMap<Integer, Double> newThreadProgress) {
        this.threadProgress.clear();
        this.threadProgress.putAll(newThreadProgress);
        return this;
    }

    public GenerationData setMax(int newMax) {
        this.max = newMax;
        return this;
    }

    public GenerationData setThreadSize(int newThreadSize) {
        this.threadSize = newThreadSize;
        return this;
    }

    public GenerationData setThreadCount(int newThreadCount) {
        this.threadCount = newThreadCount;
        return this;
    }

    public GenerationData setTimeStarted(long newTimeStarted) {
        this.timeStarted = newTimeStarted;
        return this;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public int getMax() {
        return max;
    }

    public long getTimeStarted() {
        return timeStarted;
    }
}
