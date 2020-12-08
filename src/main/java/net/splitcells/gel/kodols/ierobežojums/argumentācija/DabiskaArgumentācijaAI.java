package net.splitcells.gel.kodols.ierobežojums.argumentācija;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.gel.kodols.Valoda.ARGUMENTĀCIJA;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import net.splitcells.gel.kodols.Valoda;
import org.w3c.dom.Node;
import net.splitcells.dem.data.set.list.List;

public abstract class DabiskaArgumentācijaAI implements DabiskaArgumentācija {

    private static class DabiskaArgumentācijaI extends DabiskaArgumentācijaAI {
        @Override
        public Node toDom() {
            if (apaķsArgumentācijas().isEmpty() && parametri().isEmpty()) {
                return textNode("");
            } else if (apaķsArgumentācijas().isEmpty() && parametri().size() == 1) {
                return element(parametri().iterator().next());
            } else {
                final var dom = element(ARGUMENTĀCIJA.apraksts());
                if (!parametri().isEmpty()) {
                    dom.appendChild(element(Valoda.VĀRDS.apraksts(), textNode(parametri().stream().reduce((a, b) -> a + "," + b).get())));
                }
                if (!apaķsArgumentācijas().isEmpty()) {
                    apaķsArgumentācijas().forEach(apakšArgumentācija -> dom.appendChild(apakšArgumentācija.toDom()));
                }
                return dom;
            }
        }

        /**
         * KOMPROMISS
         */
        @Override
        public <A extends DabiskaArgumentācija> boolean equalContents(A arg) {
            if (arg instanceof DabiskaArgumentācijaI) {
                final var citaArgumentācija = (DabiskaArgumentācijaI) arg;
                return parametri().equals(citaArgumentācija.parametri())
                        && apaķsArgumentācijas().equals(citaArgumentācija.apaķsArgumentācijas());
            }
            return false;
        }

        @Override
        public DabiskaArgumentācija shallowClone() {
            final DabiskaArgumentācijaI klons = new DabiskaArgumentācijaI();
            parametri().forEach(localReason -> klons.arParametru(localReason));
            return klons;
        }
    }

    public static DabiskaArgumentācija argumentācija() {
        return new DabiskaArgumentācijaI();
    }

    @Override
    public List<DabiskaArgumentācija> paplašinātUzVienkāršuArgumentācija() {
        if (apakšDabiskaArgumentācijas.isEmpty()) {
            return list(this);
        } else {
            return listWithValuesOf
                    (apaķsArgumentācijas().stream()
                            .flatMap(apašArgumentācija -> apašArgumentācija.paplašinātUzVienkāršuArgumentācija().stream())
                            .map(vienkāršaArgumentācija -> shallowClone().arApaķsArgumentacija(vienkāršaArgumentācija))
                            .collect(Collectors.toList()));
        }
    }

    private final List<DabiskaArgumentācija> apakšDabiskaArgumentācijas = list();
    private final List<String> parametri = list();

    protected DabiskaArgumentācijaAI() {
    }

    @Override
    public Node toDom() {
        throw not_implemented_yet();
    }

    @Override
    public List<DabiskaArgumentācija> apaķsArgumentācijas() {
        return apakšDabiskaArgumentācijas;
    }

    @Override
    public List<String> parametri() {
        return parametri;
    }

    @Override
    public DabiskaArgumentācija arApaķsArgumentacija(DabiskaArgumentācija dabiskaArgumentācija) {
        apakšDabiskaArgumentācijas.add(dabiskaArgumentācija);
        return this;
    }

    @Override
    public DabiskaArgumentācija arParametru(String parametrs) {
        parametri.add(parametrs);
        return this;
    }

    @Override
    public abstract <A extends DabiskaArgumentācija> boolean equalContents(A arg);
}
