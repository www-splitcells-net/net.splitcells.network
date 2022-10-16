package net.splitcells.dem.resource;

import net.splitcells.dem.data.set.list.List;

import java.util.function.Consumer;

import static net.splitcells.dem.data.set.list.ListI.list;

public class ConnectingConstructorI<T> implements ConnectingConstructor<T> {

    public static <T> ConnectingConstructor<T> connectingConstructor() {
        return new ConnectingConstructorI<>();
    }

    private final List<Consumer<T>> connectors = list();

    private ConnectingConstructorI() {

    }

    @Override
    public ConnectingConstructor<T> withConnector(Consumer<T> connector) {
        connectors.add(connector);
        return this;
    }

    @Override
    public void connect(T subject) {
        connectors.forEach(connector -> connector.accept(subject));
    }
}
