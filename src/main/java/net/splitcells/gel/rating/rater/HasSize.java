package net.splitcells.gel.rating.rater;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.rating.structure.Rating;


public class HasSize implements Rater {
    public static HasSize irIzmērs(int mērķuIzmers) {
        return new HasSize(mērķuIzmers);
    }

    private final int mērķuIzmers;
    private final List<Discoverable> konteksts = list();

    protected HasSize(int mērķuIzmers) {
        this.mērķuIzmers = mērķuIzmers;
    }

    @Override
    public RatingEvent vērtē_pēc_papildinājumu(Table rindas, Line papildinājums, List<Constraint> bērni, Table novērtējumsPirmsPapildinājumu) {
        final var indivīdsNovērtējums = novērtējums(rindas, false);
        final var padildinājumuNovērtējumu
                = novērteRindas(rindas, papildinājums, bērni, indivīdsNovērtējums);
        padildinājumuNovērtējumu.papildinājumi().put(papildinājums
                , lokalsNovērtejums()
                        .arIzdalīšanaUz(bērni)
                        .arNovērtējumu(indivīdsNovērtējums)
                        .arRadītuGrupasId(papildinājums.vērtība(Constraint.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID))
        );
        return padildinājumuNovērtējumu;
    }

    private RatingEvent novērteRindas(Table rindas, Line maiņīts, List<Constraint> children, Rating cena) {
        final RatingEvent rindasNovērtējumu = RatingEventI.novērtejumuNotikums();
        rindas.jēlaRindasSkats().stream()
                .filter(e -> e != null)
                .filter(e -> e.indekss() != maiņīts.indekss())
                .forEach(e -> {
                    rindasNovērtējumu.atjaunaNovērtējumu_caurAizvietošana(e,
                            lokalsNovērtejums().
                                    arIzdalīšanaUz(children).
                                    arNovērtējumu(cena).
                                    arRadītuGrupasId(maiņīts.vērtība(Constraint.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID))
                    );
                });
        return rindasNovērtējumu;
    }

    @Override
    public Node argumentacija(GroupId grupa, Table piešķiršanas) {
        final var argumentacija = Xml.element(HasSize.class.getSimpleName());
        argumentacija.appendChild(
                Xml.element("vēlamais-izmērs"
                        , Xml.textNode(mērķuIzmers + "")));
        argumentacija.appendChild(
                Xml.element("faktiskais-izmērs"
                        , Xml.textNode(piešķiršanas.size() + "")));
        return argumentacija;
    }

    @Override
    public String uzVienkāršuAprakstu(Line rinda, GroupId grupa) {
        return "izmērs ir " + mērķuIzmers;
    }

    @Override
    public RatingEvent vērtē_pirms_noņemšana
            (Table rindas
                    , Line noņemšana
                    , List<Constraint> bērni
                    , Table novērtējumsPirmsNoņemšana) {
        return novērteRindas(rindas, noņemšana, bērni, novērtējums(rindas, true));
    }

    private Rating novērtējums(Table rindas, boolean pirmsNoņemšana) {
        final Rating novērtējums;
        final int izmers;
        if (pirmsNoņemšana) {
            izmers = rindas.size() - 1;
        } else {
            izmers = rindas.size();
        }
        if (izmers == 0) {
            novērtējums = Cost.bezMaksas();
        } else if (izmers > 0) {
            final int atšķirība = abs(mērķuIzmers - izmers);
            novērtējums = cost(atšķirība / ((double) izmers));
        } else {
            throw new AssertionError("negatīvs izmērs atrasts: " + izmers);
        }
        return novērtējums;
    }

    @Override
    public Class<? extends Rater> type() {
        return HasSize.class;
    }

    @Override
    public List<Domable> arguments() {
        return list(() -> Xml.element(HasSize.class.getSimpleName(), Xml.textNode("" + mērķuIzmers)));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg != null && arg instanceof HasSize) {
            return this.mērķuIzmers == ((HasSize) arg).mērķuIzmers;
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
