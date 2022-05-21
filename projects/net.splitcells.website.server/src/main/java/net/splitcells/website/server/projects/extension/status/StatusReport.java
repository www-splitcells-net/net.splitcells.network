package net.splitcells.website.server.projects.extension.status;

import net.splitcells.dem.resource.communication.interaction.LogLevel;

public class StatusReport {

    public static StatusReport statusReport(LogLevel logLevel, String report) {
        return new StatusReport(logLevel, report);
    }

    private final LogLevel logLevel;
    private final String report;

    private StatusReport(LogLevel logLevel, String report) {
        this.logLevel = logLevel;
        this.report = report;
    }

    public LogLevel logLevel() {
        return logLevel;
    }

    public String report() {
        return report;
    }
}
