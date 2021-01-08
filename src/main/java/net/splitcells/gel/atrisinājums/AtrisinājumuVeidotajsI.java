package net.splitcells.gel.atrisinājums;

import net.splitcells.gel.problēma.Problēma;

public class AtrisinājumuVeidotajsI extends AtrisinājumuVeidotajs {
    @Override
    public Atrisinājums atrisinājum(Problēma problēma) {
        return joinAspects(AtrisinājumsI.atrisinājums(problēma));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
