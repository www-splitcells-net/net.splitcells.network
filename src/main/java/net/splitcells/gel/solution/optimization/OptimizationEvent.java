package net.splitcells.gel.solution.optimization;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.common.Language;
import net.splitcells.gel.data.table.LinePointer;
import org.w3c.dom.Node;

import java.util.Objects;

import static net.splitcells.dem.lang.Xml.attribute;
import static net.splitcells.dem.lang.Xml.element;

public final class OptimizationEvent implements Domable {

    private final LinePointer prasība;
    private final LinePointer piedāvājums;
    private final StepType solisTips;

    public static OptimizationEvent optimizacijasNotikums(StepType solisTips, LinePointer prasība, LinePointer piedāvājums) {
        return new OptimizationEvent(solisTips, prasība, piedāvājums);
    }

    private OptimizationEvent(StepType solisTips, LinePointer demand, LinePointer supply) {
        this.solisTips = solisTips;
        this.prasība = demand;
        this.piedāvājums = supply;

    }

    public StepType soluTips() {
        return solisTips;
    }

    public LinePointer piedāvājums() {
        return piedāvājums;
    }

    public LinePointer prasība() {
        return prasība;
    }

    @Override
    public Node toDom() {
        final var dom = element(getClass().getSimpleName());
        dom.setAttribute(StepType.class.getSimpleName(), solisTips.name());
        dom.appendChild(Xml.element(Language.DEMAND.value(), prasība.toDom()));
        dom.appendChild(Xml.element(Language.SUPPLY.value(), piedāvājums.toDom()));
        return dom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(prasība.indekss(), piedāvājums.indekss(), solisTips);
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof OptimizationEvent) {
            final var other = (OptimizationEvent) arg;
            return prasība.equals(other.prasība())
                    && piedāvājums.equals(other.piedāvājums())
                    && solisTips.equals(other.soluTips());
        }
        throw new IllegalArgumentException();
    }
}
