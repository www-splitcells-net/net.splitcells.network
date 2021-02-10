package net.splitcells.gel.data.allocations;

import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.Databases;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.gel.data.allocation.Allocationss.allocations;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AllocationsTest extends TestSuiteI {

    @Tag(UNIT_TEST)
    @Test
    public void test_subscribe_to_afterAdditions() {
        final var demands = Databases.database();
        final var supplies = Databases.database();
        final var allocations = allocations("test", demands, supplies);
        allocations.subscribe_to_afterAdditions(
                pieķiršana -> assertThat(allocations.demand_of_allocation(pieķiršana)).isNotNull());
        allocations.allocate
                (demands.addTranslated(list())
                        , supplies.addTranslated(list()));
    }

    @Tag(UNIT_TEST)
    @Test
    public void test_subscriber_to_beforeRemoval() {
        final var demands = Databases.database();
        final var supplies = Databases.database();
        final var allocations = allocations("test", demands, supplies);
        allocations.subscriber_to_beforeRemoval
                (pieķiršana -> assertThat(allocations.demand_of_allocation(pieķiršana)).isNotNull());
        allocations.remove(
                allocations.allocate
                        (demands.addTranslated(list())
                                , supplies.addTranslated(list()))
        );
    }

    @Tag(UNIT_TEST)
    @Test
    public void test_subscriber_to_afterRemoval() {
        assertThrows(Exception.class, () -> {
            final var demands = Databases.database();
            final var supplies = Databases.database();
            final var allocations = allocations("test", demands, supplies);
            allocations.subscriber_to_afterRemoval
                    (pieķiršana -> assertThat(allocations.demand_of_allocation(pieķiršana)));
            allocations.remove(
                    allocations.allocate
                            (demands.addTranslated(list())
                                    , supplies.addTranslated(list()))
            );
        });
    }

    @Tag(UNIT_TEST)
    @Test
    public void test_allocate_and_remove() {
        final var demandAttribute = attribute(Double.class);
        final var demands = Databases.database(demandAttribute);
        demands.addTranslated(list(1.0));
        final var supplyAttribute = attribute(Integer.class);
        final var supplies = Databases.database(supplyAttribute);
        supplies.addTranslated(list(2));
        final var testSubject = allocations("test", demands, supplies);
        assertThat(testSubject.headerView())
                .containsExactly(demands.headerView().get(0), supplies.headerView().get(0));
        assertThat(testSubject.demands().size()).isEqualTo(1);
        assertThat(testSubject.supplies().size()).isEqualTo(1);
        demands.addTranslated(list(3.0));
        supplies.addTranslated(list(4));
        assertThat(testSubject.demands().size()).isEqualTo(2);
        assertThat(testSubject.supplies().size()).isEqualTo(2);
        final var firstAllocation = testSubject
                .allocate(testSubject.demands().rawLinesView().get(0)
                        , testSubject.supplies().rawLinesView().get(0));
        assertThat(firstAllocation.value(demandAttribute)).isEqualTo(1.0);
        assertThat(firstAllocation.value(supplyAttribute)).isEqualTo(2);
        final var secondAllocation = testSubject
                .allocate(testSubject.demands().rawLinesView().get(1)
                        , testSubject.supplies().rawLinesView().get(1));
        assertThat(secondAllocation.value(demandAttribute)).isEqualTo(3.0);
        assertThat(secondAllocation.value(supplyAttribute)).isEqualTo(4);
        assertThat(testSubject.size()).isEqualTo(2);
        testSubject.remove(firstAllocation);
        assertThat(testSubject.size()).isEqualTo(1);
    }
}
