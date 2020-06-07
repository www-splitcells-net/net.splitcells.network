package net.splitcells.dem.environment.resource;

import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.resource.host.OutputPath;
import net.splitcells.dem.resource.host.interaction.IsEchoToFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.resource.host.Files.createDirectory;

public final class Console extends ResourceI<Sender<String>> {

    public Console() {
        super(() -> {
            if (environment().config().configValue(IsEchoToFile.class)) {
                var consolePath
                        = environment().config().configValue(OutputPath.class)
                        .resolve("console")
                        .resolve(
                                environment().config().configValue(StartTime.class)
                                        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss.nnnn")));
                createDirectory(consolePath);
                try {
                    return stringSender
                            (new FileOutputStream
                                    (consolePath.resolve("echo.xml")
                                            .toFile()));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return stringSender(System.out);
            }
        });
    }

    public static Sender<String> console() {
        return environment().config().configValue(Console.class);
    }
}
