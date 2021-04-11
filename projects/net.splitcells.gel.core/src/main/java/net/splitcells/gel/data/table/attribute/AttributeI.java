package net.splitcells.gel.data.table.attribute;

import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.data.atom.Bools.bool;

import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.common.Language;
import org.w3c.dom.Element;

import net.splitcells.dem.data.atom.Bool;

import java.util.function.Function;

public final class AttributeI<T> implements Attribute<T> {

    private final Class<T> type;
    private final String name;
    private final Function<String, T> deserializer;

    public static <T> Attribute<T> attribute(Class<T> type) {
        return new AttributeI<>(type, type.getSimpleName());
    }

    public static Attribute<Integer> integerAttribute(String name) {
        return new AttributeI<>(Integer.class, name, arg -> Integer.valueOf(arg));
    }

    public static Attribute<String> stringAttribute(String name) {
        return new AttributeI<>(String.class, name, arg -> arg);
    }

    public static <T> Attribute<T> attribute(Class<T> type, String name) {
        return new AttributeI<>(type, name);
    }

    private AttributeI(Class<T> type, String name) {
        this(type, name, arg -> {
            throw new UnsupportedOperationException();
        });
    }

    private AttributeI(Class<T> type, String name, Function<String, T> deserializer) {
        this.type = type;
        this.name = name;
        this.deserializer = deserializer;
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
        return bool(type.isAssignableFrom(arg.getClass()));
    }

    @Override
    public Element toDom() {
        return Xml.elementWithChildren(Attribute.class.getSimpleName()
                , Xml.elementWithChildren(Language.NAME.value(), textNode(name))
                , Xml.elementWithChildren(type.getSimpleName())
        );
    }

    @Override
    public T deserializeValue(String value) {
        return deserializer.apply(value);
    }
}
