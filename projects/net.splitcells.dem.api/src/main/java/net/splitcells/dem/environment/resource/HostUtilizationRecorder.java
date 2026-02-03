/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.resource;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.dem.utils.StringUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.stream.IntStream;

import static net.splitcells.dem.Dem.executeThread;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class HostUtilizationRecorder implements Service {

    public static HostUtilizationRecorder hostUtilizationRecorder() {
        return new HostUtilizationRecorder();
    }

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            .withZone(ZoneId.systemDefault());

    private final Clock clock = Clock.systemDefaultZone();

    private final Runtime runtime = Runtime.getRuntime();
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private final OperatingSystemMXBean osBeam = ManagementFactory.getOperatingSystemMXBean();
    private final List<Instant> times = list();

    private final List<Double> cpuCoreAverageLoad = list();
    private final List<Long> maxMemory = list();

    private final List<Long> usedMemory = list();
    private final List<Long> freeAllocatedMemory = list();

    private final List<Long> usedHeapMemory = list();
    private final List<Long> usedNoneHeapMemory = list();
    private volatile boolean isRunning = false;

    private HostUtilizationRecorderAccess access = new HostUtilizationRecorderAccess() {

        @Override
        public ListView<Instant> times() {
            return HostUtilizationRecorder.this.times;
        }

        @Override
        public ListView<Long> maxMemory() {
            return HostUtilizationRecorder.this.maxMemory;
        }
    };

    private HostUtilizationRecorder() {
        clock.millis();
    }

    @Override
    public synchronized void start() {
        if (isRunning) {
            throw ExecutionException.execException(getClass().getName() + " is already running.");
        }
        isRunning = true;
        executeThread(HostUtilizationRecorder.class.getName(), () -> {
            while (HostUtilizationRecorder.this.isRunning) {
                synchronized (HostUtilizationRecorder.this) {
                    times.add(clock.instant());
                    final var heapMemoryUsage = memoryBean.getHeapMemoryUsage();
                    final var nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();
                    cpuCoreAverageLoad.add(osBeam.getSystemLoadAverage() / osBeam.getAvailableProcessors());
                    maxMemory.add(runtime.maxMemory());
                    usedMemory.add(runtime.totalMemory() - runtime.freeMemory());
                    freeAllocatedMemory.add(runtime.freeMemory());
                    usedHeapMemory.add(heapMemoryUsage.getUsed());
                    usedNoneHeapMemory.add(nonHeapMemoryUsage.getUsed());
                }
                Dem.sleepAtLeast(1000);
            }
        });
    }

    @Override
    public synchronized void close() {
        isRunning = false;
    }

    @Override
    public synchronized void flush() {
        // Nothings needs to be done.
    }

    public synchronized <Output> Output process(Function<HostUtilizationRecorderAccess, Output> processor) {
        return processor.apply(access);
    }

    public String memoryUtilizationReportAsCsv() {
        return process(s -> {
            final var csv = StringUtils.stringBuilder();
            csv.append("time,max memory,used memory,free allocated memory, used heap memory, used none heap memory\n");
            final var measurementCount = s.times().size();
            IntStream.range(0, measurementCount).forEach(i -> {
                csv.append(DATE_TIME_FORMAT.format(s.times().get(i)));
                csv.append(",");
                csv.append(maxMemory.get(i));
                csv.append(",");
                csv.append(usedMemory.get(i));
                csv.append(",");
                csv.append(freeAllocatedMemory.get(i));
                csv.append(",");
                csv.append(usedHeapMemory.get(i));
                csv.append(",");
                csv.append(usedNoneHeapMemory.get(i));
                csv.append("\n");
            });
            return csv.toString();
        });
    }

    public String cpuUtilizationReportAsCsv() {
        return process(s -> {
            final var csv = StringUtils.stringBuilder();
            csv.append("time,cpu core average load\n");
            final var measurementCount = s.times().size();
            IntStream.range(0, measurementCount).forEach(i -> {
                csv.append(DATE_TIME_FORMAT.format(s.times().get(i)));
                csv.append(",");
                csv.append(cpuCoreAverageLoad.get(i));
                csv.append("\n");
            });
            return csv.toString();
        });
    }
}
