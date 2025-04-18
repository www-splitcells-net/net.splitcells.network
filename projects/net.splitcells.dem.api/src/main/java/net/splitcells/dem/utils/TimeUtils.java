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
package net.splitcells.dem.utils;

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

@JavaLegacyArtifact
public class TimeUtils {
    private TimeUtils() {
        throw constructorIllegal();
    }

    public static LocalTime currentLocalTime() {
        return LocalTime.now();
    }

    /**
     *
     * @param arg A Stirng in the format `YYYY-MM-DD`.
     * @return
     */
    public static ZonedDateTime parseZonedDate(String arg) {
        return ZonedDateTime.of(Integers.parse(arg.substring(0, 4))
                , Integers.parse(arg.substring(5, 7))
                , Integers.parse(arg.substring(8, 10))
                , 0
                , 0
                , 0
                , 0
                , ZoneId.of("UTC"));
    }
}
