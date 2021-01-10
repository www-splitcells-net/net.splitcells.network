package net.splitcells.gel.data.table;

import java.util.Optional;

import static java.util.Objects.hash;

public class LinePointerI implements LinePointer {
    public static LinePointer rindasRādītājs(Table konteksts, int indekss) {
        return new LinePointerI(konteksts, indekss);
    }

    private final Table konteksts;
    private final int indekss;

    private LinePointerI(Table konteksts, int indekss) {
        this.konteksts = konteksts;
        this.indekss = indekss;
    }

    @Override
    public Table konteksts() {
        return konteksts;
    }

    @Override
    public int index() {
        return indekss;
    }

    @Override
    public Optional<Line> interpretē(Table argKonteksts) {
        if (argKonteksts.rawLinesView().size() <= indekss) {
            return Optional.empty();
        }
        return Optional.ofNullable(argKonteksts.getRawLines(indekss));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof LinePointer) {
            final var other = (LinePointer) arg;
            return konteksts().equals(other.konteksts()) && index() == other.index();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return hash(index(), konteksts());
    }
}
