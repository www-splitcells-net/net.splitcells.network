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
package net.splitcells.dem.object;

import static net.splitcells.dem.object.Merger.merger;

/**
 * Defines conversion between types without the explicit need of reflection.
 * Currently, only flat objects are supported, but tree object support could be added later.
 */
public class Converter {

    public static Converter converter() {
        return new Converter();
    }

    private Converter() {

    }

    /**
     * @param input  Defines the values for the output.
     * @param output Defines the default values of the output. This object is also returned.
     * @return This is the input converted to the output.
     */
    public <Input extends Convertible, Output extends Convertible> Output convert(Input input, Output output) {
        output.merge(input.merge(merger()).withIsRecording(false));
        return output;
    }
}
