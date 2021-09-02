package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.dem.resource.host.interaction.LogMessage;
import org.w3c.dom.Node;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * Pdsui = Path Based Dom Stream User Interface
 */
public class Pdsui implements Sui<LogMessage<Node>>, Flushable {

    public Pdsui pdsui() {
        return new Pdsui();
    }

    private Pdsui() {

    }

    @Override
    public <R extends ListWA<LogMessage<Node>>> R append(LogMessage<Node> arg) {
        throw notImplementedYet();
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
