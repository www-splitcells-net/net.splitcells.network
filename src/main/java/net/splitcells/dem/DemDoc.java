package net.splitcells.dem;

import net.splitcells.dem.lang.perspective.Dens;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.PerspectiveDocument;

import static net.splitcells.dem.lang.perspective.Dens.*;

public class DemDoc extends PerspectiveDocument {
    public DemDoc() {
        super(Dem.class);
    }

    @Override
    protected Perspective createPerspective() {
        return project(
                Dens.name("Dependency-manager")
                , optimization
                        (scheduling
                                (queue
                                        (prioritization
                                                (todo("Store abstract syntax trees in Java, because XML's schema system is inferioir and has great configuration costs.")
                                                )
                                        )
                                )
                        )
        );
    }
}
