package net.splitcells.dem.data.atom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class BoolsTest {
    /**
     * If condition is met nothing happens.
     */
    @Test
    public void testRequiredTruth() {
        Bools.require(true);
    }

    @Test
    public void testRequiredUntruth() {
        assertThrows(AssertionError.class, () -> Bools.require(false));
    }
}
