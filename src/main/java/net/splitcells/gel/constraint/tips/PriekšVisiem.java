package net.splitcells.gel.constraint.tips;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.vērtētājs.klasifikators.VērtētājsBalstītsUzGrupēšana.raterBasedGrouping;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.tips.struktūra.IerobežojumsBalstītaUzVietējieGrupasAI;
import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.constraint.Ziņojums;
import net.splitcells.gel.rating.vērtētājs.Vērtētājs;
import net.splitcells.gel.rating.vērtētājs.klasifikators.PriekšVisiemAtribūtsVērtībam;

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
        if (vērtētājs.type().equals(PriekšVisiemAtribūtsVērtībam.class)) {
            return list(vērtētājs.uzVienkāršuAprakstu(ziņojums.rinda(), ziņojums.grupa()));
        } else {
            return list("Priekš visiem " + vērtētājs.uzVienkāršuAprakstu(ziņojums.rinda(), ziņojums.grupa()));
        }
    }
}
