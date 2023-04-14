package net.splitcells.cin;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.testing.Assertions.requireIllegalDefaultConstructor;

public class CinTest {
    @UnitTest
    public void testIllegalConstructor() {
        requireIllegalDefaultConstructor(Cin.class);
    }
}
