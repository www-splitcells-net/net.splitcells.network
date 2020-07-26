package net.splitcells.gel.kodols.ierobežojums.argumentācija;

import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Equality_;

public interface Argumentācija extends Domable, Equality_<Argumentācija> {
    List<Argumentācija> vietējaArgumentācijas();

    List<Argumentācija> apaķsArgumentācijas();

    @Returns_this
    Argumentācija arApaķsArgumentacija(Argumentācija argumentācija);

    @Returns_this
    Argumentācija argVietējiuArgumentācija(Argumentācija argumentācija);

    List<Argumentācija> paplašinātUzVienkāršaArgumentācija();

    Argumentācija shallowClone();
}
