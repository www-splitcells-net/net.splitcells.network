package net.splitcells.gel.data.table;

import static java.util.Objects.requireNonNull;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.Xml.toFlatString;
import static net.splitcells.dem.data.set.list.Lists.list;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.data.table.attribute.Attribute;

import java.util.Objects;

public class LineI implements Line {
    private final Table konteksts;
    private final int indekss;

    public static Line rinda(Table konteksts, int indekss) {
        return new LineI(konteksts, indekss);
    }

    protected LineI(Table konteksts, int indekss) {
        this.konteksts = konteksts;
        this.indekss = indekss;
    }

    @Override
    public <T> T vērtība(Attribute<T> atribūts) {
        return konteksts.columnView(requireNonNull(atribūts)).get(indekss);
    }

    @Override
    public int indekss() {
        return indekss;
    }

    @Override
    public Table konteksts() {
        return konteksts;
    }

    @Override
    public boolean equals(Object arg) {
        if (this == arg) {
            return true;
        } else if (arg instanceof Line) {
            final var argRinda = (Line) arg;
            return indekss() == argRinda.indekss() && konteksts().equals(argRinda.konteksts());
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
        final var gūtasDomVertība = element(Line.class.getSimpleName());
        gūtasDomVertība.appendChild(element("indekss", textNode("" + indekss)));
        konteksts.headerView().forEach(atribūts -> {
            final var vertība = konteksts.columnView(atribūts).get(indekss);
            final Node domVertība;
            if (vertība == null) {
                domVertība = textNode("");
            } else {
                if (vertība instanceof Domable) {
                    domVertība = ((Domable) vertība).toDom();
                } else {
                    domVertība = textNode(vertība.toString());
                }
            }
            final var vertībasElements = element("vertība");
            vertībasElements.setAttribute("tips", atribūts.vārds());
            vertībasElements.appendChild(domVertība);
            gūtasDomVertība.appendChild(vertībasElements);
        });
        return gūtasDomVertība;
    }

    @Override
    public int hashCode() {
        return Objects.hash(indekss(), konteksts());
    }

}
