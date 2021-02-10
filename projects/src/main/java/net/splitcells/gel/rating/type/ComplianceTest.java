package net.splitcells.gel.rating.type;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.*;
import static net.splitcells.gel.rating.type.Compliance.compliance;
import static org.assertj.core.api.Assertions.assertThat;

public class ComplianceTest {

    @Test
    public void testEquality() {
        assertThat(compliance(false)).isEqualTo(compliance(false));
        assertThat(compliance(false).combine(compliance(false))).isEqualTo(compliance(false));
        assertThat(compliance(false).combine(compliance(true))).isEqualTo(compliance(false));
        assertThat(compliance(true).combine(compliance(false))).isNotEqualTo(compliance(true));
    }

    @Test
    public void test_combinations_and_order_consistency() {
        assertThat(compliance(false).combine(compliance(false)).compare_partially_to(compliance(false)).get())
                .isEqualTo(EQUAL);
        assertThat(compliance(false).combine(compliance(true)).compare_partially_to(compliance(false)).get())
                .isEqualTo(EQUAL);
        assertThat(compliance(true).combine(compliance(false)).compare_partially_to(compliance(true)).get())
                .isNotEqualTo(EQUAL);
    }

    @Test
    public void test_order_consistency() {
        assertThat(compliance(true).compare_partially_to(compliance(true)).get()).isEqualTo(EQUAL);
        assertThat(compliance(false).compare_partially_to(compliance(false)).get()).isEqualTo(EQUAL);
        assertThat(compliance(false).compare_partially_to(compliance(true)).get()).isEqualTo(LESSER_THAN);
        assertThat(compliance(true).compare_partially_to(compliance(false)).get()).isEqualTo(GREATER_THAN);
    }

}
