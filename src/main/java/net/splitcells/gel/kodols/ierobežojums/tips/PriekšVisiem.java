package net.splitcells.gel.kodols.ierobežojums.tips;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.kodols.novērtējums.vērtētājs.klasifikators.VērtētājsBalstītsUzGrupēšana.raterBasedGrouping;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.Ziņojums;
import net.splitcells.gel.kodols.ierobežojums.tips.struktūra.IerobežojumsBalstītaUzVietējieGrupasAI;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.Vērtētājs;

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

    @Override
    protected List<String> vietēijaDabiskaArgumentācija(Ziņojums ziņojums) {
        return list("Priekš visiem " + vērtētājs.uzVienkāršuAprakstu(ziņojums.rinda(), ziņojums.grupa()));
    }
}
