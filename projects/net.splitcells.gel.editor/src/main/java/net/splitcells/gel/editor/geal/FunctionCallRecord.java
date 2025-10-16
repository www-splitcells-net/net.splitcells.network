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
package net.splitcells.gel.editor.geal;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.splitcells.dem.utils.StringUtils;

import static net.splitcells.dem.lang.CommonMarkUtils.joinDocuments;

@Accessors(chain = true)
public class FunctionCallRecord {
    public static FunctionCallRecord functionCallRecord(String argName) {
        return new FunctionCallRecord(argName);
    }

    @Getter private final String name;
    /**
     * This is a CommonMark document.
     */
    @Getter private StringBuilder description = StringUtils.stringBuilder();

    private FunctionCallRecord(String argName) {
        name = argName;
    }

    public FunctionCallRecord addDescription(String addition) {
        joinDocuments(description, addition);
        return this;
    }
}
