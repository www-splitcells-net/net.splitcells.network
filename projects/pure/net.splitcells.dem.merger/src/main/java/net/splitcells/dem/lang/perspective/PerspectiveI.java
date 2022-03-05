/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
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

	protected PerspectiveI(String value, NameSpace nameSpace) {
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
