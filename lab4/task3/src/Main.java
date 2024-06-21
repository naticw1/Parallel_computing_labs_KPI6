import com.google.common.base.Stopwatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        List<List<String>> docs = loadTextFiles("for_compare");

        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        Set<String> result = pool.invoke(new WordFinder(docs));

        System.out.println(result);
    }

    public static List<List<String>> loadTextFiles(String dir) {
        File directory = new File(dir);
        List<List<String>> fileContents = new ArrayList<>();

        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                List<String> content = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.add(line);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
                }
                fileContents.add(content);
            }
        }

        return fileContents;
    }
}
