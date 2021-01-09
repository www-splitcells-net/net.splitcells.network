package net.splitcells.gel.rating.rater.classification;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.Tabula;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;
import org.w3c.dom.Node;

import java.util.Collection;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.gel.constraint.Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID;
import static net.splitcells.gel.constraint.Ierobežojums.RINDA;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;
import static net.splitcells.gel.rating.rater.RatingEventI.novērtejumuNotikums;
import static net.splitcells.gel.rating.type.Cost.bezMaksas;

public class ForAllWithCondition<T> implements Rater {

    public static <T> ForAllWithCondition<T> priekšVisiemArNosacījumu(Predicate<Rinda> nosacījums) {
        return new ForAllWithCondition<>(nosacījums);
    }

    private final Predicate<Rinda> nosacījums;
    private final List<Discoverable> konteksti = list();

    private ForAllWithCondition(Predicate<Rinda> nosacījums) {
        this.nosacījums = nosacījums;
    }

    @Override
    public RatingEvent vērtē_pēc_papildinājumu
            (Tabula rindas, Rinda papildinājums, List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
        final List<Ierobežojums> mērķBērni;
        if (nosacījums.test(papildinājums.vērtība(RINDA))) {
            mērķBērni = bērni;
        } else {
            mērķBērni = list();
        }
        final var novērtejumuNotikums = novērtejumuNotikums();
        novērtejumuNotikums.papildinājumi().put
                (papildinājums
                        , lokalsNovērtejums()
                                .arIzdalīšanaUz(mērķBērni)
                                .arNovērtējumu(bezMaksas())
                                .arRadītuGrupasId(papildinājums.vērtība(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)));
        return novērtejumuNotikums;
    }

    @Override
    public RatingEvent vērtē_pirms_noņemšana(Tabula rindas, Rinda noņemšana, List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
        final var argumentācjia = Xml.element("priekš-visiem-ar-nosacījumu");
        final var atribūtuApraksts = Xml.element("nosacījumu");
        argumentācjia.appendChild(atribūtuApraksts);
        atribūtuApraksts.appendChild(Xml.textNode(nosacījums.toString()));
        return argumentācjia;
    }

    @Override
    public void addContext(Discoverable konteksts) {
        konteksti.add(konteksts);
    }

    @Override
    public Collection<List<String>> paths() {
        return konteksti.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public List<Domable> arguments() {
        return list(
                new Domable() {
                    @Override
                    public Node toDom() {
                        return Xml.textNode(nosacījums.toString());
                    }
                }
        );
    }
}
