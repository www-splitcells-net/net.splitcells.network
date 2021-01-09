package net.splitcells.gel.data.table;

import java.util.Optional;

import static java.util.Objects.hash;

public class RindasRādītājsI implements RindasRādītājs {
    public static RindasRādītājs rindasRādītājs(Tabula konteksts, int indekss) {
        return new RindasRādītājsI(konteksts, indekss);
    }

    private final Tabula konteksts;
    private final int indekss;

    private RindasRādītājsI(Tabula konteksts, int indekss) {
        this.konteksts = konteksts;
        this.indekss = indekss;
    }

    @Override
    public Tabula konteksts() {
        return konteksts;
    }

    @Override
    public int indekss() {
        return indekss;
    }

    @Override
    public Optional<Rinda> interpretē(Tabula argKonteksts) {
        if (argKonteksts.jēlaRindasSkats().size() <= indekss) {
            return Optional.empty();
        }
        return Optional.ofNullable(argKonteksts.gūtJēluRindas(indekss));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof RindasRādītājs) {
            final var other = (RindasRādītājs) arg;
            return konteksts().equals(other.konteksts()) && indekss() == other.indekss();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return hash(indekss(), konteksts());
    }
}
