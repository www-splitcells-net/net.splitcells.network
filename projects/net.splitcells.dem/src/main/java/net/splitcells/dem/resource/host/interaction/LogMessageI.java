package net.splitcells.dem.resource.host.interaction;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;

public class LogMessageI<T> implements LogMessage<T> {

	public static <T> LogMessage<T> logMessage(T content, Discoverable context, LogLevel priority) {
		return new LogMessageI<T>(content, context, priority);
	}

	private final T content;
	private final LogLevel priority;
	private final Discoverable context;

	private LogMessageI(T content, Discoverable context, LogLevel priority) {
		this.content = content;
		this.context = context;
		this.priority = priority;

	}

	@Override
	public T content() {
		return content;
	}

	@Override
	public LogLevel priority() {
		return priority;
	}

	@Override
	public List<String> path() {
		return context.path();
	}

}
