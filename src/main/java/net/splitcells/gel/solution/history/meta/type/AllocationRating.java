package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.rating.struktūra.Novērtējums;
import org.w3c.dom.Node;

public class AllocationRating implements MetaData<Novērtējums> {

    public static AllocationRating pieškiršanasNovērtejums(Novērtējums novērtējums) {
        return new AllocationRating(novērtējums);
    }

    private final Novērtējums novērtējums;

    private AllocationRating(Novērtējums novērtējums) {
        this.novērtējums = novērtējums;
    }

    @Override
    public Novērtējums vertība() {
        return novērtējums;
    }

    @Override
    public Node toDom() {
        final var dom = Xml.element(getClass().getSimpleName());
        dom.appendChild(novērtējums.toDom());
        return dom;
    }
}
