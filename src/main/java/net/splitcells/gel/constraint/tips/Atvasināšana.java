package net.splitcells.gel.constraint.tips;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.rating.structure.LocalRating;
import net.splitcells.gel.rating.structure.MetaRating;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

public final class Atvasināšana implements Constraint {

    public static Atvasināšana atvasināšana
            (Constraint atvasināšanasMērķis, Function<MetaRating, MetaRating> atvasināšanaFuncija) {
        return new Atvasināšana(atvasināšanasMērķis, atvasināšanaFuncija);
    }

    private final Constraint atvasināšanasMērķis;
    private final Function<MetaRating, MetaRating> atvasināšanaFuncija;

    private Atvasināšana(Constraint atvasināšanasMērķis, Function<MetaRating, MetaRating> atvasināšanaFuncija) {
        this.atvasināšanasMērķis = atvasināšanasMērķis;
        this.atvasināšanaFuncija = atvasināšanaFuncija;
    }

    @Override
    public GroupId injekcijasGrupa() {
        return atvasināšanasMērķis.injekcijasGrupa();
    }

    @Override
    public MetaRating novērtējums(GroupId grupaId, Line rinda) {
        return atvasināšanaFuncija.apply(atvasināšanasMērķis.novērtējums(grupaId, rinda));
    }

    @Override
    public MetaRating novērtējums(GroupId groupdId) {
        return atvasināšanaFuncija.apply(atvasināšanasMērķis.novērtējums(groupdId));
    }

    @Override
    public Perspective dabiskaArgumentācija(GroupId grupa) {
        throw not_implemented_yet();
    }

    @Override
    public Optional<Discoverable> galvenaisKonteksts() {
        return atvasināšanasMērķis.galvenaisKonteksts();
    }

    @Override
    public Perspective dabiskaArgumentācija
            (Line rinda, GroupId grupa, Predicate<AllocationRating> rindasAtlasītājs) {
        throw not_implemented_yet();
    }

    @Override
    public GroupId grupaNo(Line rinda) {
        return atvasināšanasMērķis.grupaNo(rinda);
    }

    @Override
    public void reģistrē_papildinājums(GroupId grupaId, Line rinda) {
        throw not_implemented_yet();
    }

    @Override
    public void rēgistrē_pirms_noņemšanas(GroupId grupaId, Line rinda) {
        throw not_implemented_yet();
    }

    @Override
    public List<Constraint> skatsUsBerniem() {
        throw not_implemented_yet();
    }

    @Override
    public Set<Line> izpildītāji(GroupId grupaId) {
        throw not_implemented_yet();
    }

    @Override
    public Set<Line> neievērotaji(GroupId grupaId) {
        throw not_implemented_yet();
    }

    @Override
    public Line pieliktRadījums(LocalRating vietējieNovērtējums) {
        throw not_implemented_yet();
    }

    @Override
    public Allocations rindasAbstrāde() {
        throw not_implemented_yet();
    }

    @Override
    public Element toDom() {
        throw not_implemented_yet();
    }

    @Override
    public Element toDom(Set<GroupId> grupas) {
        throw not_implemented_yet();
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        final var path = atvasināšanasMērķis.path();
        path.add(getClass().getSimpleName());
        return path;
    }

    @Override
    public net.splitcells.dem.data.set.list.List<Domable> arguments() {
        throw not_implemented_yet();
    }

    @Override
    public Class<? extends Constraint> type() {
        throw not_implemented_yet();
    }

    @Override
    public Constraint arBērnu(Constraint... ierobežojums) {
        throw not_implemented_yet();
    }

    @Override
    public Constraint arBērnu(Function<Query, Query> būvētājs) {
        throw not_implemented_yet();
    }

    @Override
    public void addContext(Discoverable konteksts) {
        throw not_implemented_yet();
    }

    @Override
    public Collection<net.splitcells.dem.data.set.list.List<String>> paths() {
        throw not_implemented_yet();
    }
}
