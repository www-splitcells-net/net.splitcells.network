package net.splitcells.gel.kodols.ierobežojums.argumentācija;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;

public abstract class ArgumentācijaAI implements Argumentācija {

    private static class ArgumentācijaI extends ArgumentācijaAI {
        @Override
        public Node toDom() {
            if (apaķsArgumentācijas().isEmpty() && vietējaArgumentācijas().isEmpty()) {
                return Xml.textNode("");
            } else if (apaķsArgumentācijas().isEmpty() && vietējaArgumentācijas().size() == 1) {
                return vietējaArgumentācijas().iterator().next().toDom();
            } else {
                final var dom = Xml.element("argumentācija");
                if (!vietējaArgumentācijas().isEmpty()) {
                    vietējaArgumentācijas().forEach(localReason -> dom.appendChild(localReason.toDom()));
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
        public <A extends Argumentācija> boolean equalContents(A arg) {
            if (arg instanceof ArgumentācijaI) {
                final var citaArgumentācija = (ArgumentācijaI) arg;
                return vietējaArgumentācijas().equals(citaArgumentācija.vietējaArgumentācijas())
                        && apaķsArgumentācijas().equals(citaArgumentācija.apaķsArgumentācijas());
            }
            return false;
        }

        @Override
        public Argumentācija shallowClone() {
            final ArgumentācijaI klons = new ArgumentācijaI();
            vietējaArgumentācijas().forEach(localReason -> klons.argVietējiuArgumentācija(localReason.shallowClone()));
            return klons;
        }
    }

    public static Argumentācija argumentācija() {
        return new ArgumentācijaI();
    }

    @Override
    public List<Argumentācija> paplašinātUzVienkāršaArgumentācija() {
        if (apakšArgumentācijas.isEmpty()) {
            return list(this);
        } else {
            return listWithValuesOf
                    (apaķsArgumentācijas().stream()
                            .flatMap(apašArgumentācija -> apašArgumentācija.paplašinātUzVienkāršaArgumentācija().stream())
                            .map(vienkāršaArgumentācija -> shallowClone().arApaķsArgumentacija(vienkāršaArgumentācija))
                            .collect(Collectors.toList()));
        }
    }

    private final List<Argumentācija> apakšArgumentācijas = list();
    private final List<Argumentācija> vietēijaArgumentācija = list();

    protected ArgumentācijaAI() {
    }

    @Override
    public Node toDom() {
        throw not_implemented_yet();
    }

    @Override
    public List<Argumentācija> apaķsArgumentācijas() {
        return apakšArgumentācijas;
    }

    @Override
    public List<Argumentācija> vietējaArgumentācijas() {
        return vietēijaArgumentācija;
    }

    @Override
    public Argumentācija arApaķsArgumentacija(Argumentācija argumentācija) {
        apakšArgumentācijas.add(argumentācija);
        return this;
    }

    @Override
    public Argumentācija argVietējiuArgumentācija(Argumentācija argumentācija) {
        vietēijaArgumentācija.add(argumentācija);
        return this;
    }

    @Override
    public abstract <A extends Argumentācija> boolean equalContents(A arg);
}
