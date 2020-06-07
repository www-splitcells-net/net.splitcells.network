package net.splitcells.dem.execution;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.dem.resource.communication.interaction.Dsui;

import java.util.function.Supplier;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.execution.EventProcessorExecutor.eventProcessorExecutor;

public class EventProcessorExecution extends ResourceI<EventProcessorExecutor> {
    public EventProcessorExecution() {
        super(() -> {
            final var executor = eventProcessorExecutor();
            executor.start();
            return executor;
        });
    }

    public static void register(EventProcessor processor) {
        environment().config().configValue(EventProcessorExecution.class).register(processor);
    }
}
