package net.splitcells.dem.resource.communication.interaction;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LogLevelTest {
    @Test
    public void testPrioritySmallerThanOrder() {
        assertThat(LogLevel.ERROR.smallerThan(LogLevel.DEBUG)).isTrue();
        assertThat(LogLevel.DEBUG.smallerThan(LogLevel.DEBUG)).isFalse();
        assertThat(LogLevel.DEBUG.smallerThan(LogLevel.ERROR)).isFalse();
    }
}
