package io.github.bluelhf.permutations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

class Permutations {

    public static void main(String[] args) {
        int length = Integer.parseInt(args[0]);

        System.out.println("7562 in hex is " + Utility.toBase(7562, 16, "0123456789ABCDEF"));

        StringBuilder charsetBuilder = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++) {
            charsetBuilder.append(" ").append(args[i]);
        }
        String charset = charsetBuilder.toString();

        try {
            GPUGenerator gpuGenerator = new GPUGenerator(charset, new FileOutputStream("./permutations.txt"));
            gpuGenerator.permutations(length);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}