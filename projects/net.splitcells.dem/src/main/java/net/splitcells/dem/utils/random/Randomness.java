package net.splitcells.dem.utils.random;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.Random;
import java.util.stream.IntStream;

import static java.lang.Math.abs;
import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static org.assertj.core.api.Assertions.assertThat;

public interface Randomness extends BasicRndSrc {

    default boolean truthValue() {
        return 1 == integer(0, 1);
    }

    default boolean truthValue(float chance) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(chance)
                    .withFailMessage("Invalid chance given.")
                    .isBetween(0f, 1f);
        }
        final int scaleFactor = 10_000;
        final var scaledChance = chance * scaleFactor;
        return scaledChance >= integer(0, scaleFactor);
    }

    default String readableAsciiString(int size) {
        final StringBuilder rBase = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            rBase.append((char) asRandom().nextInt(126));
        }
        return rBase.toString();
    }

    @JavaLegacy
    Random asRandom();

    default <T> List<T> chooseAtMostMultipleOf(int numberOfThingsToChoose, List<T> args) {
        if (args.isEmpty()) {
            return list();
        }
        if (numberOfThingsToChoose >= args.size()) {
            return listWithValuesOf(args);
        }
        final List<T> chosenArgs = list();
        range(0, numberOfThingsToChoose)
                .forEach(i -> {
                    final var nextIndexCandidate = this.integer(0, args.size() - 1);
                    chosenArgs.add(args.remove(nextIndexCandidate));
                });
        return chosenArgs;
    }

    default <T> T chooseOneOf(java.util.List<T> arg) {
        if (arg.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return arg.get(asRandom().nextInt(arg.size()));
    }

    default <T> T chooseOneOf(List<T> arg) {
        if (arg.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return arg.get(asRandom().nextInt(arg.size()));
    }

    default <T> T removeOneOf(List<T> arg) {
        if (arg.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return arg.remove(asRandom().nextInt(arg.size()));
    }

    static void assertPlausibility(float chance, int tries, int positives) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(chance).isBetween(0f, 1f);
            assertThat(tries).isGreaterThan(-1);
            assertThat(positives).isGreaterThan(-1);
        }
        final var actualChance = abs((float) positives / (float) tries);
        final var chanceDiff = actualChance - chance;
        final var isPlausible = chanceDiff < 0.1;
        assertThat(isPlausible).withFailMessage("actualChance: " + actualChance + "chanceDiff: " + chanceDiff).isTrue();
    }
}
