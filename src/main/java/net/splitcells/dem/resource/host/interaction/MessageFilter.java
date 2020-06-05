package net.splitcells.dem.resource.host.interaction;

import net.splitcells.dem.environment.config.OptionI;
import org.w3c.dom.Node;

import java.util.function.Predicate;

public class MessageFilter extends OptionI<Predicate<LogMessage<Node>>> {
	public MessageFilter() {
		super(() -> logMessage -> true);
	}
}
