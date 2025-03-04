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
package net.splitcells.gel.editor.lang;

import net.splitcells.dem.data.set.map.Map;

import java.util.Optional;

import static net.splitcells.dem.data.set.map.Maps.map;

public class SolutionDescription {
    public static SolutionDescription solutionDescription(String name) {
        return new SolutionDescription(name);
    }

    private String name;
    private final Map<String, AttributeDescription> attributes = map();
    private Optional<TableDescription> demands = Optional.empty();
    private Optional<TableDescription> supplies = Optional.empty();

    private SolutionDescription(String argName) {
        name = argName;
    }

    public Map<String, AttributeDescription> attributes() {
        return attributes;
    }

    public Optional<TableDescription> demands() {
        return demands;
    }

    public SolutionDescription withDemands(TableDescription arg) {
        demands = Optional.of(arg);
        return this;
    }

    public SolutionDescription withSupplies(TableDescription arg) {
        supplies = Optional.of(arg);
        return this;
    }

    public Optional<TableDescription> supplies() {
        return supplies;
    }

    public String name() {
        return name;
    }
}
