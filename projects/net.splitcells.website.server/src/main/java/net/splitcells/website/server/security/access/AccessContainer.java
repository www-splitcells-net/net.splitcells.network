/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.access;

import lombok.val;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.website.server.security.authentication.Authenticator;
import net.splitcells.website.server.security.authentication.Login;
import net.splitcells.website.server.security.authentication.UserSession;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.website.server.security.authentication.Authentication.*;

/**
 * <p>Provides data specific to {@link UserSession} and {@link Authenticator#lifeCycleId(UserSession)}.
 * In other words, user specific data can be stored and accessed here.</p>
 * <p>TODO Currently, the life cycle of the data has to be managed manually. This is not good.</p>
 *
 * @param <T>
 */
public class AccessContainer<T> implements AccessControl<T> {
    public static <R> AccessContainer<R> accessContainer() {
        return new AccessContainer<>();
    }

    private final Map<DataKey, T> content = map();

    private record DataKey(String username, String lifeCycleId) {
    }

    private AccessContainer() {
    }

    @Override public void access(BiConsumer<UserSession, T> action, Login login) {
        val userSession = userSession(login);
        try {
            access(action, userSession);
        } finally {
            endSession(userSession);
        }
    }

    @Override public synchronized void access(Consumer<T> action, UserSession userSession) {
        access(action, userSession, lifeCycleId(userSession));
    }

    @Override public synchronized void access(Consumer<T> action, UserSession userSession, String lifeCycleId) {
        val dataKey = new DataKey(name(userSession), lifeCycleId);
        final T dataValue;
        if (content.hasKey(dataKey)) {
            dataValue = content.value(dataKey);
        } else {
            throw execException("No data exists for given user session and lifeCycleId.");
        }
        action.accept(dataValue);
    }

    @Override public synchronized <R> R process(Function<T, R> processor, UserSession userSession, String lifeCycleId) {
        val dataKey = new DataKey(name(userSession), lifeCycleId);
        final T dataValue;
        if (content.hasKey(dataKey)) {
            dataValue = content.value(dataKey);
        } else {
            throw execException("No data exists for given user session and lifeCycleId.");
        }
        return processor.apply(dataValue);
    }

    public synchronized <R> R createAndAccess(Function<UserSession, T> accessSupplier, Function<T, R> processor, UserSession userSession) {
        val dataKey = new DataKey(name(userSession), lifeCycleId(userSession));
        final T dataValue;
        if (content.hasKey(dataKey)) {
            throw execException("Data should not exist for given user session and lifeCycleId, but it does.");
        } else {
            dataValue = accessSupplier.apply(userSession);
            content.put(dataKey, dataValue);
        }
        try {
            return processor.apply(dataValue);
        } catch (RuntimeException e) {
            content.remove(dataKey);
            throw e;
        }
    }

    public synchronized AccessContainer<T> delete(UserSession userSession) {
        return delete(userSession, lifeCycleId(userSession));
    }

    public synchronized AccessContainer<T> delete(UserSession userSession, String lifeCycleId) {
        content.remove(new DataKey(name(userSession), lifeCycleId));
        return this;
    }
}
