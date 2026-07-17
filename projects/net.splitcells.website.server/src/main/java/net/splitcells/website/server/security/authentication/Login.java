/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authentication;

import static net.splitcells.website.server.security.authentication.UserSession.ANONYMOUS_USER_NAME;

public class Login {
    public static Login login(String username, String password) {
        return new Login(username, password);
    }
    
    public static Login anonymousLogin() {
        return login(ANONYMOUS_USER_NAME, "no-password");
    }

    private final String username;
    private final String password;

    private Login(String argUsername, String argPassword) {
        username = argUsername;
        password = argPassword;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": username = " + username;
    }
}
