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
package net.splitcells.gel.data.table.attribute;

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.testing.Result;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Mocking.anyClass;
import static net.splitcells.dem.testing.Mocking.anyString;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.gel.data.table.attribute.AttributeI.*;

public class Attributes {
	public static Attribute<?> attributeATO() {
		return AttributeI.attribute(anyClass(), anyString());
	}

	public static Result<Attribute<? extends Object>, Tree> parseAttribute(String name, String type) {
		final Result<Attribute<? extends Object>, Tree> parsedAttribute = result();
		if (type.equals("int") || type.equals("integer")) {
			return parsedAttribute.withValue(integerAttribute(name));
		} else if (type.equals("float")) {
			return parsedAttribute.withValue(floatAttribute(name));
		} else if (type.equals("string")) {
			return parsedAttribute.withValue(stringAttribute(name));
		}
		return parsedAttribute.withErrorMessage(tree("Unknown attribute type.")
				.withProperty("name", name)
				.withProperty("type", type));
	}
}
