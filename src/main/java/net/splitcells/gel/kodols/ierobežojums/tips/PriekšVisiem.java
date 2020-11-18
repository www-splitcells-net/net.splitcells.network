package net.splitcells.gel.kodols.ierobežojums.tips;

import static net.splitcells.gel.kodols.novērtējums.vērtētājs.klasifikators.VērtētājsBalstītsUzGrupēšana.raterBasedGrouping;

import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.tips.struktūra.IerobežojumsBalstītaUzVietējieGrupasAI;
import net.splitcells.gel.kodols.ierobežojums.Ziņojums;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.Vērtētājs;
import net.splitcells.gel.kodols.ierobežojums.argumentācija.Argumentācija;

public class PriekšVisiem extends IerobežojumsBalstītaUzVietējieGrupasAI {

    public static PriekšVisiem veidot(Vērtētājs grupēšana) {
        return priekšVisiem(grupēšana);
    }

    public static PriekšVisiem priekšVisiem(Vērtētājs grupēšana) {
        return new PriekšVisiem(grupēšana);
    }

    protected PriekšVisiem(Vērtētājs grupēšana) {
        super(raterBasedGrouping(grupēšana));
    }

    public Vērtētājs grupešana() {
        return vērtētājs;
    }

    @Override
    public Class<? extends Ierobežojums> type() {
        return PriekšVisiem.class;
    }

}
