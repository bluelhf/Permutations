package io.github.bluelhf.permutations;

public class Utility {
    public static String toBase(long n, long base, String charset) {
        long quotient = (long) Math.floor(n / (double) base);
        long remainder = n % base;
        StringBuilder builder = new StringBuilder();
        builder.insert(0, charset.charAt((int) remainder));
        while (quotient != 0) {
            remainder = quotient % base;
            quotient = (long) Math.floor(quotient / (double) base);
            builder.insert(0, charset.charAt((int) remainder));
        }
        return builder.toString();
    }

    public static long getCPUCores() {
        return Runtime.getRuntime().availableProcessors();
    }
}
