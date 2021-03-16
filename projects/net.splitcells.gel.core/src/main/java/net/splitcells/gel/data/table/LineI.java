package net.splitcells.gel.data.table;

import static java.util.Objects.requireNonNull;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.Xml.toFlatString;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.common.Language.*;

import net.splitcells.dem.lang.Xml;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.data.table.attribute.Attribute;

import java.util.Objects;

public class LineI implements Line {
    private final Table context;
    private final int index;

    public static Line line(Table context, int index) {
        return new LineI(context, index);
    }

    protected LineI(Table context, int line) {
        this.context = context;
        this.index = line;
    }

    @Override
    public <T> T value(Attribute<T> attribute) {
        return context.columnView(requireNonNull(attribute)).get(index);
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public Table context() {
        return context;
    }

    @Override
    public boolean equals(Object arg) {
        if (this == arg) {
            return true;
        } else if (arg instanceof Line) {
            final var argLine = (Line) arg;
            return index() == argLine.index() && context().equals(argLine.context());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return toFlatString(toDom());
    }

    @Override
    public Element toDom() {
        final var dom = Xml.elementWithChildren(Line.class.getSimpleName());
        dom.appendChild(Xml.elementWithChildren(INDEX.value(), textNode("" + index)));
        context.headerView().forEach(attribute -> {
            final var value = context.columnView(attribute).get(index);
            final Node domValue;
            if (value == null) {
                domValue = textNode("");
            } else {
                if (value instanceof Domable) {
                    domValue = ((Domable) value).toDom();
                } else {
                    domValue = textNode(value.toString());
                }
            }
            final var valueElement = Xml.elementWithChildren(VALUE.value());
            valueElement.setAttribute(TYPE.value(), attribute.name());
            valueElement.appendChild(domValue);
            dom.appendChild(valueElement);
        });
        return dom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index(), context());
    }

}
