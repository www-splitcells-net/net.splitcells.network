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

import lombok.experimental.Accessors;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * Extracts function call data from {@link FunctionCallDesc},
 * while recording all types of function calls, in order to generate a complete function call documentation.
 */
@Accessors(chain = true)
public class FunctionCallDoc {
    public static FunctionCallDoc functionCallDoc() {
        return new FunctionCallDoc();
    }

    private final List<FunctionCallRecord> functionCallRecords = list();

    private FunctionCallDoc() {

    }

    private FunctionCallDoc addRecord(FunctionCallRecord record) {
        final var matches = functionCallRecords.stream()
                .filter(fcr -> fcr.getName().equals(record.getName()) && fcr.getVariation() == record.getVariation())
                .toList();
        if (matches.hasElements()) {
            throw execException(tree("Function call variation is recorded multiple times.")
                    .withProperty("New record", record.toString())
                    .withProperty("Existing matching records", matches.toString()));
        }
        functionCallRecords.add(record);
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
}
