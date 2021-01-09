package net.splitcells.gel.constraint;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.namespace.NameSpaces.GEL;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.gel.common.Vārdi.ARGUMENTI;
import static net.splitcells.gel.constraint.GroupId.grupa;
import static net.splitcells.gel.data.table.attribute.AttributeI.atributs;
import static net.splitcells.gel.data.table.attribute.ListAttribute.listAttribute;
import static net.splitcells.gel.rating.structure.MetaRating.neitrāla;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.reflection.PubliclyConstructed;
import net.splitcells.dem.utils.reflection.PubliclyTyped;
import net.splitcells.gel.constraint.intermediate.data.AllocationSelector;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.structure.LocalRating;
import net.splitcells.gel.rating.structure.MetaRating;
import net.splitcells.gel.rating.structure.Rating;

public interface Constraint extends AfterAdditionSubscriber, BeforeRemovalSubscriber, ConstraintWriter, Discoverable, PubliclyTyped<Constraint>, PubliclyConstructed<Domable>, Domable {
    Attribute<Line> RINDA = atributs(Line.class, "rinda");
    Attribute<java.util.List<Constraint>> IZDALĪŠANA_UZ = listAttribute(Constraint.class, "idalīšana uz");
    Attribute<GroupId> IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID = atributs(GroupId.class, "ienākošie ierobežojumu grupas id");
    Attribute<GroupId> RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID = atributs(GroupId.class, "radītas ierobežojumu grupas id");
    Attribute<Rating> NOVĒRTĒJUMS = atributs(Rating.class, "novērtējums");

    static List<List<Constraint>> piešķiršanasGruppas(List<Constraint> momentānaTaka) {
        final var ierobežojums = momentānaTaka.lastValue().get();
        final List<List<Constraint>> piešķiršanasGruppas = list();
        piešķiršanasGruppas.add(momentānaTaka);
        piešķiršanasGruppas.addAll(
                ierobežojums.skatsUsBerniem().stream()
                        .map(child -> piešķiršanasGruppas(listWithValuesOf(momentānaTaka).withAppended(child)))
                        .reduce((a, b) -> a.withAppended(b))
                        .orElseGet(() -> list()));
        return piešķiršanasGruppas;
    }

    static List<List<Constraint>> piešķiršanasGruppas(Constraint ierobežojums) {
        return piešķiršanasGruppas(list(ierobežojums));
    }

    GroupId injekcijasGrupa();

    default MetaRating novērtējums() {
        return novērtējums(injekcijasGrupa());
    }

    static GroupId standartaGrupa() {
        return GroupId.grupa("priekš-visiem");
    }

    default MetaRating novērtējums(Line rinda) {
        return novērtējums(injekcijasGrupa(), rinda);
    }

    MetaRating novērtējums(GroupId grupaId, Line rinda);

    MetaRating novērtējums(GroupId grupaId);

    default Perspective dabiskaArgumentācija() {
        return dabiskaArgumentācija(injekcijasGrupa());
    }

    Perspective dabiskaArgumentācija(GroupId grupa);

    Optional<Discoverable> galvenaisKonteksts();

    default Perspective dabiskaArgumentācija(Line priekšmets, GroupId grupa) {
        return dabiskaArgumentācija(priekšmets, grupa, AllocationSelector::atlasītArCenu);
    }

    Perspective dabiskaArgumentācija(Line rinda, GroupId grupa, Predicate<AllocationRating> rindasAtlasītājs);

    default MetaRating rating(Set<GroupId> grupas) {
        return grupas.stream().
                map(group -> novērtējums(group)).
                reduce((a, b) -> a.kombinē(b)).
                orElseGet(() -> neitrāla());
    }

    default GroupId reģistrē(Line rinda) {
        final var rVal = injekcijasGrupa();
        reģistrē_papildinājums(rVal, rinda);
        return rVal;
    }

    GroupId grupaNo(Line rinda);

    void reģistrē_papildinājums(GroupId grupaId, Line rinda);

    default void reģistrē_papildinājumi(Line rinda) {
        reģistrē_papildinājums(injekcijasGrupa(), rinda);
    }

    void rēgistrē_pirms_noņemšanas(GroupId grupaId, Line rinda);

    @Deprecated
    default void rēgistrē_pirms_noņemšanas(Line rinda) {
        rēgistrē_pirms_noņemšanas(injekcijasGrupa(), rinda);
    }

    List<Constraint> skatsUsBerniem();

    Set<Line> izpildītāji(GroupId grupaId);

    default Set<Line> izpildītāji() {
        return izpildītāji(injekcijasGrupa());
    }

    Set<Line> neievērotaji(GroupId grupaId);

    default Set<Line> neievērotaji() {
        return neievērotaji(injekcijasGrupa());
    }

    Line pieliktRadījums(LocalRating vietējieNovērtējums);

    default Query jautājums() {
        return QueryI.jautājums(this);
    }

    Table rindasAbstrāde();

    Element toDom();

    Element toDom(Set<GroupId> grupas);

    default Set<Constraint> vecāksNo(Constraint ierobežojums) {
        if (equals(ierobežojums)) {
            return setOfUniques(this);
        }
        return skatsUsBerniem().stream()
                .map(child -> child.vecāksNo(ierobežojums))
                .reduce((a, b) -> Sets.merge(a, b))
                .orElseGet(() -> setOfUniques());
    }

    default Set<GroupId> bērnuGrupas(Line rinda, Constraint priekšmets) {
        final Set<GroupId> bērnuGrupas = setOfUniques();
        if (equals(priekšmets)) {
            bērnuGrupas.add(grupaNo(rinda));
        } else {
            skatsUsBerniem().forEach(bērns -> bērnuGrupas.addAll(bērns.bērnuGrupas(rinda, priekšmets)));
        }
        return bērnuGrupas;
    }

    default Element grafiks() {
        final var grafiks = Xml.rElement(GEL, type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> grafiks.appendChild(Xml.element(ARGUMENTI, arg.toDom())));
        }
        skatsUsBerniem().forEach(child -> {
            grafiks.appendChild(child.grafiks());
        });
        return grafiks;
    }
}
