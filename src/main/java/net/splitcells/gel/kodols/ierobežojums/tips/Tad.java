package net.splitcells.gel.kodols.ierobežojums.tips;

import static net.splitcells.gel.kodols.novērtējums.vērtētājs.NemainīgsVērtētājs.constantRater;
import static net.splitcells.gel.kodols.ierobežojums.argumentācija.valoda.TadArgumentācija.tadARgumentācija;

import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.tips.struktūra.IerobežojumsBalstītaUzVietējieGrupasAI;
import net.splitcells.gel.kodols.ierobežojums.Ziņojums;
import net.splitcells.gel.kodols.novērtējums.struktūra.Novērtējums;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.Vērtētājs;
import net.splitcells.gel.kodols.ierobežojums.argumentācija.Argumentācija;

public class Tad extends IerobežojumsBalstītaUzVietējieGrupasAI {

    public static Tad tad(Vērtētājs vērtētājs) {
        return new Tad(vērtētājs);
    }

    public static Tad tad(Novērtējums novērtējums) {
        return new Tad(constantRater(novērtējums));
    }

    protected Tad(Vērtētājs vērtētājs) {
        super(vērtētājs, vērtētājs.getClass().getSimpleName());
    }

    @Override
    public Class<? extends Ierobežojums> type() {
        return Tad.class;
    }

    @Override
    protected Argumentācija vietēijaArgumentācija(Ziņojums ziņojums) {
        return tadARgumentācija(ziņojums.grupa()
                , vērtētājs
                , rindasApstrāde.kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                        .uzmeklēšana(ziņojums.grupa()));
    }
}
