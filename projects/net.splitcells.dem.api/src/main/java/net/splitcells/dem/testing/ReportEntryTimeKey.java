/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing;

import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.time.format.DateTimeFormatter;

@Deprecated
@JavaLegacy
public class ReportEntryTimeKey {

    public static final ReportEntryTimeKey START_TIME = new ReportEntryTimeKey();
    public static final ReportEntryTimeKey END_TIME = new ReportEntryTimeKey();
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    private ReportEntryTimeKey() {
        clazz = Long.class;
    }

    public String currentValue() {
        return "" + java.lang.System.nanoTime();
    }

    private final Class<Long> clazz;


    public Class<Long> key() {
        return clazz;
    }

    public String keyString() {
        return clazz.getName();
    }

}
