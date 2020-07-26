package net.splitcells.gel.kodols.ierobežojums.argumentācija.valoda;

import static net.splitcells.dem.lang.Xml.element;

import org.w3c.dom.Node;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.Vērtētājs;
import net.splitcells.gel.kodols.ierobežojums.argumentācija.Argumentācija;
import net.splitcells.gel.kodols.ierobežojums.argumentācija.ArgumentācijaAI;

import java.util.Objects;

public class TadArgumentācija extends ArgumentācijaAI {
    public static TadArgumentācija tadARgumentācija(GrupaId grupa, Vērtētājs vērtētājs, Tabula piešķiršana) {
        return new TadArgumentācija(grupa, vērtētājs, piešķiršana);
    }

    private final GrupaId grupa;
    private final Vērtētājs vērtētājs;
    private final Tabula piešķiršanaNoGrupas;

    private TadArgumentācija(GrupaId grupa, Vērtētājs vērtētājs, Tabula piešķiršanaNoGrupas) {
        this.grupa = grupa;
        this.vērtētājs = vērtētājs;
        this.piešķiršanaNoGrupas = piešķiršanaNoGrupas;
    }

    @Override
    public Node toDom() {
        final var doc = element("tad");
        if (grupa.vārds().isPresent()) {
            doc.appendChild(grupa.toDom());
        }
        doc.appendChild(vērtētājs.argumentacija(grupa, piešķiršanaNoGrupas));
        return doc;
    }

    @Override
    public <A extends Argumentācija> boolean equalContents(A arg) {
        if (arg instanceof TadArgumentācija) {
            final var citasArgumentācijas = (TadArgumentācija) arg;
            return vietējaArgumentācijas().equals(citasArgumentācijas.vietējaArgumentācijas())
                    && apaķsArgumentācijas().equals(citasArgumentācijas.apaķsArgumentācijas())
                    && Objects.equals(grupa, citasArgumentācijas.grupa)
                    && Objects.equals(vērtētājs, citasArgumentācijas.vērtētājs);
        }
        return false;
    }

    @Override
    public Argumentācija shallowClone() {
        return new TadArgumentācija(grupa, vērtētājs, piešķiršanaNoGrupas);
    }
}
