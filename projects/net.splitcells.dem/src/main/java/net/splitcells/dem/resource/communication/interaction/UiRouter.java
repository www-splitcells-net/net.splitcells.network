package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.interaction.LogMessage;
import org.checkerframework.checker.guieffect.qual.UI;

import static net.splitcells.dem.data.set.map.Maps.map;

public class UiRouter implements Ui {
    public static UiRouter uiRouter() {
        return new UiRouter();
    }

    private final Map<List<String>, Ui> routing = map();

    private UiRouter() {

    }

    @Override
    public <R extends ListWA<LogMessage<Perspective>>> R append(LogMessage<Perspective> arg) {
        return (R) this;
    }

    @Override
    public void close() {
        routing.values().forEach(Ui::close);
        routing.clear();
    }

    @Override
    public void flush() {
        routing.values().forEach(Ui::flush);
    }
}
