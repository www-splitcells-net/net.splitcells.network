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

import net.splitcells.dem.data.set.map.Map;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.map.Maps.map;

/**
 * Records the current attributes of an object or provides the current attributers of an object.
 * In other words, each {@link #merge(String, int)} method is used, in order to read values of an object
 * or to provide the values of an object.
 * This way the serialization and de-serialization of an object can be defined by one method, instead of two.
 */
public class Merger {
    public static Merger merger() {
        return new Merger();
    }

    private boolean isRecording = true;
    private final Map<String, Integer> intAttributes = map();
    private final Map<String, String> stringAttributes = map();

    private Merger() {

    }

    public Merger withIsRecording(boolean arg) {
        isRecording = arg;
        return this;
    }

    public Merger requireIsRecording() {
        require(isRecording);
        return this;
    }

    public int merge(String attribute, int value) {
        if (isRecording) {
            intAttributes.put(attribute, value);
            return value;
        }
        if (intAttributes.containsKey(attribute)) {
            return intAttributes.get(attribute);
        }
        return value;
    }

    public String merge(String attribute, String value) {
        if (isRecording) {
            stringAttributes.put(attribute, value);
            return value;
        }
        if (stringAttributes.containsKey(attribute)) {
            return stringAttributes.get(attribute);
        }
        return value;
    }
}
