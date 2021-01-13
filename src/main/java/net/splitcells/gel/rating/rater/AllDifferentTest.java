package net.splitcells.gel.rating.rater;

import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.attribute.AttributeI;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.AllDifferent.allDifferent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

public class AllDifferentTest extends TestSuiteI {

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_rating_with_all_values_different() {
        final Attribute<Integer> attribute = attribute(Integer.class);
        final Database lineSupplier = database(attribute);
        final Then testSubject = then(allDifferent(attribute));
        assertThat(testSubject.rating().equalz(noCost())).isTrue();
        assertThat(testSubject.defying()).isEmpty();
        testSubject.register_papildinājumi(lineSupplier.addTranslated(list(1)));
        testSubject.register_papildinājumi(lineSupplier.addTranslated(list(2)));
        assertThat(testSubject.defying()).isEmpty();
        assertThat(testSubject.rating().equalz(noCost())).isTrue();
        // TODO Test removal.
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_rating_with_all_values_same() {
        final Attribute<Integer> attribute = attribute(Integer.class);
        final Database lineSupplier = database(attribute);
        final Then testSubject = then(allDifferent(attribute));
        assertThat(testSubject.rating().equalz(noCost())).isTrue();
        testSubject.register_papildinājumi(lineSupplier.addTranslated(list(1)));
        testSubject.register_papildinājumi(lineSupplier.addTranslated(list(1)));
        assertThat(testSubject.rating().equalz(cost(2.0))).isTrue();
        // TODO Test removal.
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_rating_with_some_values_same() {
        final var attribute = attribute(Integer.class);
        final var lineSupplier = database(attribute);
        final var testSubject = then(allDifferent(attribute));
        assertThat(testSubject.rating().equalz(noCost())).isTrue();
        testSubject.register_papildinājumi(lineSupplier.addTranslated(list(1)));
        testSubject.register_papildinājumi(lineSupplier.addTranslated(list(1)));
        testSubject.register_papildinājumi(lineSupplier.addTranslated(list(2)));
        assertThat(testSubject.rating().equalz(cost(2.0))).isTrue();
        // TODO Test removal.
    }
}
