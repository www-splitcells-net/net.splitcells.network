package net.splitcells.dem.testing;

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.data.set.list.List;
import org.assertj.core.api.Condition;

import java.util.function.Predicate;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static org.assertj.core.api.Assertions.assertThat;

public class Assertions {
    private Assertions() {
        throw constructorIllegal();
    }

    public static <T> void assertComplies(T subject, Predicate<T> constraint, String description) {
        assertThat(subject).is(new Condition<T>(constraint, description));
    }
}
