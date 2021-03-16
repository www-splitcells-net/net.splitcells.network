package net.splitcells.dem.execution;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static net.splitcells.dem.execution.EventProcessorExecutor.eventProcessorExecutor;
import static org.assertj.core.api.Assertions.assertThat;

@Deprecated
public class EventProcessorExecutorTest {
    @Test
    public void test() {
        final var testResult = new AtomicInteger(0);
        try (final EventProcessorExecutor testSubject = eventProcessorExecutor()) {
            testSubject.start();
            final var f = new Semaphore(1);
            IntStream.rangeClosed(1, 3).forEach(i ->
                    testSubject.register(new EventProcessor() {
                        @Override
                        public void processEvents() {
                            testResult.addAndGet(1);
                            testSubject.register(new EventProcessor() {
                                @Override
                                public void processEvents() {
                                    testResult.addAndGet(1);
                                    if (i == 3) {
                                        f.release();
                                    }
                                }
                            });
                        }
                    })
            );
            try {
                f.acquire(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        assertThat(testResult.get()).isEqualTo(3 * 2);
    }
}
