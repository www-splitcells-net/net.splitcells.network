/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.projects;

import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.security.authentication.UserSession;

import java.util.Optional;

public class RenderRequest implements Request<Optional<byte[]>> {
    public static RenderRequest renderRequest(Trail trail, Optional<byte[]> data, UserSession userSession) {
        return new RenderRequest(trail, data, userSession);
    }

    private Trail trail;
    private final Optional<byte[]> data;
    private final UserSession userSession;

    private RenderRequest(Trail trailArg, Optional<byte[]> dataArg, UserSession userSessionArg) {
        trail = trailArg;
        data = dataArg;
        userSession = userSessionArg;
    }

    public Optional<byte[]> data() {
        return data;
    }

    public Trail trail() {
        return trail;
    }

    /**
     * @return The user, that sent the request.
     * I did not name it requester, because otherwise a function pointer would be `RenderRequest::requester`.
     * This makes it harder to read, as a word would kind of be present 2 times.
     */
    public UserSession userSession() {
        return userSession;
    }
}
