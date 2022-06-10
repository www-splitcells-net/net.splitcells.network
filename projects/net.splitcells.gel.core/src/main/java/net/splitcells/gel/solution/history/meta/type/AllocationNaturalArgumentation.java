package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.PerspectiveI;
import org.w3c.dom.Node;

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
        return PerspectiveI.perspective(allocationNaturalArgumentation).toDom();
    }

    @Override
    public String value() {
        return allocationNaturalArgumentation;
    }
}
