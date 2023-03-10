package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.PerspectiveI;
import org.w3c.dom.Node;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;

public class AllocationNaturalArgumentation implements MetaData<String> {
    public static AllocationNaturalArgumentation allocationNaturalArgumentation(String allocationNaturalArgumentation) {
        return new AllocationNaturalArgumentation(allocationNaturalArgumentation);
    }

    private final String allocationNaturalArgumentation;

    private AllocationNaturalArgumentation(String allocationNaturalArgumentation) {
        this.allocationNaturalArgumentation = allocationNaturalArgumentation;
    }

    @Override
    public Node toDom() {
        return perspective(allocationNaturalArgumentation).toDom();
    }

    @Override
    public Perspective toPerspective() {
        return perspective(allocationNaturalArgumentation);
    }

    @Override
    public String value() {
        return allocationNaturalArgumentation;
    }
}
