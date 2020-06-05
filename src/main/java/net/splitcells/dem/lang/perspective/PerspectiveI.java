package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpace;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.TEXT;

public class PerspectiveI implements Perspective {
	public static Perspective perspective(String value, NameSpace nameSpace) {
		return new PerspectiveI(value, nameSpace);
	}

	public static Perspective perspective(String value) {
		return new PerspectiveI(value, TEXT);
	}

	private final String value;
	private final NameSpace nameSpace;
	private final List<Perspective> children = list();

	private PerspectiveI(String value, NameSpace nameSpace) {
		this.value = value;
		this.nameSpace = nameSpace;
	}

	@Override
	public NameSpace nameSpace() {
		return nameSpace;
	}

	@Override
	public String name() {
		return value;
	}

	@Override
	public List<Perspective> children() {
		return children;
	}

	@Override
	public String toString() {
		return Xml.toPrettyString(toDom());
	}
}
