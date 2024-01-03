package net.splitcells.dem.environment.resource;

import static net.splitcells.dem.environment.resource.HostUtilizationRecorder.hostUtilizationRecorder;

public class HostUtilizationRecordService extends ResourceOptionI<HostUtilizationRecorder> {
    public HostUtilizationRecordService() {
        super(() -> hostUtilizationRecorder());
    }
}
