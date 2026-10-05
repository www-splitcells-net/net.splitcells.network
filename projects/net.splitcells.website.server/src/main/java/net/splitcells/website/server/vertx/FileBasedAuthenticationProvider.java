/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import lombok.val;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.website.server.security.authentication.Authentication;
import net.splitcells.website.server.security.authentication.Authenticator;
import net.splitcells.website.server.security.authentication.Login;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.security.authentication.UserSession.ANONYMOUS_USER_SESSION;
import static net.splitcells.website.server.security.authentication.UserSession.INSECURE_USER_SESSION;

@JavaLegacy
public class FileBasedAuthenticationProvider implements AuthenticationProvider {
    public static final String LOGIN_KEY = FileBasedAuthenticationProvider.class.getName() + ".login.key";

    public static FileBasedAuthenticationProvider fileBasedAuthenticationProvider() {
        return new FileBasedAuthenticationProvider();
    }

    private final Authenticator authenticator = configValue(Authentication.class);

    private FileBasedAuthenticationProvider() {
    }

    /**
     * This method will never log the password entered by the user or the actual password,
     * in order to avoid security problems.
     *
     * @param credentials The credentials
     */
    @Override public Future<User> authenticate(Credentials credentials) {
        val userCredentials = (UsernamePasswordCredentials) credentials;
        final var username = userCredentials.getUsername();
        final var userSession = authenticator.userSession(Login.login(username, userCredentials.getPassword()));
        if (INSECURE_USER_SESSION.equals(userSession)) {
            return Future.failedFuture("The password for `"
                    + username
                    + "` is unknown.");
        } else if (ANONYMOUS_USER_SESSION.equals(userSession)) {
            return Future.failedFuture("The username `" + username + "` is unknown.");
        }
        final var user = User.fromName(username);
        user.attributes().put(LOGIN_KEY, userSession);
        return Future.succeededFuture(user);
    }
}
