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

public class ConvertibleTestExampleA implements Convertible {
    public static ConvertibleTestExampleA convertibleTestExampleA() {
        return new ConvertibleTestExampleA();
    }

    private ConvertibleTestExampleA() {

    }

    private int a = 0;
    private int b = 0;
    private String name = "";

    @Override
    public Merger merge(Merger merger) {
        name = merger.merge("name", name);
        return merger;
    }

    public int a() {
        return a;
    }

    public ConvertibleTestExampleA withA(int arg) {
        a = arg;
        return this;
    }

    public int b() {
        return b;
    }

    public ConvertibleTestExampleA withB(int arg) {
        b = arg;
        return this;
    }

    public String name() {
        return name;
    }

    public ConvertibleTestExampleA withName(String arg) {
        name = arg;
        return this;
    }
}
