package net.splitcells.gel.data.table.atribūts;

import static net.splitcells.dem.data.atom.BoolI.bool;
import static net.splitcells.dem.data.atom.BoolI.untrue;

import java.util.List;

import org.w3c.dom.Element;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.atom.Bool;

public class SarakstsAtribūts<T> implements Atribūts<List<T>> {

    private final Class<T> tips;
    private final String vārds;

    public static <T> SarakstsAtribūts<T> listAttribute(Class<T> type, String name) {
        return new SarakstsAtribūts<>(type, name);
    }

    protected SarakstsAtribūts(Class<T> tips, String vārds) {
        this.tips = tips;
        this.vārds = vārds;
    }

    @Override
    public String vārds() {
        return vārds;
    }

    @Override
    public boolean equals(Object arg) {
        return super.equals(arg);
    }

    @Override
    public Bool irGadījumsNo(Object arg) {
        if (arg instanceof List<?>) {
            return bool(
                    ((List<?>) arg).stream()
                            .filter(i -> i != null)
                            .allMatch(i -> tips.isAssignableFrom(i.getClass())));
        } else {
            return untrue();
        }
    }

    @Override
    public Element toDom() {
        return Xml.element(vārds
                , Xml.element(getClass().getSimpleName())
                , Xml.element(tips.getSimpleName())
        );
    }
}
