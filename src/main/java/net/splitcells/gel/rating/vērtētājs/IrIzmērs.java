package net.splitcells.gel.rating.vērtētājs;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.tips.Cena.cena;
import static net.splitcells.gel.rating.struktūra.VietējieNovērtējumsI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.Tabula;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.tips.Cena;
import net.splitcells.gel.rating.struktūra.Novērtējums;


public class IrIzmērs implements Vērtētājs {
    public static IrIzmērs irIzmērs(int mērķuIzmers) {
        return new IrIzmērs(mērķuIzmers);
    }

    private final int mērķuIzmers;
    private final List<Discoverable> konteksts = list();

    protected IrIzmērs(int mērķuIzmers) {
        this.mērķuIzmers = mērķuIzmers;
    }

    @Override
    public NovērtējumsNotikums vērtē_pēc_papildinājumu(Tabula rindas, Rinda papildinājums, List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
        final var indivīdsNovērtējums = novērtējums(rindas, false);
        final var padildinājumuNovērtējumu
                = novērteRindas(rindas, papildinājums, bērni, indivīdsNovērtējums);
        padildinājumuNovērtējumu.papildinājumi().put(papildinājums
                , lokalsNovērtejums()
                        .arIzdalīšanaUz(bērni)
                        .arNovērtējumu(indivīdsNovērtējums)
                        .arRadītuGrupasId(papildinājums.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID))
        );
        return padildinājumuNovērtējumu;
    }

    private NovērtējumsNotikums novērteRindas(Tabula rindas, Rinda maiņīts, List<Ierobežojums> children, Novērtējums cena) {
        final NovērtējumsNotikums rindasNovērtējumu = NovērtējumsNotikumsI.novērtejumuNotikums();
        rindas.jēlaRindasSkats().stream()
                .filter(e -> e != null)
                .filter(e -> e.indekss() != maiņīts.indekss())
                .forEach(e -> {
                    rindasNovērtējumu.atjaunaNovērtējumu_caurAizvietošana(e,
                            lokalsNovērtejums().
                                    arIzdalīšanaUz(children).
                                    arNovērtējumu(cena).
                                    arRadītuGrupasId(maiņīts.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID))
                    );
                });
        return rindasNovērtējumu;
    }

    @Override
    public Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
        final var argumentacija = Xml.element(IrIzmērs.class.getSimpleName());
        argumentacija.appendChild(
                Xml.element("vēlamais-izmērs"
                        , Xml.textNode(mērķuIzmers + "")));
        argumentacija.appendChild(
                Xml.element("faktiskais-izmērs"
                        , Xml.textNode(piešķiršanas.izmērs() + "")));
        return argumentacija;
    }

    @Override
    public String uzVienkāršuAprakstu(Rinda rinda, GrupaId grupa) {
        return "izmērs ir " + mērķuIzmers;
    }

    @Override
    public NovērtējumsNotikums vērtē_pirms_noņemšana
            (Tabula rindas
                    , Rinda noņemšana
                    , List<Ierobežojums> bērni
                    , Tabula novērtējumsPirmsNoņemšana) {
        return novērteRindas(rindas, noņemšana, bērni, novērtējums(rindas, true));
    }

    private Novērtējums novērtējums(Tabula rindas, boolean pirmsNoņemšana) {
        final Novērtējums novērtējums;
        final int izmers;
        if (pirmsNoņemšana) {
            izmers = rindas.izmērs() - 1;
        } else {
            izmers = rindas.izmērs();
        }
        if (izmers == 0) {
            novērtējums = Cena.bezMaksas();
        } else if (izmers > 0) {
            final int atšķirība = abs(mērķuIzmers - izmers);
            novērtējums = cena(atšķirība / ((double) izmers));
        } else {
            throw new AssertionError("negatīvs izmērs atrasts: " + izmers);
        }
        return novērtējums;
    }

    @Override
    public Class<? extends Vērtētājs> type() {
        return IrIzmērs.class;
    }

    @Override
    public List<Domable> arguments() {
        return list(() -> Xml.element(IrIzmērs.class.getSimpleName(), Xml.textNode("" + mērķuIzmers)));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg != null && arg instanceof IrIzmērs) {
            return this.mērķuIzmers == ((IrIzmērs) arg).mērķuIzmers;
        }
        return false;
    }

    @Override
    public void addContext(Discoverable context) {
        konteksts.add(context);
    }

    @Override
    public Collection<List<String>> paths() {
        return konteksts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + mērķuIzmers;
    }
}
