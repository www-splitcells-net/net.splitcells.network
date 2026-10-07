/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server;

import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.slf4j.LoggerFactory;

/**
 * Provides configurations that is useful for local tests or CI pipelines.
 */
@JavaLegacy
public class TestCell implements Cell {
    @Override public String groupId() {
        return "net.splitcells";
    }

    @Override public String artifactId() {
        return "webiste.server." + getClass().getSimpleName().toLowerCase();
    }

    @Override public void accept(Environment env) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        LoggerFactory.getLogger(env.config().configValue(ProgramName.class)).debug("Initialize simple console logger for SLF4j.");
    }
}