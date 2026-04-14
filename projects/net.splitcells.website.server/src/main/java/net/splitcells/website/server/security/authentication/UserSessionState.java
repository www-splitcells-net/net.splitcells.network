/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authentication;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class UserSessionState {
    public static UserSessionState userSessionState() {
        return new UserSessionState();
    }
    
    @Getter @Setter String username;
    @Getter @Setter String lifeCycleId;
    
    private UserSessionState() {
        
    }
}
