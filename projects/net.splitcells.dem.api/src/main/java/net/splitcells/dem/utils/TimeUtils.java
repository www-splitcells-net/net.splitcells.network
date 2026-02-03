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
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

@JavaLegacy
public class TimeUtils {

    private static final DateTimeFormatter UTC_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    public static LocalTime currentLocalTime() {
        return LocalTime.now();
    }

    public static ZonedDateTime zonedDateTime() {
        return ZonedDateTime.now();
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

    public static String toUtcStringForFiles(ZonedDateTime arg)  {
        return arg.withZoneSameInstant(ZoneOffset.UTC).format(UTC_DATE_TIME_FORMAT);
    }

    private TimeUtils() {
        throw constructorIllegal();
    }
}
