/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view.attribute;

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.testing.Result;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Mocking.anyClass;
import static net.splitcells.dem.testing.Mocking.anyString;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.data.view.attribute.AttributeI.*;

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

	private Attributes()  {
		throw constructorIllegal();
	}
}
