package net.splitcells.gel.data.table.attribute;

import static net.splitcells.dem.data.atom.BoolI.bool;

import org.w3c.dom.Element;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.atom.Bool;

public class MetaAttribute<T> implements Attribute<Class<T>> {

    private final Class<T> type;
    private final String name;

    public MetaAttribute(Class<T> type, String name) {
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
        return bool(type.isAssignableFrom((Class<?>) arg));
    }

    @Override
    public Element toDom() {
        return Xml.elementWithChildren(name
                , Xml.elementWithChildren(getClass().getSimpleName())
                , Xml.elementWithChildren(type.getSimpleName())
        );
    }
}