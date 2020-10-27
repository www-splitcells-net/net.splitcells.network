package net.splitcells.gel.kodols.atrisinājums;

import net.splitcells.gel.kodols.problēma.Problēma;

public class AtrisinājumuVeidotajsI extends AtrisinājumuVeidotajs {
    @Override
    public Atrisinājums atrisinājum(Problēma problēma) {
        return joinAspects(new AtrisinājumsI(problēma));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
