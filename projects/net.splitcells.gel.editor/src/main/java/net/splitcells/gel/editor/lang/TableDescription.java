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
package net.splitcells.gel.editor.lang;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;

import static net.splitcells.dem.data.set.list.Lists.list;

public class TableDescription implements SourceCodeQuotation {
    public static TableDescription tableDescription(String name, List<ReferenceDescription<AttributeDescription>> header, SourceCodeQuote sourceCodeQuote) {
        return new TableDescription(name, header, sourceCodeQuote);
    }

    private final String name;
    private List<ReferenceDescription<AttributeDescription>> header = list();
    private final SourceCodeQuote sourceCodeQuote;

    private TableDescription(String argName, List<ReferenceDescription<AttributeDescription>> argHeader, SourceCodeQuote argSourceCodeQuote) {
        name = argName;
        header = argHeader;
        sourceCodeQuote = argSourceCodeQuote;
    }

    public String name() {
        return name;
    }

    public ListView<ReferenceDescription<AttributeDescription>> header() {
        return header;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", header: " + header;
    }

    @Override
    public SourceCodeQuote sourceCodeQuote() {
        return sourceCodeQuote;
    }
}
