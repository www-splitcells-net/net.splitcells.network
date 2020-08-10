package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.resource.communication.Sender;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static net.splitcells.dem.resource.communication.interaction.Dsui.dsui;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class DsuiTest {
    private interface StringSender extends Sender<String> {

    }
    @Test
    public void testSenderOnlyClosedOnce() {
        final var verifier = Mockito.mock(StringSender.class);
        dsui(verifier, message -> true).close();
        verify(verifier).close();
    }
    @Test
    public void testEndMessage() {
        final var verifier = Mockito.mock(StringSender.class);
        dsui(verifier, message -> true).close();
        verify(verifier).append(eq("</d:execution>"));
        verify(verifier).flush();
    }
}
