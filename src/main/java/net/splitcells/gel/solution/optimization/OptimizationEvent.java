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

    private final LinePointer demand;
    private final LinePointer supply;
    private final StepType stepType;

    public static OptimizationEvent optimizationEvent(StepType stepType, LinePointer demand, LinePointer supply) {
        return new OptimizationEvent(stepType, demand, supply);
    }

    private OptimizationEvent(StepType stepType, LinePointer demand, LinePointer supply) {
        this.stepType = stepType;
        this.demand = demand;
        this.supply = supply;

    }

    public StepType stepType() {
        return stepType;
    }

    public LinePointer supply() {
        return supply;
    }

    public LinePointer demand() {
        return demand;
    }

    @Override
    public Node toDom() {
        final var dom = element(getClass().getSimpleName());
        dom.setAttribute(StepType.class.getSimpleName(), stepType.name());
        dom.appendChild(Xml.element(Language.DEMAND.value(), demand.toDom()));
        dom.appendChild(Xml.element(Language.SUPPLY.value(), supply.toDom()));
        return dom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(demand.index(), supply.index(), stepType);
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof OptimizationEvent) {
            final var other = (OptimizationEvent) arg;
            return demand.equals(other.demand())
                    && supply.equals(other.supply())
                    && stepType.equals(other.stepType());
        }
        throw new IllegalArgumentException();
    }
}
