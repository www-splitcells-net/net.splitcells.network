package net.splitcells.network.worker;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.host.SystemUtils;
import net.splitcells.dem.testing.ReportEntryKey;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.resource.ContentType.CSV;
import static net.splitcells.dem.resource.Files.appendToFile;
import static net.splitcells.dem.resource.Files.createDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Files.writeToFile;
import static net.splitcells.dem.testing.ReportEntryKey.END_TIME;
import static net.splitcells.dem.testing.ReportEntryKey.START_TIME;
import static net.splitcells.dem.testing.ReportEntryTimeKey.DATE_TIME_FORMAT;

/**
 * <p>Logs the runtime of tests into a project folder
 * and commits these logs.
 * It is assumed, that the folder of the project is under version control.</p>
 * <p>The entries are stored at "./src/main/csv/**".
 * The relative path of each entry represents the package, the test case and the executor of the test:
 * "./src/main/csv/<package>/<test case>/<id of the executor>.csv".
 * The executors hardware and software should not change between each run,
 * in order to make it easily possible to find regressions.</p>
 * <p>Each CSV file contains the 2 columns "Date" and "Execution Time".
 * The first one has the executions date of the test case.
 * The second one contains the execution time for the corresponding test case.</p>
 * <p>{@link #BUILDER_RUNTIME_LOG}"/<host>.csv" contains the start times of all runs for a given host.
 * </p>
 */
public class Logger implements TestExecutionListener {
    private static Pattern UNIQUE_ID = Pattern.compile("(\\[.*\\])(/)(\\[[a-zA-Z]*:)(.*)(\\])(/)(\\[[a-zA-Z-]*:)([a-zA-Z-_]*)(.*\\])");

    public static Logger logger(Path logProject) {
        return new Logger(logProject);
    }
    
    private static final String BUILDER_RUNTIME_LOG = "net/splitcells/network/logger/builder/runtime";

    private final Path logProject;
    private final Map<TestIdentifier, Long> testToStartTime = map();

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
            writeToFile(projectPath, ("Date," + resultType + Files.newLine()).getBytes(StandardCharsets.UTF_8));
        }
        appendToFile(projectPath, (localDate + "," + result + Files.newLine()).getBytes(StandardCharsets.UTF_8));
    }

    public String readExecutionResults(String subject, String executor) {
        final var projectPath = logProject
                .resolve("src/main/" + CSV.codeName())
                .resolve(subject)
                .resolve(executor + "." + CSV.codeName());
        createDirectory(logProject.resolve(projectPath).getParent());
        return Files.readFileAsString(projectPath);
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        testToStartTime.put(testIdentifier, System.nanoTime());
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        final var splitTestIdentifier = list(testIdentifier.getUniqueId().split("/"));
        final Optional<String> testPath;
        if (splitTestIdentifier.size() == 1) {
            testPath = Optional.of(BUILDER_RUNTIME_LOG);
        } else {
            testPath = splitTestIdentifier
                    .withRemovedByIndex(0)
                    .stream()
                    .map(e -> e.replace("[", ""))
                    .map(e -> e.replace("]", ""))
                    .map(e -> e.split(":")[1])
                    .map(e -> e.replace(".", "/"))
                    .map(e -> e.replaceAll("[^a-zA-Z-_/]", "_"))
                    .reduce((a, b) -> a + "/" + b)
                    .map(e -> e.replaceAll("/+", "/"));
        }
        if (testPath.isPresent()) {
            final var endDateTime = System.nanoTime();
            final var startDateTime = testToStartTime.get(testIdentifier);
            final var runTime = (endDateTime - startDateTime) / 1_000_000_000d;
            logExecutionResults(testPath.get()
                    , config().configValue(ProgramName.class)
                    , LocalDate.now()
                    , "Execution Time"
                    , runTime);
        }

    }

    /**
     * Some systems may not have added an OS state interface installation to the PATH environmental variable of non-interactive shells.
     * Therefore, the standard `~/bin/net.splitcells.os.state.interface.commands.managed/command.managed.export.bin` command is used,
     * in order to extend the PATH variable accordingly only inside the current shell session.
     */
    public void commit() {
        SystemUtils.executeShellScript("sh -c \". ~/bin/net.splitcells.os.state.interface.commands.managed/command.managed.export.bin; command.managed.execute.command repo.commit.all\"", logProject);
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        commit();
    }

}
