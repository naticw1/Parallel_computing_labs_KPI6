import java.util.ArrayList;
import java.util.List;

public class Document {
    private final String name; // Name of the document
    private final List<String> content; // Content lines of the document

    public Document(String name) {
        this.name = name;
        this.content = new ArrayList<>();
    }

    // Adds a line of text to the document
    public void addLine(String line) {
        this.content.add(line);
    }

    // Returns the lines of text from the document
    public List<String> getLines() {
        return content;
    }

    // Returns the name of the document
    public String getDocumentName() {
        return name;
    }
}
