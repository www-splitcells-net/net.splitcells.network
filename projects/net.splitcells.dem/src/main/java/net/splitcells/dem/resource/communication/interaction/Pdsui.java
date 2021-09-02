package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.resource.host.interaction.LogMessage;
import org.w3c.dom.Node;

import java.util.function.Predicate;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * Pdsui = Path Based Dom Stream User Interface
 */
public class Pdsui implements Sui<LogMessage<Perspective>>, Flushable {

    public Pdsui pdsui(Sender<String> output, Predicate<LogMessage<Perspective>> messageFilter) {
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

    private static void print(Sender<String> output, Perspective content) {
        output.append(content.name());
        if (!content.children().isEmpty()) {
            throw notImplementedYet();
        }
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
