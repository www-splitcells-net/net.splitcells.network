/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.network.worker.via.java;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class LogEntryProperty {

    public static LogEntryProperty logEntryProperty(String argKey, String argValue) {
        return new LogEntryProperty(argKey, argValue);
    }

    @Getter @Setter private String key;
    @Getter @Setter private String value;

    private LogEntryProperty(String argKey, String argValue) {
        key = argKey;
        value = argValue;
    }
}
