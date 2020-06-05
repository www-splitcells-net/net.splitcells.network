package net.splitcells.dem.resource.communication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static java.util.Arrays.asList;
import static net.splitcells.dem.data.set.map.Maps.map;

/**
 * TODO implementation without Flush.
 */
@Deprecated
public class ConnectorI<T extends Flushable> implements Connector<T> {

	private final Map<Class<?>, Map<List<Class<?>>, T>> connections = new HashMap<>();
	private final BiFunction<Class<?>, List<Class<?>>, T> senderF;

	@Deprecated
	public ConnectorI(BiFunction<Class<?>, List<Class<?>>, T> arg_senderF) {
		senderF = arg_senderF;
	}

	@Override
	public T connect(Class<?> address, Class<?>... types) {
		if (!connections.containsKey(address)) {
			connections.put(address, map());
		}
		List<Class<?>> type_list = asList(types);
		if (!connections.get(address).containsKey(type_list)) {
			connections.get(address).put(type_list, senderF.apply(address, type_list));
		}
		return connections.get(address).get(type_list);
	}

	@Override
	public void flush() {
		for (Map<List<Class<?>>, T> i : connections.values()) {
			i.values().forEach(j -> j.flush());
		}
	}
}
