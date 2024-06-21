import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) throws IOException {
        // Read text files from a directory for comparison
        List<Document> docs = readTxtFiles("compare");

        // Create a ForkJoinPool with a number of threads equal to available processors
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        // Perform parallel searching and collect results
        Set<String> foundFiles = pool.invoke(new ParallelSearchingTask(docs));

        // Output the results
        System.out.println("Keywords were found in the next files:");
        for (String path : foundFiles) {
            // Extract file name from path
            int lastBackslash = path.lastIndexOf("\\");
            String fileName = path.substring(lastBackslash + 1);
            System.out.println(fileName);
        }
    }

    // Reads all .txt files in a given directory and returns a list of Document objects
    public static List<Document> readTxtFiles(String dirPath) {
        File dir = new File(dirPath);
        List<Document> filesList = new ArrayList<>();

        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                Document doc = new Document(file.getName());
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        doc.addLine(line);
                    }
                    filesList.add(doc);
                } catch (IOException e) {
                    System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
                }
            }
        }

        return filesList;
    }
}
