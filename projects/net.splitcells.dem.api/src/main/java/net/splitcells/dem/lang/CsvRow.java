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
package net.splitcells.dem.lang;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import org.apache.commons.csv.CSVRecord;

@JavaLegacyArtifact
public class CsvRow {
    public static CsvRow csvRow(CSVRecord argRecord) {
        return new CsvRow(argRecord);
    }

    private final CSVRecord record;

    private CsvRow(CSVRecord argRecord) {
        record = argRecord;
    }

    public String value(String attribute) {
        return record.get(attribute);
    }
}
