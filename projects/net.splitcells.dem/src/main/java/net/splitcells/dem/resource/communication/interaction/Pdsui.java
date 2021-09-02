package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.resource.host.interaction.LogMessage;
import org.w3c.dom.Node;

import java.util.function.Predicate;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * Pdsui = Path Based Dom Stream User Interface
 */
public class Pdsui implements Sui<LogMessage<Node>>, Flushable {

    public Pdsui pdsui(Sender<String> output, Predicate<LogMessage<Node>> messageFilter) {
        return new Pdsui(output, messageFilter);
    }

    private final Sender<String> output;
    private final Predicate<LogMessage<Node>> messageFilter;

    private Pdsui(Sender<String> output, Predicate<LogMessage<Node>> messageFilter) {
        this.output = output;
        this.messageFilter = messageFilter;
    }

    @Override
    public <R extends ListWA<LogMessage<Node>>> R append(LogMessage<Node> arg) {
        if (messageFilter.test(arg)) {
            throw notImplementedYet();
        }
        return (R) this;
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
