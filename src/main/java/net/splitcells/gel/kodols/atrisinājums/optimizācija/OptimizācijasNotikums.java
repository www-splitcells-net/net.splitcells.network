package net.splitcells.gel.kodols.atrisinājums.optimizācija;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.kodols.dati.tabula.RindaRādītājs;
import org.w3c.dom.Node;

import static net.splitcells.dem.lang.Xml.attribute;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.gel.kodols.Valoda.PIEDĀVĀJUMS;
import static net.splitcells.gel.kodols.Valoda.PRASĪBA;

public final class OptimizācijasNotikums implements Domable {

    private final RindaRādītājs prasība;
    private final RindaRādītājs piedāvājums;
    private final SoluTips solisTips;

    public static OptimizācijasNotikums optimizacijasNotikums(SoluTips solisTips, RindaRādītājs prasība, RindaRādītājs piedāvājums) {
        return new OptimizācijasNotikums(solisTips, prasība, piedāvājums);
    }

    private OptimizācijasNotikums(SoluTips solisTips, RindaRādītājs demand, RindaRādītājs supply) {
        this.solisTips = solisTips;
        this.prasība = demand;
        this.piedāvājums = supply;

    }

    public SoluTips soluTips() {
        return solisTips;
    }

    public RindaRādītājs piedāvājums() {
        return piedāvājums;
    }

    public RindaRādītājs prasība() {
        return prasība;
    }

    @Override
    public Node toDom() {
        final var dom = element(getClass().getSimpleName());
        dom.setAttribute(SoluTips.class.getSimpleName(), solisTips.name());
        dom.appendChild(element(PRASĪBA.apraksts(), prasība.toDom()));
        dom.appendChild(element(PIEDĀVĀJUMS.apraksts(), piedāvājums.toDom()));
        return dom;
    }
}
