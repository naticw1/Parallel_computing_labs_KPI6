import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Class to calculate the frequency of word lengths across files in a directory
public class DirLengthStat extends RecursiveTask<HashMap<Integer, Integer>> {
    private final List<String> files;

    // Constructor to initialize file paths
    public DirLengthStat(String dir) {
        try (Stream<Path> paths = Files.walk(Paths.get(dir))) {
            files = paths.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while reading files");
        }
    }

    @Override
    protected HashMap<Integer, Integer> compute() {
        var tasks = new ArrayList<FileLengthStat>();

        // Create and fork new tasks for each file
        for (String file : files) {
            var task = new FileLengthStat(file);
            task.fork();
            tasks.add(task);
        }

        var results = new HashMap<Integer, Integer>();

        // Aggregate results from all tasks
        for (FileLengthStat task : tasks) {
            task.join().forEach((length, count) ->
                    results.merge(length, count, Integer::sum)
            );
        }

        return results;
    }
}
