package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.rating.structure.Rating;
import org.w3c.dom.Node;

public class AllocationRating implements MetaData<Rating> {

    public static AllocationRating pieškiršanasNovērtejums(Rating novērtējums) {
        return new AllocationRating(novērtējums);
    }

    private final Rating novērtējums;

    private AllocationRating(Rating novērtējums) {
        this.novērtējums = novērtējums;
    }

    @Override
    public Rating value() {
        return novērtējums;
    }

    @Override
    public Node toDom() {
        final var dom = Xml.element(getClass().getSimpleName());
        dom.appendChild(novērtējums.toDom());
        return dom;
    }
}
