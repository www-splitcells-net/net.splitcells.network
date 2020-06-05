package net.splitcells.dem.environment.resource;

import net.splitcells.dem.config.StartTime;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.resource.host.OutputPath;
import net.splitcells.dem.resource.host.interaction.IsEchoToFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

import static net.splitcells.dem.Dem.m;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.resource.host.Files.createDirectory;

public final class Console extends ResourceI<Sender<String>> {

    public Console() {
        super(() -> {
            if (m().configValue(IsEchoToFile.class)) {
                var consolePath
                        = m().configValue(OutputPath.class)
                        .resolve("console")
                        .resolve(
                                m().configValue(StartTime.class)
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
        return m().configValue(Console.class);
    }
}
