import java.util.*;
import java.util.concurrent.RecursiveTask;

// Performs recursive task to find common words in a list of documents.
public class WordFinder extends RecursiveTask<Set<String>> {
    private final List<List<String>> docs;

    // Initialize the documents list for processing
    public WordFinder(List<List<String>> docs) {
        this.docs = docs;
    }

    // Computes the unique words in documents
    @Override
    protected Set<String> compute() {
        List<RecursiveTask<Set<String>>> tasks = new ArrayList<>();

        // Create a separate task for each document if multiple are present
        if (this.docs.stream().count() > 1) {
            for (List<String> doc : docs) {
                ArrayList<List<String>> single = new ArrayList<>();
                single.add(doc);
                WordFinder task = new WordFinder(single);
                tasks.add(task);
                task.fork(); //asyn
            }
        } else if (docs.get(0).size() > 16000) {

            // Split large documents into smaller chunks
            tasks.addAll(splitSearch(docs.get(0)));
        } else if (tasks.isEmpty()) {

            // Process small single documents directly
            HashSet<String> uniqueWords = new HashSet<>();
            for (String line : docs.get(0)) {
                uniqueWords.addAll(extractWords(line));
            }
            return uniqueWords;
        }

        Set<String> result = tasks.get(0).join();
        tasks.remove(0);
        for (RecursiveTask<Set<String>> task : tasks) {
            result.retainAll(task.join());
        }
        return result;
    }

    // Extracts words from a line
    private static List<String> extractWords(String line) {
        String[] words = line.trim().split("\\s+");
        return Arrays.asList(words);
    }

    // Splits document into parts for separate processing
    private static ArrayList<WordFinder> splitSearch(List<String> words) {
        List<String> firstPart = words.subList(0, words.size() / 2);
        ArrayList<List<String>> halfDoc = new ArrayList<>();
        halfDoc.add(firstPart);

        List<String> secondPart = words.subList(words.size() / 2, words.size());
        ArrayList<List<String>> otherDoc = new ArrayList<>();
        otherDoc.add(secondPart);

        WordFinder firstTask = new WordFinder(halfDoc);
        WordFinder secondTask = new WordFinder(otherDoc);

        ArrayList<WordFinder> result = new ArrayList<>();
        result.add(secondTask);
        secondTask.fork();
        result.add(firstTask);
        firstTask.fork();

        return result;
    }
}
