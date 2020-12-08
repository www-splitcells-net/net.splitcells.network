package net.splitcells.gel.kodols.ierobežojums.argumentācija;

import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Equality_;

@Deprecated
public interface DabiskaArgumentācija extends Domable, Equality_<DabiskaArgumentācija> {
    List<String> parametri();

    List<DabiskaArgumentācija> apaķsArgumentācijas();

    @Returns_this
    DabiskaArgumentācija arApaķsArgumentacija(DabiskaArgumentācija dabiskaArgumentācija);

    @Returns_this
    DabiskaArgumentācija arParametru(String parametrs);

    List<DabiskaArgumentācija> paplašinātUzVienkāršuArgumentācija();

    DabiskaArgumentācija shallowClone();
}
