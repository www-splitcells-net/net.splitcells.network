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

import net.splitcells.gel.editor.lang.SourceCodeQuotation;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

public sealed interface ExpressionDesc extends SourceCodeQuotation permits FunctionCallDesc
        , IntegerDesc
        , NameDesc
        , StringDesc {
    default <T extends ExpressionDesc> T to(Class<T> clazz) {
        if (this.getClass().isAssignableFrom(clazz)) {
            return (T) this;
        }
        throw execException(tree("Incompatible types.")
                .withProperty("this type", getClass().toString())
                .withProperty("given type", clazz.toString()));
    }
}
