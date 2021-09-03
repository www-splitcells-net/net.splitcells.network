package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.dem.resource.host.interaction.LogMessage;
import org.w3c.dom.Node;

import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.resource.host.interaction.LogMessageI.logMessage;

public interface Ui extends Sui<LogMessage<Perspective>>, Flushable {
    default Ui append(Domable domable, LogLevel logLevel) {
        return append(logMessage(domable.toPerspective(), NO_CONTEXT, logLevel));
    }

    default Ui append(Perspective perspective, LogLevel logLevel) {
        return append(logMessage(perspective, NO_CONTEXT, logLevel));
    }
}
