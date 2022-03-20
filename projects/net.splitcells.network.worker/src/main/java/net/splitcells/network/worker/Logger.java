package net.splitcells.network.worker;

import net.splitcells.dem.resource.Files;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;

import static net.splitcells.dem.resource.ContentType.CSV;
import static net.splitcells.dem.resource.Files.appendToFile;
import static net.splitcells.dem.resource.Files.createDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Files.writeToFile;

public class Logger {
    public static Logger logger(Path logProject) {
        return new Logger(logProject);
    }

    private final Path logProject;

    private Logger(Path logProject) {
        this.logProject = logProject;
    }

    public void logExecutionResults(String subject, String executor, LocalDate localDate, String resultType, double result) {
        final var projectPath = logProject
                .resolve("src/main/" + CSV.codeName())
                .resolve(subject)
                .resolve(executor + "." + CSV.codeName());
        createDirectory(logProject.resolve(projectPath).getParent());
        if (!is_file(projectPath)) {
            writeToFile(projectPath, ("Date," + resultType).getBytes(StandardCharsets.UTF_8));
        }
        appendToFile(projectPath, (localDate + "," + result + Files.newLine()).getBytes(StandardCharsets.UTF_8));
    }
}
