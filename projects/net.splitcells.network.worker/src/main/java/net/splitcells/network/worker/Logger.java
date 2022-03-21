package net.splitcells.network.worker;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.host.SystemUtils;
import net.splitcells.dem.testing.ReportEntryKey;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

import static net.splitcells.dem.resource.ContentType.CSV;
import static net.splitcells.dem.resource.Files.appendToFile;
import static net.splitcells.dem.resource.Files.createDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Files.writeToFile;
import static net.splitcells.dem.testing.ReportEntryKey.END_TIME;
import static net.splitcells.dem.testing.ReportEntryKey.START_TIME;
import static net.splitcells.dem.testing.ReportEntryTimeKey.DATE_TIME_FORMAT;

public class Logger implements TestExecutionListener {
    private static Pattern UNIQUE_ID = Pattern.compile("(\\[.*\\])(/)(\\[[a-zA-Z]*:)(.*)(\\])(/)(\\[[a-zA-Z-]*:)([a-zA-Z-_]*)(.*\\])");

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
        SystemUtils.executeProgram(Lists.list("repo.commit.all"), logProject);
    }

    @Override
    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
        final var endDateTime = ZonedDateTime.parse(entry.getKeyValuePairs().get(END_TIME.keyString()), DATE_TIME_FORMAT);
        final var startDateTime = ZonedDateTime.parse(entry.getKeyValuePairs().get(START_TIME.keyString()), DATE_TIME_FORMAT);
        final var runTime = ChronoUnit.NANOS.between(startDateTime, endDateTime) / 1_000_000d;
        System.out.println(testIdentifier.getLegacyReportingName());
        final var uniqueIdMatch = UNIQUE_ID.matcher(testIdentifier.getUniqueId());
        if (!uniqueIdMatch.matches()) {
            throw new RuntimeException(testIdentifier.getUniqueId());
        }
        logExecutionResults(uniqueIdMatch.group(4).replace(".", "/") + "/" + uniqueIdMatch.group(8)
                , "test"
                , LocalDate.now()
                , "Execution Time"
                , runTime);
    }
}
