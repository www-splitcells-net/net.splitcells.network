package net.splitcells.gel.rating.rater.classification;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.constraint.GrupaId.grupa;
import static net.splitcells.gel.rating.rater.RatingEventI.novērtejumuNotikums;
import static net.splitcells.gel.rating.type.Cost.bezMaksas;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

@Deprecated
public class ForAllAttributeValues implements Rater {
    public static ForAllAttributeValues priekšVisiemAtribūtuVertības(final Attribute<?> arg) {
        return new ForAllAttributeValues(arg);
    }

    private final Attribute<?> atribūts;

    protected ForAllAttributeValues(final Attribute<?> atribūts) {
        this.atribūts = atribūts;
    }

    protected final Map<GrupaId, Map<Object, GrupaId>> grupa = map();
    private final List<Discoverable> konteksti = list();

    public Attribute<?> atribūti() {
        return atribūts;
    }

    @Override
    public RatingEvent vērtē_pēc_papildinājumu
            (Table rindas, Line papildinājums, List<Ierobežojums> bērni, Table novērtējumsPirmsPapildinājumu) {
        final var grupēšanasVertība = papildinājums.vērtība(Ierobežojums.RINDA).vērtība(atribūts);
        final var ienākošasGrupasId = papildinājums.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID);
        if (!grupa.containsKey(ienākošasGrupasId)) {
            grupa.put(ienākošasGrupasId, map());
        }
        if (!grupa.get(ienākošasGrupasId).containsKey(grupēšanasVertība)) {
            grupa.get(ienākošasGrupasId).put(grupēšanasVertība, GrupaId.grupa(grupēšanasVertība.toString()));
        }
        final var novērtejumuNotikums = novērtejumuNotikums();
        novērtejumuNotikums.papildinājumi().put(papildinājums
                , lokalsNovērtejums()
                        .arIzdalīšanaUz(bērni)
                        .arNovērtējumu(bezMaksas())
                        .arRadītuGrupasId(grupa.get(ienākošasGrupasId).get(grupēšanasVertība))
        );
        return novērtejumuNotikums;
    }

    @Override
    public RatingEvent vērtē_pirms_noņemšana
            (Table rindas, Line noņemšana, List<Ierobežojums> bērni, Table novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public List<Domable> arguments() {
        return list(atribūts);
    }

    @Override
    public Node argumentacija(GrupaId grupa, Table piešķiršanas) {
        final var argumentācia = Xml.element("priekš-visiem");
        argumentācia.appendChild(
                Xml.element("vārds"
                        , Xml.textNode(getClass().getSimpleName())));
        argumentācia.appendChild(atribūts.toDom());
        return argumentācia;
    }

    @Override
    public String uzVienkāršuAprakstu(Line rinda, GrupaId grupa) {
        return "priekš visiem " + atribūts.vārds();
    }

    @Override
    public void addContext(Discoverable kontexsts) {
        konteksti.add(kontexsts);
    }

    @Override
    public Collection<List<String>> paths() {
        return konteksti.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Class<? extends Rater> type() {
        return ForAllAttributeValues.class;
    }
    
    @Override
    public String toString() {
        return ForAllAttributeValues.class.getSimpleName() + "-" + atribūts.vārds();
    }
}