package net.splitcells.gel.data.table.attribute;

import static net.splitcells.dem.data.atom.Bools.bool;
import static net.splitcells.dem.data.atom.Bools.untrue;

import java.util.List;

import org.w3c.dom.Element;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.atom.Bool;

public class ListAttribute<T> implements Attribute<List<T>> {

    private final Class<T> type;
    private final String name;

    public static <T> ListAttribute<T> listAttribute(Class<T> type, String name) {
        return new ListAttribute<>(type, name);
    }

    protected ListAttribute(Class<T> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object arg) {
        return super.equals(arg);
    }

    @Override
    public Bool isInstanceOf(Object arg) {
        if (arg instanceof List<?>) {
            return bool(
                    ((List<?>) arg).stream()
                            .filter(i -> i != null)
                            .allMatch(i -> type.isAssignableFrom(i.getClass())));
        } else {
            return untrue();
        }
    }

    @Override
    public Element toDom() {
        return Xml.elementWithChildren(name
                , Xml.elementWithChildren(getClass().getSimpleName())
                , Xml.elementWithChildren(type.getSimpleName())
        );
    }
}
