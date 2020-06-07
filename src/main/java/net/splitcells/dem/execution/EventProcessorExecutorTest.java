package net.splitcells.dem.execution;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static net.splitcells.dem.execution.EventProcessorExecutor.eventProcessorExecutor;
import static org.assertj.core.api.Assertions.assertThat;

public class EventProcessorExecutorTest {
    @Test
    public void test() {
        final var testSubject = eventProcessorExecutor();
        testSubject.start();
        final var counter = new AtomicInteger(0);
        IntStream.rangeClosed(1, 3).forEach(i ->
                testSubject.register(new EventProcessor() {
                    @Override
                    public void processEvents() {
                        counter.addAndGet(1);
                        testSubject.register(new EventProcessor() {
                            @Override
                            public void processEvents() {
                                counter.addAndGet(1);
                            }
                        });
                    }
                })
        );
        assertThat(counter).hasValue(3 * 2);
    }
}
