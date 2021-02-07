package net.splitcells.dem.source.code;

import net.splitcells.dem.resource.host.Files;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;

import javax.xml.transform.Source;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.resource.host.Files.walk_recursively;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

public class SourceCodeCheck {
    public static void main(String... arg) {
        walk_recursively(Paths.get("src/main/java/"))
                .filter(Files::is_file)
                .forEach(SourceCodeCheck::check_Java_source_code);
    }

    private static void check_Java_source_code(Path file) {
        try {
            System.out.println("Checking file: " + file);
            final var lexer = new net.splitcells.dem.source.code.antlr.Java_11_lexer
                    (CharStreams.fromFileName(file.toString()));
            final var parser = new net.splitcells.dem.source.code.antlr.Java_11_parser(new CommonTokenStream(lexer));
            parser.addErrorListener(new DiagnosticErrorListener());
            parser.setErrorHandler(new BailErrorStrategy());
            parser.source_unit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
