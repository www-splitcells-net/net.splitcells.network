package net.splitcells.gel.kodols.ierobežojums.argumentācija.valoda;

import static net.splitcells.dem.lang.Xml.element;

import org.w3c.dom.Node;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.Vērtētājs;
import net.splitcells.gel.kodols.ierobežojums.argumentācija.Argumentācija;
import net.splitcells.gel.kodols.ierobežojums.argumentācija.ArgumentācijaAI;

import java.util.Objects;

public class PriekšVisiemArgumentācija extends ArgumentācijaAI {
    public static PriekšVisiemArgumentācija priekšVisiemArgumentācija
            (GrupaId group, Vērtētājs vērtētājs, Tabula piešķiršana) {
        return new PriekšVisiemArgumentācija(group, vērtētājs, piešķiršana);
    }

    private final GrupaId grupa;
    private final Vērtētājs vērtētājs;
    private final Tabula piešķiršanasNoGrupas;

    private PriekšVisiemArgumentācija(GrupaId grupa, Vērtētājs vērtētājs, Tabula piešķiršanasNoGrupas) {
        this.grupa = grupa;
        this.vērtētājs = vērtētājs;
        this.piešķiršanasNoGrupas = piešķiršanasNoGrupas;
    }

    @Override
    public Node toDom() {
        final var doc = element("priekš-visiem");
        if (grupa.vārds().isPresent()) {
            doc.appendChild(grupa.toDom());
        }
        doc.appendChild(vērtētājs.argumentacija(grupa, piešķiršanasNoGrupas));
        return doc;
    }

    @Override
    public <A extends Argumentācija> boolean equalContents(A arg) {
        if (arg instanceof PriekšVisiemArgumentācija) {
            final var citaArgumentācija = (PriekšVisiemArgumentācija) arg;
            return vietējaArgumentācijas().equals(citaArgumentācija.vietējaArgumentācijas())
                    && apaķsArgumentācijas().equals(citaArgumentācija.apaķsArgumentācijas())
                    && Objects.equals(grupa, citaArgumentācija.grupa)
                    && Objects.equals(vērtētājs, citaArgumentācija.vērtētājs);
        }
        return false;
    }

    @Override
    public Argumentācija shallowClone() {
        return new PriekšVisiemArgumentācija(grupa, vērtētājs, piešķiršanasNoGrupas);
    }
}
