package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.resource.host.interaction.LogMessage;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * Pdsui = Path Based Dom Stream User Interface
 */
public class Pdsui implements Ui {

    public static Pdsui pdsui(Sender<String> output, Predicate<LogMessage<Perspective>> messageFilter) {
        return new Pdsui(output, messageFilter);
    }

    private final Sender<String> output;
    private final Predicate<LogMessage<Perspective>> messageFilter;

    private Pdsui(Sender<String> output, Predicate<LogMessage<Perspective>> messageFilter) {
        this.output = output;
        this.messageFilter = messageFilter;
    }

    @Override
    public <R extends ListWA<LogMessage<Perspective>>> R append(LogMessage<Perspective> arg) {
        if (messageFilter.test(arg)) {
            print(output, arg.content());
        }
        return (R) this;
    }

    private static void print(Sender<String> output, Perspective content, String prefix) {
        if (content.children().size() == 1) {
            if (prefix.isEmpty()) {
                print(output, content.children().get(0), content.name());
            } else {
                print(output, content.children().get(0), prefix + "." + content.name());
            }
            return;
        } else if (content.children().size() > 0) {
            output.append(prefix + content.name() + ":");
        } else if (content.children().size() == 0) {
            if (prefix.isEmpty()) {
                output.append(content.name());
            } else {
                output.append(prefix + "." + content.name());
            }
            return;
        } else {
            throw new UnsupportedOperationException();
        }
        content.children().forEach(child -> {
            print(Sender.extend(output, "\t", ""), child);
        });
    }

    private static void print(Sender<String> output, Perspective content) {
        print(output, content, "");
    }

    @Override
    public void close() {
        output.close();
    }

    @Override
    public void flush() {
        output.flush();
    }
}
