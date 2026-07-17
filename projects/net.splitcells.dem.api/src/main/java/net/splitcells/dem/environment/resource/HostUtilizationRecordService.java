/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.resource;

import static net.splitcells.dem.environment.resource.HostUtilizationRecorder.hostUtilizationRecorder;

public class HostUtilizationRecordService extends ResourceOptionImpl<HostUtilizationRecorder> {
    public HostUtilizationRecordService() {
        super(() -> hostUtilizationRecorder());
    }
}
