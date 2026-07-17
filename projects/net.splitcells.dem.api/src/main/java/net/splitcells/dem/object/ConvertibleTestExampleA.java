/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.object;

public class ConvertibleTestExampleA implements Convertible {
    public static ConvertibleTestExampleA convertibleTestExampleA() {
        return new ConvertibleTestExampleA();
    }

    private ConvertibleTestExampleA() {

    }

    private String a = "0";
    private String b = "0";
    private String name = "";

    @Override
    public Merger merge(Merger merger) {
        name = merger.merge("name", name);
        a = merger.merge("a", a);
        b = merger.merge("b", b);
        return merger;
    }

    public String a() {
        return a;
    }

    public ConvertibleTestExampleA withA(String arg) {
        a = arg;
        return this;
    }

    public String b() {
        return b;
    }

    public ConvertibleTestExampleA withB(String arg) {
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
