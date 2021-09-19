package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.ProcessPath;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.resource.communication.interaction.Pdsui.pdsui;
import static net.splitcells.dem.resource.Files.createDirectory;

/**
 * TODO Log all used {@link LogMessage#path()} to dedicated file.
 */
public class UiRouter implements Ui {

    public static UiRouter uiRouter(Predicate<LogMessage<Perspective>> messageFilter) {
        return new UiRouter(messageFilter);
    }

    private final Map<List<String>, Ui> routing = map();
    private final Predicate<LogMessage<Perspective>> messageFilter;

    private UiRouter(Predicate<LogMessage<Perspective>> messageFilter) {
        this.messageFilter = messageFilter;
    }

    @Override
    public <R extends ListWA<LogMessage<Perspective>>> R append(LogMessage<Perspective> arg) {
        if (messageFilter.test(arg)) {
            if (!routing.containsKey(arg.path())) {
                Path consolePath;
                if (arg.path().size() == 0) {
                    consolePath = environment().config().configValue(ProcessPath.class)
                            .resolve("src")
                            .resolve("main")
                            .resolve("csv");
                    createDirectory(consolePath);
                    consolePath = consolePath.resolve
                            (environment()
                                    .config()
                                    .configValue(StartTime.class)
                                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss.nnnn")));
                } else if (arg.path().size() == 1) {
                    consolePath = environment().config().configValue(ProcessPath.class)
                            .resolve("src")
                            .resolve("main")
                            .resolve("csv");
                    createDirectory(consolePath);
                    consolePath = consolePath.resolve(arg.path().get(0));
                } else {
                    final var filePath = Lists.listWithValuesOf(arg.path());
                    // TODO HACK File Suffix
                    final var file = filePath.remove(filePath.size() - 1) + ".csv";
                    consolePath = environment().config().configValue(ProcessPath.class)
                            .resolve("src")
                            .resolve("main")
                            .resolve("csv");
                    for (String e : filePath) {
                        consolePath = consolePath.resolve(e);
                    }
                    createDirectory(consolePath);
                    consolePath = consolePath.resolve(file);
                }
                try {
                    routing.put(arg.path()
                            , pdsui(stringSender(new FileOutputStream(consolePath.toFile())), messageFilter));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            routing.get(arg.path()).append(arg);
        }
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
