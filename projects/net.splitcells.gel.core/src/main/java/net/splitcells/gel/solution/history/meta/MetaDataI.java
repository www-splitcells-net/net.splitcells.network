package net.splitcells.gel.solution.history.meta;

import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.Xml.toPrettyString;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.common.Language.*;

import java.util.Optional;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import org.w3c.dom.Node;

public class MetaDataI implements MetaDataView, MetaDataWriter {
    public static MetaDataI metaData() {
        return new MetaDataI();
    }

    private final Map<Class<?>, Object> data = map();

    private MetaDataI() {
    }

    @Override
    public <A> MetaDataWriter with(Class<A> type, A value) {
        if (data.containsKey(type)) {
            throw new IllegalArgumentException(type.getName());
        }
        data.put(type, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> value(Class<T> tips) {
        return (Optional<T>) Optional.ofNullable(data.get(tips));
    }

    @Override
    public String toString() {
        return toPrettyString(toDom());
    }

    @Override
    public Node toDom() {
        final var dom = Xml.elementWithChildren(META_DATA.value());
        data.forEach((key, value) -> {
            final var data = Xml.elementWithChildren(META_DATA.value());
            final var keyData = Xml.elementWithChildren(KEY.value());
            keyData.appendChild(textNode(key.getName()));
            final var valueData = Xml.elementWithChildren(VALUE.value());
            {
                if (value instanceof Domable) {
                    valueData.appendChild(((Domable) value).toDom());
                } else {
                    valueData.appendChild(textNode(value.toString()));
                }
            }
            dom.appendChild(keyData);
            dom.appendChild(valueData);
        });
        return dom;
    }
}