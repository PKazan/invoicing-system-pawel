package pl.futurecollars.invoicing.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FilesService {

    public void appendLineToFile(Path path, String line) throws IOException {
        Files.writeString(path, line + System.lineSeparator(), StandardOpenOption.APPEND);
    }

    public void writeToFile(Path path, String line) throws IOException {
        Files.writeString(path, line, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void writeLinesToFile(Path path, List<String> lines) throws IOException {
        Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public List<String> readAllLines(Path path) throws IOException {
        return Files.readAllLines(path);
    }

}
