package net.splitcells.gel.kodols.atrisinājums;

import net.splitcells.gel.kodols.problēma.Problēma;

import static net.splitcells.gel.kodols.atrisinājums.AtrisinājumsI.atrisinājums;

public class AtrisinājumuVeidotajsI extends AtrisinājumuVeidotajs {
    @Override
    public Atrisinājums atrisinājum(Problēma problēma) {
        return joinAspects(atrisinājums(problēma));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
