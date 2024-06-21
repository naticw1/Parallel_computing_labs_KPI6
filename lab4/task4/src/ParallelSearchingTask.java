import java.util.*;
import java.util.concurrent.RecursiveTask;

public class ParallelSearchingTask extends RecursiveTask<Set<String>> {
    private final List<Document> documents;
    // IT keywords for searching
    private List<String> keywords = Arrays.asList(
            "Artificial Intelligence", "Cybersecurity", "Machine Learning", "Data Science", "Cloud Computing",
            "Internet of Things", "Virtual Reality", "Big Data", "Blockchain", "Computer Networking",
            "Software Development", "User Experience Design", "Mobile Application Development",
            "Operating Systems", "Database Management", "Web Development", "Computer Graphics",
            "Information Systems", "Human-Computer Interaction", "Game Development", "Programming Languages",
            "Algorithms and Data Structures", "Computer Architecture", "Computer Organization",
            "Object-Oriented Programming", "Operating Systems Concepts", "Computer Networks and Communications",
            "Information Technology Management", "Web Design and Development Fundamentals", "Software Testing and Quality Assurance",
            "Data Modeling and Database Design", "Computer Graphics Principles and Practice", "Digital Signal Processing",
            "Multimedia Computing", "Artificial Neural Networks", "Computer Vision and Image Processing",
            "Internet Technologies and Applications", "Systems Analysis and Design", "Project Management for IT",
            "IT Governance and Risk Management", "Coding", "Cloud", "Cyber", "Data", "Security", "IoT", "VR", "AI",
            "ML", "Blockchain", "UX", "Web Dev", "Mobile", "Network", "OS", "Database", "Graphics", "IT Management", "Programming",
            "Software Testing", "IT Governance"
    );

    public ParallelSearchingTask(List<Document> docs) {
        this.documents = docs;
    }

    @Override
    protected Set<String> compute() {
        List<RecursiveTask<Set<String>>> tasks = new ArrayList<>();

        if (this.documents.size() > 1) {
            // Split task for multiple documents
            for (Document doc : documents) {
                List<Document> singleDocList = new ArrayList<>();
                singleDocList.add(doc);
                ParallelSearchingTask subTask = new ParallelSearchingTask(singleDocList);
                tasks.add(subTask);
                subTask.fork(); // initiate concurrent execution
            }
        } else {
            // Process a single document
            List<String> lines = documents.get(0).getLines();
            Set<String> results = new HashSet<>();
            for (String line : lines) {
                List<String> words = splitLine(line);
                if (words.stream().anyMatch(word -> keywords.contains(word))) {
                    results.add(documents.get(0).getDocumentName());
                    break;
                }
            }
            return results;
        }

        // Join all results from subtasks
        Set<String> finalResults = tasks.get(0).join();
        tasks.remove(0);
        for (RecursiveTask<Set<String>> task : tasks) {
            finalResults.addAll(task.join());
        }
        return finalResults;
    }

    // Helper method to extract words from a line
    private static List<String> splitLine(String line) {
        String[] words = line.trim().split("\\s+");
        return Arrays.asList(words);
    }
}
