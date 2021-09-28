package net.splitcells.dem.utils;

import net.splitcells.dem.data.set.list.Lists;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.MathUtils.sumsForTarget;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MathUtilsTest {

    @Test
    public void testSumsForTargetWithOneResult() {
        assertThat(sumsForTarget(1, list(1))).isEqualTo(list(list(1)));
    }

    @Test
    public void testSumsForTargetWithMultipleResults() {
        assertThat(sumsForTarget(2, list(1))).isEqualTo(list(list(1, 1)));
        assertThat(sumsForTarget(3, list(1, 2, 3)))
                .isEqualTo(list
                        (list(1, 1, 1)
                                , list(1, 2)
                                , list(2, 1)
                                , list(3)));
    }
}
