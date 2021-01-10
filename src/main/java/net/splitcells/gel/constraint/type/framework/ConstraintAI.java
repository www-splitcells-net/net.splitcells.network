package net.splitcells.gel.constraint.type.framework;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.namespace.NameSpaces.GEL;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.dem.resource.host.interaction.LogLevel.DEBUG;
import static net.splitcells.gel.common.Language.ARGUMENTATION;
import static net.splitcells.gel.data.database.Databases.datuBāze;
import static net.splitcells.gel.data.allocation.Allocationss.allocations;
import static net.splitcells.gel.constraint.intermediate.data.AllocationRating.rindasNovērtējums;
import static net.splitcells.gel.constraint.Report.report;
import static net.splitcells.gel.constraint.intermediate.data.RoutingResult.routingResult;
import static net.splitcells.gel.rating.type.Cost.bezMaksas;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.intermediate.data.AllocationSelector;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.constraint.intermediate.data.RoutingRating;
import net.splitcells.gel.rating.structure.MetaRatingI;
import org.assertj.core.api.Assertions;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.QueryI;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.rating.structure.LocalRating;
import net.splitcells.gel.rating.structure.MetaRating;
import net.splitcells.gel.rating.structure.Rating;

@Deprecated
public abstract class ConstraintAI implements Constraint {
    private final GroupId injekcijasGrupas;
    protected final net.splitcells.dem.data.set.list.List<Constraint> bērni = list();
    protected Optional<Discoverable> golvenaisKonteksts = Optional.empty();
    private final List<Discoverable> konteksti = list();
    protected final Database rindas;
    protected final Database radījums = datuBāze("results", this, RESULTING_CONSTRAINT_GROUP_ID, NOVĒRTĒJUMS, IZDALĪŠANA_UZ);
    protected final Allocations rindasApstrāde;
    protected final Map<GroupId, Rating> grupasApstrāde = map();

    protected ConstraintAI(GroupId injekcijasGrupas) {
        this(injekcijasGrupas, "");
    }

    protected ConstraintAI(GroupId injekcijasGrupas, String vārds) {
        this.injekcijasGrupas = injekcijasGrupas;
        rindas = datuBāze(vārds + ".rindas", this, LINE, INCOMING_CONSTRAINT_GROUP_ID);
        rindasApstrāde = allocations("rindasApstrāde", rindas, radījums);
        rindasApstrāde.subscribe_to_afterAddtions(this::izdalīt_papildinajumu);
        rindasApstrāde.subscriber_to_beforeRemoval(this::izdalīt_noņemšana);
        rindas.subscribe_to_afterAddtions(this::apstrāde_rindu_papildinajumu);
    }

    protected ConstraintAI() {
        this(Constraint.standartaGrupa());
    }

    @Override
    public GroupId injectionGroup() {
        return injekcijasGrupas;
    }

    @Override
    public void reģistrē_papildinājums(GroupId ienākošieGrupasId, Line papildinajums) {
        // DARĪT Kustēt uz ārpuses projektu.
        if (TRACING) {
            domsole().append
                    (element("reģistrē_papildinajums." + Constraint.class.getSimpleName()
                            , element("papildinajums", papildinajums.toDom())
                            , element("ienākošieGrupasId", textNode(ienākošieGrupasId.toString()))
                            )
                            , this
                            , DEBUG);
        }
        // DARĪT Kustēt uz ārpuses projektu.
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(
                    rindas
                            .columnView(INCOMING_CONSTRAINT_GROUP_ID)
                            .uzmeklēšana(ienākošieGrupasId)
                            .columnView(LINE)
                            .uzmeklēšana(papildinajums)
                            .getLines()
            ).isEmpty();
        }
        rindas.addTranslated(list(papildinajums, ienākošieGrupasId));
    }

    protected abstract void apstrāde_rindu_papildinajumu(Line papildinājums);

    @Override
    public void rēgistrē_pirms_noņemšanas(GroupId ienākošaGrupaId, Line noņemšana) {
        // DARĪT Kustēt uz ārpuses projektu.
        if (ENFORCING_UNIT_CONSISTENCY) {
            Assertions.assertThat(noņemšana.irDerīgs()).isTrue();
        }
        apstrāda_rindas_primsNoņemšana(ienākošaGrupaId, noņemšana);
        rindasApstrāde
                .columnView(INCOMING_CONSTRAINT_GROUP_ID)
                .uzmeklēšana(ienākošaGrupaId)
                .columnView(LINE)
                .uzmeklēšana(noņemšana)
                .getLines()
                .forEach(rindasApstrāde::remove);
        // JAUDA
        rindas.rawLinesView().stream()
                .filter(e -> e != null)
                .filter(line -> line.value(LINE).equals(noņemšana))
                .filter(line -> line.value(INCOMING_CONSTRAINT_GROUP_ID).equals(ienākošaGrupaId))
                .forEach(rindas::remove);
    }

    protected void apstrāda_rindas_primsNoņemšana(GroupId ienākošaGrupaId, Line noņemšana) {
        rindasApstrāde.supplies_free().rawLinesView().stream()
                .filter(e -> e != null)
                .forEach(nelitotsPiedāvājums -> radījums.remove(nelitotsPiedāvājums));
    }

    protected void izdalīt_papildinajumu(Line papildinajums) {
        papildinajums.value(IZDALĪŠANA_UZ).forEach(child ->
                child.reģistrē_papildinājums
                        (papildinajums.value(RESULTING_CONSTRAINT_GROUP_ID)
                                , papildinajums.value(LINE)));
    }

    protected void izdalīt_noņemšana(Line noņemšana) {
        noņemšana.value(IZDALĪŠANA_UZ).forEach(child ->
                child.rēgistrē_pirms_noņemšanas
                        (noņemšana.value(RESULTING_CONSTRAINT_GROUP_ID)
                                , noņemšana.value(LINE)));
    }

    @Override
    public Constraint arBērnu(Constraint... ierobežojumi) {
        asList(ierobežojumi).forEach(ierobežojums -> {
            bērni.add(ierobežojums);
            ierobežojums.addContext(this);
        });
        return this;
    }

    public Set<Line> ievērojami(GroupId grupaId, Set<Line> piešķiršanas) {
        final Set<Line> ievērojama = setOfUniques();
        piešķiršanas.forEach(piešķiršana -> {
            if (novērtējums(grupaId, piešķiršana.value(LINE)).equals(bezMaksas())) {
                ievērojama.add(rindasApstrāde.demand_of_allocation(piešķiršana).value(LINE));
            }
        });
        return ievērojama;
    }

    public Set<Line> neievērotaji(GroupId grupaId, Set<Line> piešķiršanas) {
        final Set<Line> neievērotaji = setOfUniques();
        piešķiršanas.forEach(piešķiršana -> {
            if (!novērtējums(grupaId, piešķiršana.value(LINE)).equals(bezMaksas())) {
                neievērotaji.add(rindasApstrāde.demand_of_allocation(piešķiršana).value(LINE));
            }
        });
        return neievērotaji;
    }

    @Override
    public MetaRating novērtējums(GroupId grupaId, Line rinda) {
        final var novērtetāMaršrutēšana
                = atlasītNovērtetāMaršrutēšana(grupaId, processedLine -> processedLine.value(LINE).vienāds(rinda));
        novērtetāMaršrutēšana.gūtBērnusUzGrupas().forEach((bērns, grūpa) ->
                grūpa.forEach(group -> novērtetāMaršrutēšana.gūtNovērtējums().add(bērns.novērtējums(group, rinda)))
        );
        if (novērtetāMaršrutēšana.gūtNovērtējums().isEmpty()) {
            return MetaRatingI.reflektētsNovērtējums(bezMaksas());
        }
        return novērtetāMaršrutēšana.gūtNovērtējums().stream()
                .reduce((a, b) -> a.kombinē(b))
                .get()
                .kāReflektētsNovērtējums();
    }

    protected RoutingRating atlasītNovērtetāMaršrutēšana
            (GroupId grupaId, Predicate<Line> apstrādsRindasAtlasītajs) {
        final var novērtetāMaršrutēšana = RoutingRating.veidot();
        rindasApstrāde.rawLinesView().forEach(rinda -> {
            if (rinda != null
                    && apstrādsRindasAtlasītajs.test(rinda)
                    && grupaId.equals(rinda.value(INCOMING_CONSTRAINT_GROUP_ID))) {
                novērtetāMaršrutēšana.gūtNovērtējums().add(rinda.value(NOVĒRTĒJUMS));
                rinda.value(Constraint.IZDALĪŠANA_UZ).forEach(bērni -> {
                    final Set<GroupId> groupsOfChild;
                    if (!novērtetāMaršrutēšana.gūtBērnusUzGrupas().containsKey(bērni)) {
                        groupsOfChild = setOfUniques();
                        novērtetāMaršrutēšana.gūtBērnusUzGrupas().put(bērni, groupsOfChild);
                    } else {
                        groupsOfChild = novērtetāMaršrutēšana.gūtBērnusUzGrupas().get(bērni);
                    }
                    groupsOfChild.add(rinda.value(RESULTING_CONSTRAINT_GROUP_ID));
                });
            }
        });
        return novērtetāMaršrutēšana;
    }

    @Override
    public MetaRating novērtējums(GroupId grupaId) {
        final var novērtetāMaršrutēšana
                = atlasītNovērtetāMaršrutēšana(grupaId, atlasītaRinda -> true);
        novērtetāMaršrutēšana.gūtBērnusUzGrupas().forEach((bērni, grupas) ->
                grupas.forEach(group -> novērtetāMaršrutēšana.gūtNovērtējums().add(bērni.novērtējums(group))));
        if (novērtetāMaršrutēšana.gūtNovērtējums().isEmpty()) {
            return MetaRatingI.reflektētsNovērtējums(bezMaksas());
        }
        return novērtetāMaršrutēšana.gūtNovērtējums().stream()
                .reduce((a, b) -> a.kombinē(b))
                .get()
                .kāReflektētsNovērtējums();
    }

    /**
     * JAUDA
     */
    @Override
    public List<Constraint> childrenView() {
        return listWithValuesOf(bērni);
    }

    public Set<Line> piešķiršanaNo(GroupId grupaId) {
        final Set<Line> piešķiršanas = setOfUniques();
        rindasApstrāde.rawLinesView().forEach(rinda -> {
            if (rinda != null && rinda.value(INCOMING_CONSTRAINT_GROUP_ID).equals(grupaId)) {
                piešķiršanas.add(rinda);
            }
        });
        return piešķiršanas;
    }

    public Set<Line> izpildītāji(GroupId grupaId) {
        return ievērojami(grupaId, piešķiršanaNo(grupaId));
    }

    public Set<Line> defying(GroupId grupaId) {
        return neievērotaji(grupaId, piešķiršanaNo(grupaId));
    }

    @Override
    public Allocations lineProcessing() {
        return rindasApstrāde;
    }

    public Table rindas() {
        return rindas;
    }

    public GroupId grupaNo(Line rinda) {
        return lineProcessing()
                .rawLinesView()
                .get(rinda.index())
                .value(RESULTING_CONSTRAINT_GROUP_ID);
    }

    @Override
    public Line pieliktRadījums(LocalRating vietējieNovērtējums) {
        return radījums.addTranslated
                (list
                        (vietējieNovērtējums.radītsIerobežojumuGrupaId()
                                , vietējieNovērtējums.novērtējums()
                                , vietējieNovērtējums.izdalīUz()));
    }

    @Override
    public void addContext(Discoverable konteksts) {
        if (golvenaisKonteksts.isEmpty()) {
            golvenaisKonteksts = Optional.of(konteksts);
        }
        konteksti.add(konteksts);
    }

    @Override
    public Collection<net.splitcells.dem.data.set.list.List<String>> paths() {
        return konteksti.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Constraint arBērnu(Function<Query, Query> būvētājs) {
        būvētājs.apply(QueryI.jautājums(this, Optional.empty()));
        return this;
    }

    @Override
    public Element toDom() {
        final var dom = Xml.element(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.appendChild(Xml.element(ARGUMENTATION.value(), arg.toDom())));
        }
        dom.appendChild(Xml.element("novērtējums", rating().toDom()));
        {
            final var novērtējumi = Xml.element("novērtējumi");
            dom.appendChild(novērtējumi);
            rindasApstrāde.columnView(INCOMING_CONSTRAINT_GROUP_ID)
                    .uzmeklēšana(injectionGroup())
                    .getLines()
                    .forEach(line -> novērtējumi.appendChild(line.toDom()));
        }
        childrenView().forEach(bērns ->
                dom.appendChild(
                        bērns.toDom(
                                setOfUniques(radījums
                                        .columnView(RESULTING_CONSTRAINT_GROUP_ID)
                                        .values()))));
        return dom;
    }

    @Override
    public Element toDom(Set<GroupId> grupas) {
        final var dom = Xml.element(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.appendChild(Xml.element(ARGUMENTATION.value(), arg.toDom())));
        }
        dom.appendChild(Xml.element("novērtējums", rating(grupas).toDom()));
        {
            final var novērtējumi = Xml.element("novērtējumi");
            dom.appendChild(novērtējumi);
            grupas.forEach(grupa ->
                    rindasApstrāde
                            .columnView(INCOMING_CONSTRAINT_GROUP_ID)
                            .uzmeklēšana(grupa)
                            .getLines().
                            forEach(line -> novērtējumi.appendChild(line.toDom())));
        }
        childrenView().forEach(bērns ->
                dom.appendChild(
                        bērns.toDom(
                                grupas.stream().
                                        map(group -> rindasApstrāde
                                                .columnView(INCOMING_CONSTRAINT_GROUP_ID)
                                                .uzmeklēšana(group)
                                                .getLines()
                                                .stream()
                                                .map(groupLines -> groupLines.value(RESULTING_CONSTRAINT_GROUP_ID))
                                                .collect(toSet()))
                                        .flatMap(resultingGroupings -> resultingGroupings.stream())
                                        .collect(toSet()))));
        return dom;
    }

    protected abstract List<String> vietēijaDabiskaArgumentācija(Report ziņojums);

    protected Optional<List<String>> vietēijaDabiskaArgumentācija
            (Line rinda, GroupId grupa, Predicate<AllocationRating> piešķiršanaAtlasītājs) {
        final var vietējaArgumentācijas
                = rindasApstrāde
                .columnView(LINE)
                .uzmeklēšana(rinda)
                .columnView(INCOMING_CONSTRAINT_GROUP_ID)
                .uzmeklēšana(grupa)
                .getLines()
                .stream()
                .filter(piešķiršana -> piešķiršanaAtlasītājs
                        .test(rindasNovērtējums
                                (piešķiršana
                                        , novērtējums(piešķiršana.value(INCOMING_CONSTRAINT_GROUP_ID), rinda))))
                .map(piešķiršana -> report
                        (piešķiršana.value(LINE)
                                , piešķiršana.value(INCOMING_CONSTRAINT_GROUP_ID)
                                , piešķiršana.value(NOVĒRTĒJUMS)))
                .map(ziņojums -> vietēijaDabiskaArgumentācija(ziņojums))
                .findFirst();
        return vietējaArgumentācijas;
    }

    @Override
    public Perspective dabiskaArgumentācija(GroupId grupa) {
        final var vietējiaArgumentācijas = rindasApstrāde
                .columnView(INCOMING_CONSTRAINT_GROUP_ID)
                .uzmeklēšana(grupa)
                .getLines()
                .stream()
                .map(piešķiršana -> piešķiršana.value(LINE))
                .map(rinda -> dabiskaArgumentācija(rinda, grupa, AllocationSelector::atlasītArCenu))
                .collect(toList());
        if (vietējiaArgumentācijas.size() == 1) {
            return vietējiaArgumentācijas.get(0);
        }
        final var vietējiaArgumentācija = perspective(ARGUMENTATION.value(), GEL);
        vietējiaArgumentācijas.stream()
                .forEach(naturalReasoning -> vietējiaArgumentācija.withChild(naturalReasoning));
        return vietējiaArgumentācija;
    }

    @Override
    public Perspective dabiskaArgumentācija(Line rinda, GroupId grupa, Predicate<AllocationRating> rindasAtlasītājs) {
        final var vietējiaArgumēntacija = vietēijaDabiskaArgumentācija(rinda, grupa, rindasAtlasītājs);
        final var bērnuArgumēntacija = bērnuArgumēntacija(rinda, grupa, rindasAtlasītājs);
        final var argumentācija = perspective(ARGUMENTATION.value(), GEL);
        if (vietējiaArgumēntacija.isPresent()) {
            vietējiaArgumēntacija.get().stream()
                    .map(e -> perspective(e, NameSpaces.STRING))
                    .forEach(argumentācija::withChild);
        }
        if (bērnuArgumēntacija.isPresent()) {
            if (bērnuArgumēntacija.get().name().isEmpty()) {
                bērnuArgumēntacija.get().children().stream()
                        .filter(e -> !e.children().isEmpty())
                        .forEach(argumentācija::withChild);
            } else {
                argumentācija.withChild(bērnuArgumēntacija.get());
            }
        }
        return argumentācija;
    }

    protected Optional<Perspective> bērnuArgumēntacija
            (Line rinda, GroupId grupa, Predicate<AllocationRating> piešķiršanaAtlasītājs) {
        final var argumēntacijas
                = rindasApstrāde
                .columnView(LINE)
                .uzmeklēšana(rinda)
                .columnView(INCOMING_CONSTRAINT_GROUP_ID)
                .uzmeklēšana(grupa)
                .getLines()
                .stream()
                .filter(piešķiršana
                        -> piešķiršanaAtlasītājs
                        .test(rindasNovērtējums
                                (piešķiršana
                                        , novērtējums(piešķiršana.value(INCOMING_CONSTRAINT_GROUP_ID), rinda))))
                .map(piešķiršana -> piešķiršana.value(IZDALĪŠANA_UZ)
                        .stream()
                        .map(propagatedTo ->
                                routingResult
                                        (piešķiršana.value(RESULTING_CONSTRAINT_GROUP_ID)
                                                , propagatedTo)))
                .flatMap(e -> e)
                .map(routingResult -> routingResult.izplatītājs().naturalArgumentation(rinda, routingResult.grupa()))
                .collect(toSet());
        if (argumēntacijas.isEmpty()) {
            return Optional.empty();
        }
        if (argumēntacijas.size() == 1) {
            return Optional.of(argumēntacijas.iterator().next());
        }
        final var argumēntacija = perspective(ARGUMENTATION.value(), GEL);
        argumēntacijas.forEach(argumēntacija::withChild);
        return Optional.of(argumēntacija);
    }

    public Optional<Discoverable> galvenaisKonteksts() {
        return golvenaisKonteksts;
    }
}
