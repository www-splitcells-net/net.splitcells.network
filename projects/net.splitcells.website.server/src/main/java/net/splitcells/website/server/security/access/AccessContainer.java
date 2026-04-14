/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.access;

import lombok.val;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.website.server.security.authentication.UserSession;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.website.server.security.authentication.Authentication.lifeCycleId;
import static net.splitcells.website.server.security.authentication.Authentication.name;

public class AccessContainer<T> implements AccessProvider<T> {
    public static <R> AccessContainer<R> accessContainer(Function<UserSession, R> argDataCreator) {
        return new AccessContainer<>(argDataCreator);
    }

    private final Function<UserSession, T> dataCreator;
    private final Map<DataKey, T> content = map();

    private record DataKey(String username, String lifeCycleId) {
    }

    private AccessContainer(Function<UserSession, T> argDataCreator) {
        dataCreator = argDataCreator;
    }

    @Override public void access(Consumer<T> accessor, UserSession userSession) {
        access(accessor, userSession, lifeCycleId(userSession));
    }

    @Override public void access(Consumer<T> accessor, UserSession userSession, String lifeCycleId) {
        val dataKey = new DataKey(name(userSession), lifeCycleId);
        final T dataValue;
        if (content.hasKey(dataKey)) {
            dataValue = content.value(dataKey);
        } else {
            dataValue = dataCreator.apply(userSession);
            content.put(dataKey, dataValue);
        }
        accessor.accept(dataValue);
    }
}
