package net.splitcells.gel.ierobežojums.tips;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.novērtējums.vērtētājs.NemainīgsVērtētājs.constantRater;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.ierobežojums.tips.struktūra.IerobežojumsBalstītaUzVietējieGrupasAI;
import net.splitcells.gel.ierobežojums.Ierobežojums;
import net.splitcells.gel.ierobežojums.Ziņojums;
import net.splitcells.gel.novērtējums.struktūra.Novērtējums;
import net.splitcells.gel.novērtējums.vērtētājs.Vērtētājs;

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
    protected List<String> vietēijaDabiskaArgumentācija(Ziņojums ziņojums) {
        return list(vērtētājs.uzVienkāršuAprakstu(ziņojums.rinda(), ziņojums.grupa()));
    }
}
