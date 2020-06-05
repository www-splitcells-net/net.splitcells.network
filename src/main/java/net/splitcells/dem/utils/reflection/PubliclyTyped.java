package net.splitcells.dem.utils.reflection;

import java.util.Optional;

import static net.splitcells.dem.utils.reflection.ClassesRelated.isSubClass;

public interface PubliclyTyped<T> {

    Class<? extends T> type();

    @SuppressWarnings("unchecked")
	default <R> Optional<R> casted(Class<? extends R> targetType) {
        if (isSubClass(targetType, type())) {
            return Optional.of((R) this);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
	default <R> R cast(Class<? extends R> targetType) {
        if (isSubClass(targetType, type())) {
            return (R) this;
        }
        throw new IllegalArgumentException(targetType.toString());
    }
}
