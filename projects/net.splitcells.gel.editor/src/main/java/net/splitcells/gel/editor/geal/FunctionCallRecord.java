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
import lombok.experimental.Accessors;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;

import static net.splitcells.dem.lang.CommonMarkUtils.joinDocuments;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

@Accessors(chain = true)
public class FunctionCallRecord implements Closeable {
    public static FunctionCallRecord functionCallRecord(FunctionCallDoc argFunctionCallDoc, String argName, int argVariation) {
        return new FunctionCallRecord(argFunctionCallDoc, argName, argVariation);
    }

    @Getter private final String name;
    /**
     * {@link #name} + {@link #variation} is the id.
     * A function with a name can have multiple variation,
     * where each one has different arguments.
     */
    @Getter private final int variation;
    /**
     * This is a CommonMark document.
     */
    private StringBuilder description = StringUtils.stringBuilder();
    private final FunctionCallDoc functionCallDoc;

    private FunctionCallRecord(FunctionCallDoc argFunctionCallDoc, String argName, int argVariation) {
        name = argName;
        variation = argVariation;
        functionCallDoc = argFunctionCallDoc;
    }

    public FunctionCallRecord addDescription(String addition) {
        joinDocuments(description, addition);
        return this;
    }

    public void requireArgumentCount(FunctionCallDesc functionCall, int requiredArgumentCount) {
        if (functionCall.getArguments().size() != requiredArgumentCount) {
            throw execException(tree("The "
                    + functionCall.getName().getValue()
                    + " function requires exactly "
                    + requiredArgumentCount
                    + " arguments, but "
                    + functionCall.getArguments().size()
                    + " were given.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
    }

    @Override
    public void close() {
        functionCallDoc.addRecord(this);
    }
}
