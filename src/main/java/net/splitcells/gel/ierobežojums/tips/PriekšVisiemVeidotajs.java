package net.splitcells.gel.ierobežojums.tips;

import net.splitcells.gel.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.novērtējums.vērtētājs.Vērtētājs;
import net.splitcells.gel.novērtējums.vērtētājs.klasifikators.PriekšVisiemVērtībasKombinācija;

import static net.splitcells.gel.novērtējums.vērtētājs.klasifikators.Izdalīšana.izdalīšana;
import static net.splitcells.gel.novērtējums.vērtētājs.klasifikators.PriekšVisiemArNosacījumu.priekšVisiemArNosacījumu;
import static net.splitcells.gel.novērtējums.vērtētājs.klasifikators.PriekšVisiemAtribūtsVērtībam.priekšVisiemAtribūtuVertības;

public class PriekšVisiemVeidotajs {
    protected static final PriekšVisiemVeidotajs GADĪJUMS = new PriekšVisiemVeidotajs();
    
    public static PriekšVisiemVeidotajs gadījums() {
        return GADĪJUMS;
    }

    protected PriekšVisiemVeidotajs() {

    }

    public <T> PriekšVisiem priekšVisiemArVērtibu(Atribūts<T> atribūts, T vērtiba) {
        return PriekšVisiem.veidot(
                priekšVisiemArNosacījumu(line -> vērtiba.equals(line.vērtība(atribūts))));
    }

    public PriekšVisiem priekšVisiem() {
        return PriekšVisiem.veidot(izdalīšana());
    }

    public PriekšVisiem priekšVisiem(final Atribūts<?> arg) {
        return PriekšVisiem.veidot(priekšVisiemAtribūtuVertības(arg));
    }

    public PriekšVisiem priekšVisiem(Vērtētājs grouping) {
        return PriekšVisiem.veidot(grouping);
    }

    public PriekšVisiem forAllCombinations(final Atribūts<?>... argumenti) {
        return PriekšVisiem.veidot(PriekšVisiemVērtībasKombinācija.forAllValueCombinations(argumenti));
    }

}
