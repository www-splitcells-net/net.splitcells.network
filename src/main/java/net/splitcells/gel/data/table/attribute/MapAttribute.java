package net.splitcells.gel.data.table.attribute;

import static net.splitcells.dem.data.atom.BoolI.bool;
import static net.splitcells.dem.data.atom.BoolI.untrue;

import java.util.Map;

import org.w3c.dom.Element;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.atom.Bool;

public class MapAttribute<T> implements Attribute<Map<Class<T>, T>> {

    private final Class<?> tips;
    private final String vārds;

    public MapAttribute(Class<?> tips, String vārds) {
        this.tips = tips;
        this.vārds = vārds;
    }

    @Override
    public String vārds() {
        return vārds;
    }

    @Override
    public Bool irGadījumsNo(Object arg) {
        if (arg instanceof Map) {
            return bool(((Map<Class<?>, ?>) arg).entrySet().stream()
                    .filter(i -> i != null)
                    .allMatch(i -> tips.isAssignableFrom(i.getValue().getClass())
                            && tips.isAssignableFrom(i.getKey())
                            && i.getKey().equals(i.getValue().getClass())
                    ));
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
