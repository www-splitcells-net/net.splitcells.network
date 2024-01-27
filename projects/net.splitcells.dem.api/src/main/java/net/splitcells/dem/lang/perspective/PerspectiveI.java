/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.namespace.NameSpace;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.WARNING;
import static net.splitcells.dem.lang.namespace.NameSpaces.TEXT;
import static net.splitcells.dem.resource.communication.log.Logs.logs;

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

	/**
	 * @return Returns a descriptive string representation of this.
	 *
	 */
	@Override
	public String toString() {
		if (WARNING) {
			logs().appendUnimplementedWarning(PerspectiveI.class);
		}
		return toXmlString();
	}
}
