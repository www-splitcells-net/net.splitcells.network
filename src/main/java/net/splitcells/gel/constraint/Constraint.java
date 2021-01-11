package net.splitcells.gel.constraint;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.namespace.NameSpaces.GEL;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.gel.common.Language.ARGUMENTATION;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.data.table.attribute.ListAttribute.listAttribute;
import static net.splitcells.gel.rating.structure.MetaRating.neutral;

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
    Attribute<Line> LINE = attribute(Line.class, "rinda");
    Attribute<java.util.List<Constraint>> IZDALĪŠANA_UZ = listAttribute(Constraint.class, "idalīšana uz");
    Attribute<GroupId> INCOMING_CONSTRAINT_GROUP = attribute(GroupId.class, "ienākošie ierobežojumu grupas id");
    Attribute<GroupId> RESULTING_CONSTRAINT_GROUP = attribute(GroupId.class, "radītas ierobežojumu grupas id");
    Attribute<Rating> NOVĒRTĒJUMS = attribute(Rating.class, "novērtējums");

    static List<List<Constraint>> piešķiršanasGruppas(List<Constraint> momentānaTaka) {
        final var ierobežojums = momentānaTaka.lastValue().get();
        final List<List<Constraint>> piešķiršanasGruppas = list();
        piešķiršanasGruppas.add(momentānaTaka);
        piešķiršanasGruppas.addAll(
                ierobežojums.childrenView().stream()
                        .map(child -> piešķiršanasGruppas(listWithValuesOf(momentānaTaka).withAppended(child)))
                        .reduce((a, b) -> a.withAppended(b))
                        .orElseGet(() -> list()));
        return piešķiršanasGruppas;
    }

    static List<List<Constraint>> allocationGroups(Constraint ierobežojums) {
        return piešķiršanasGruppas(list(ierobežojums));
    }

    GroupId injectionGroup();

    default MetaRating rating() {
        return rating(injectionGroup());
    }

    static GroupId standardGroup() {
        return GroupId.group("priekš-visiem");
    }

    default MetaRating novērtējums(Line rinda) {
        return event(injectionGroup(), rinda);
    }

    MetaRating event(GroupId grupaId, Line rinda);

    MetaRating rating(GroupId grupaId);

    default Perspective naturalArgumentation() {
        return naturalArgumentation(injectionGroup());
    }

    Perspective naturalArgumentation(GroupId grupa);

    Optional<Discoverable> mainContext();

    default Perspective naturalArgumentation(Line priekšmets, GroupId grupa) {
        return naturalArgumentation(priekšmets, grupa, AllocationSelector::selectLinesWithCost);
    }

    Perspective naturalArgumentation(Line rinda, GroupId grupa, Predicate<AllocationRating> rindasAtlasītājs);

    default MetaRating rating(Set<GroupId> grupas) {
        return grupas.stream().
                map(group -> rating(group)).
                reduce((a, b) -> a.combine(b)).
                orElseGet(() -> neutral());
    }

    default GroupId reģistrē(Line rinda) {
        final var rVal = injectionGroup();
        register_additions(rVal, rinda);
        return rVal;
    }

    GroupId groupOf(Line rinda);

    void register_additions(GroupId grupaId, Line rinda);

    default void register_addition(Line rinda) {
        register_additions(injectionGroup(), rinda);
    }

    void register_before_removal(GroupId grupaId, Line rinda);

    @Deprecated
    default void register_before_removal(Line rinda) {
        register_before_removal(injectionGroup(), rinda);
    }

    List<Constraint> childrenView();

    Set<Line> complying(GroupId grupaId);

    default Set<Line> izpildītāji() {
        return complying(injectionGroup());
    }

    Set<Line> defying(GroupId grupaId);

    default Set<Line> defying() {
        return defying(injectionGroup());
    }

    Line addResult(LocalRating vietējieNovērtējums);

    default Query jautājums() {
        return QueryI.jautājums(this);
    }

    Table lineProcessing();

    Element toDom();

    Element toDom(Set<GroupId> grupas);

    default Set<Constraint> vecāksNo(Constraint ierobežojums) {
        if (equals(ierobežojums)) {
            return setOfUniques(this);
        }
        return childrenView().stream()
                .map(child -> child.vecāksNo(ierobežojums))
                .reduce((a, b) -> Sets.merge(a, b))
                .orElseGet(() -> setOfUniques());
    }

    default Set<GroupId> bērnuGrupas(Line rinda, Constraint priekšmets) {
        final Set<GroupId> bērnuGrupas = setOfUniques();
        if (equals(priekšmets)) {
            bērnuGrupas.add(groupOf(rinda));
        } else {
            childrenView().forEach(bērns -> bērnuGrupas.addAll(bērns.bērnuGrupas(rinda, priekšmets)));
        }
        return bērnuGrupas;
    }

    default Element graph() {
        final var grafiks = Xml.rElement(GEL, type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> grafiks.appendChild(Xml.element(ARGUMENTATION.value(), arg.toDom())));
        }
        childrenView().forEach(child -> {
            grafiks.appendChild(child.graph());
        });
        return grafiks;
    }
}
