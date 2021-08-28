package net.splitcells.dem.resource;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PathsTest {
    @Test
    public void testRemoveFileSuffix() {
        assertThat(Paths.removeFileSuffix("name.suffix")).isEqualTo("name");
    }
}
