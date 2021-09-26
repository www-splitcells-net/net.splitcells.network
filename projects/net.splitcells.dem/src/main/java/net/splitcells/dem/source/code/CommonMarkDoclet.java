package net.splitcells.dem.source.code;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.SourceVersion;
import java.util.Locale;
import java.util.Set;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class CommonMarkDoclet implements Doclet {
    @Override
    public void init(Locale locale, Reporter reporter) {
        throw notImplementedYet();
    }

    @Override
    public String getName() {
        throw notImplementedYet();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        throw notImplementedYet();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        throw notImplementedYet();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        throw notImplementedYet();
    }
}
