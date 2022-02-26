package net.splitcells.website.server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * TODO IDEA Use string and enum based mapping as a backend,
 * so that clones can easily be created.
 * This would also make serialization easier.
 * Maybe create a serialization mini framework for that?
 */
public class Config {
    public static Config create() {
        return new Config();
    }

    private Optional<String> sslKeystorePassword = Optional.of("password");
    private Optional<Path> sslKeystoreFile = Optional.of(Paths.get("target/keystore.p12"));
    private Optional<String> layout = Optional.empty();
    private Optional<String> layoutRelevant = Optional.empty();
    private int openPort = 443;
    private String generationStyle = "standard";
    private Optional<String> siteFolder;
    private String rootPath = "/";

    private Config() {
    }

    public Config withLayout(String layout) {
        this.layout = Optional.of(layout);
        return this;
    }

    public Optional<String> layout() {
        return layout;
    }

    public Config withOpenPort(int openPort) {
        this.openPort = openPort;
        return this;
    }

    public int openPort() {
        return openPort;
    }

    public String generationStyle() {
        return generationStyle;
    }

    public Config withGenerationStyle(String arg) {
        generationStyle = arg;
        return this;
    }

    public Optional<String> sslKeystorePassword() {
        return sslKeystorePassword;
    }

    public Optional<Path> sslKeystoreFile() {
        return sslKeystoreFile;
    }

    public Config witSslKeystoreFile(Optional<Path> sslKeystoreFile) {
        this.sslKeystoreFile = sslKeystoreFile;
        return this;
    }

    public Optional<String> siteFolder() {
        return siteFolder;
    }

    public Config withSiteFolder(Optional<String> siteFolder) {
        this.siteFolder = siteFolder;
        return this;
    }

    public Config withLayoutRelevant(String layoutRelevant) {
        this.layoutRelevant = Optional.of(layoutRelevant);
        return this;
    }

    public Optional<String> layoutRelevant() {
        return layoutRelevant;
    }
    
    public Config withRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public String rootPath() {
        return rootPath;
    }
}