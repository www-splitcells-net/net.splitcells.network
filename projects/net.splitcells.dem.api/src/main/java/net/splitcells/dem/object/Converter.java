/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
