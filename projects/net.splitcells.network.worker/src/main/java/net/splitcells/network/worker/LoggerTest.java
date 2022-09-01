package net.splitcells.network.worker;

import org.junit.jupiter.api.Test;

import static net.splitcells.network.worker.Logger.logger;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LoggerTest {
    @Test
    public void test() {
        final var testSubject = logger();
        assertThat(testSubject.parseTestIdentifier("[engine:junit-jupiter]/[class:net.splitcells.gel.test.functionality.NQueenProblemTest]/[method:test_8_queen_problem_with_repair()]"))
                .contains("net/splitcells/gel/test/functionality/NQueenProblemTest/test_8_queen_problem_with_repair");
    }
}
