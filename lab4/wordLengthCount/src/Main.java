import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String DIR_PATH = "texts";

    public static void main(String[] args) {
        timeIt(() -> seqDirAnalysis(DIR_PATH), "sequential");
        timeIt(() -> parDirAnalysis(DIR_PATH), "parallel");
    }

    // Sequential directory analysis
    private static void seqDirAnalysis(String dir) {
        var paths = new ArrayList<Path>();
        try {
            Files.walk(Paths.get(dir))
                    .filter(Files::isRegularFile)
                    .forEach(paths::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        var counts = new HashMap<Integer, Integer>();

        for (Path path : paths) {
            try {
                var txt = Files.readString(path);
                String[] words = txt.split("\\s+");

                for (String word : words) {
                    int len = word.length();
                    counts.put(len, counts.getOrDefault(len, 0) + 1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        printStats(counts);
    }

    // Parallel directory analysis
    private static void parDirAnalysis(String dir) {
        var pool = ForkJoinPool.commonPool();
        var task = new DirLengthStat(dir);

        var result = pool.invoke(task);

        pool.shutdown();
        printStats(result);
    }

    // Timing method
    private static void timeIt(Runnable task, String type) {
        var start = System.currentTimeMillis();
        task.run();
        var end = System.currentTimeMillis();
        System.out.println("Time for " + type + " analysis: " + (end - start) + " ms");
    }

    // Print stats
    public static void printStats(HashMap<Integer, Integer> map) {
        var wordsAmount = 0;
        var charsAmount = 0.0;

        for (int lengthKey : map.keySet()) {
            wordsAmount += map.get(lengthKey);
            charsAmount += map.get(lengthKey) * lengthKey;
        }

        var meadWordsLength = charsAmount / wordsAmount;
        var dispersion = 0.0;

        for (int lengthKey : map.keySet()) {
            for (int i = 0; i < map.get(lengthKey); i++) {
                dispersion += Math.pow(lengthKey - meadWordsLength, 2);
            }
        }

        dispersion /= wordsAmount;

        System.out.println("Total amount of words: " + wordsAmount);
        System.out.println("Avg length of words: " + Math.round(meadWordsLength * 100.0) / 100.0);

        System.out.println("Word length dispersion: " + Math.round(dispersion * 100.0) / 100.0);
        System.out.println("Standard deviation: " + Math.round(Math.sqrt(dispersion) * 100.0) / 100.0);
    }
}
