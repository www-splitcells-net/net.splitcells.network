package net.splitcells.gel.data.tabula.atribūts;

import static net.splitcells.dem.data.atom.BoolI.bool;

import org.w3c.dom.Element;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.atom.Bool;

public class MetaAtribūts<T> implements Atribūts<Class<T>> {

    private final Class<T> tips;
    private final String vārds;

    public MetaAtribūts(Class<T> tips, String vārds) {
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
        return bool(tips.isAssignableFrom((Class<?>) arg));
    }

    @Override
    public Element toDom() {
        return Xml.element(vārds
                , Xml.element(getClass().getSimpleName())
                , Xml.element(tips.getSimpleName())
        );
    }
}