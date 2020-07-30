package io.github.bluelhf.permutations;

public class Utility {
    public static String toBase(int n, int base, String charset) {
        int quotient = (int) Math.floor(n / (double) base);
        int remainder = n % base;
        StringBuilder builder = new StringBuilder();
        builder.insert(0, charset.charAt(remainder));
        while (quotient != 0) {
            remainder = quotient % base;
            quotient = (int) Math.floor(quotient / (double) base);
            builder.insert(0, charset.charAt(remainder));
        }
        return builder.toString();
    }

    public static int getCPUCores() {
        return Runtime.getRuntime().availableProcessors();
    }
}
