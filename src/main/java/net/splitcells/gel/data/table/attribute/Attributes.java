package net.splitcells.gel.data.table.attribute;

import static net.splitcells.dem.testing.Mocking.anyClass;
import static net.splitcells.dem.testing.Mocking.anyString;

public class Attributes {
	public static Attribute<?> attributeATO() {
		return AttributeI.attribute(anyClass(), anyString());
	}
}
