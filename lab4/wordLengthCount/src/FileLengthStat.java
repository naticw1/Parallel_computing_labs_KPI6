import java.util.concurrent.RecursiveTask;
import java.util.HashMap;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.io.IOException;

// File length statistics using ForkJoin
public class FileLengthStat extends RecursiveTask<HashMap<Integer, Integer>> {
    private String file;
    private List<String> words;
    private int start;
    private int end;
    private final boolean split;

    public FileLengthStat(String file) {
        this.file = file;
        split = false;
    }

    public FileLengthStat(String file, List<String> words, int start, int end) {
        this.file = file;
        this.words = words;
        this.start = start;
        this.end = end;
        split = true;
    }

    @Override
    protected HashMap<Integer, Integer> compute() {
        if (!split) {
            initWordsList();
        }

        if (end - start < 200_000) {
            return getWordLengths();
        }

        var mid = (end + start) / 2;
        var left = new FileLengthStat(file, words, start, mid);
        left.fork();
        var right = new FileLengthStat(file, words, mid, end);
        var res = right.compute();
        left.join().forEach((k, v) -> res.merge(k, v, Integer::sum));

        return res;
    }

    // Gather word lengths in a given range
    private HashMap<Integer, Integer> getWordLengths() {
        var counts = new HashMap<Integer, Integer>();

        words.subList(start, end).forEach(word -> {
            var len = word.length();
            counts.merge(len, 1, Integer::sum);
        });

        return counts;
    }

    // Initialize word list from file
    private void initWordsList() {
        try {
            String txt = Files.readString(Paths.get(file));
            words = List.of(txt.split("\\s+"));
            start = 0;
            end = words.size();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading file");
        }
    }
}
