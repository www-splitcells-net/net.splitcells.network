/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.resource.communication.interaction.Sui;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO Test and use this.
 *
 * TODO Use {@link net.splitcells.dem.lang.dom.Domable#toDom} for rendering.
 */
@Deprecated
public class PerspectiveXmlRenderer implements Sui<Perspective> {
	public static PerspectiveXmlRenderer perspectiveXmlRenderer(Sender<String> output) {
		return new PerspectiveXmlRenderer(output, false);
	}

	private static final String INDENT = "   ";

	private final Sender<String> output;
	private final Map<NameSpace, String> nameSpace_to_prefix;
	private final Map<NameSpace, String> new_nameSpace_to_prefix = map();

	private PerspectiveXmlRenderer(Sender<String> output, boolean startOfDocument) {
		this.output = output;
		nameSpace_to_prefix = map();
		if (startOfDocument) {
			output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		}
	}

	private PerspectiveXmlRenderer(Sender<String> output, Map<NameSpace, String> nameSpace_to_prefix) {
		this.output = output;
		this.nameSpace_to_prefix = nameSpace_to_prefix;
	}

	public String prefix(NameSpace nameSpace) {
		if (!nameSpace_to_prefix.containsKey(nameSpace)) {
			if (nameSpace_to_prefix.containsValue(nameSpace.defaultPrefix())) {
				throw notImplementedYet();
			}
			nameSpace_to_prefix.put(nameSpace, nameSpace.defaultPrefix());
			new_nameSpace_to_prefix.put(nameSpace, nameSpace.defaultPrefix());
		}
		return nameSpace_to_prefix.get(nameSpace);
	}

	public String renderedValue(Perspective arg) {
		return prefix(arg.nameSpace()) + ":" + arg.name() + newNameSpaceDeclerationsInElement();
	}

	public String newNameSpaceDeclerationsInElement() {
		final var rVal = new_nameSpace_to_prefix
				.entrySet().stream()//
				.map(entry -> "xmlns:" + entry.getValue() + "=\"" + entry.getKey().uri() + "\"")//
				.reduce((a, b) -> a + " " + b);//
		new_nameSpace_to_prefix.clear();
		if (rVal.isPresent()) {
			return " " + rVal.get();
		} else {
			return "";
		}
	}

	@Override
	public <R extends ListWA<Perspective>> R append(Perspective arg) {
		if (arg.nameSpace().equals(NameSpaces.TEXT)) {
			assertThat(arg.children()).isEmpty();
			output.append(arg.name());
		} else if (arg.children().isEmpty()) {
			output.append("<" + renderedValue(arg) + "/>");
		} else {
			output.append("<" + renderedValue(arg) + ">");
			try (var valueRenderer = new PerspectiveXmlRenderer(Sender.extend(output, INDENT, ""),
					map(nameSpace_to_prefix))) {
				arg.children().forEach(v -> valueRenderer.append(v));
			}
			output.append(INDENT + "</" + renderedValue(arg) + ">");
		}
		return (R) this;
	}

	@Override
	public void close() {
		throw notImplementedYet();
	}
}
