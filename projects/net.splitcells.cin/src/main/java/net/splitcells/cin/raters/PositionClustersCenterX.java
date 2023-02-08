package net.splitcells.cin.raters;

import net.splitcells.dem.data.set.map.typed.TypedKey;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class PositionClustersCenterX implements TypedKey<Integer> {
    private PositionClustersCenterX() {
        throw constructorIllegal();
    }
}
