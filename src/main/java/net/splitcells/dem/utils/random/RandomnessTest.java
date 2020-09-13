package net.splitcells.dem.utils.random;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.Randomness.assertPlausibility;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RandomnessTest {
    @Test
    public void testTruthValueOfChanceOfZero() {
        assertThat(randomness().truthValue(0)).isFalse();
    }

    @Test
    public void testTruthValueOfChanceOfHundredPercent() {
        assertThat(randomness().truthValue(1)).isTrue();
    }

    @Test
    public void testTruthValueOf() {
        final var chance = 0.75f;
        final var randomness = randomness();
        final var truthCounter = new AtomicInteger(0);
        final var runs = 1_000_000;
        final var deviation = 0.1f;
        rangeClosed(1, runs).forEach(i -> {
            if (randomness.truthValue(chance)) {
                truthCounter.incrementAndGet();
            }
        });
        assertThat(truthCounter.get()).isLessThan((int) ((chance + deviation) * runs));
        assertThat(truthCounter.get()).isGreaterThan((int) ((chance - deviation) * runs));
    }

    @Test
    public void testAssertPlausibilityWithPlausible() {
        assertPlausibility(.5f, 100, 45);
    }

    @Test
    public void testAssertPlausibilityWithImplausible() {
        assertThrows(Error.class, () -> assertPlausibility(.1f, 100, 30));
    }

    @Test
    public void test_chooseOneOf_on_empty_list() {
        assertThrows(IllegalArgumentException.class, () -> randomness().chooseOneOf(list()));
    }

    @Test
    public void test_removeOneOf_on_empty_list() {
        assertThrows(IllegalArgumentException.class, () -> randomness().removeOneOf(list()));
    }
}
