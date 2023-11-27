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
package net.splitcells.website.server.translation.to.html;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static net.splitcells.dem.resource.communication.log.Logs.logs;

/**
 * The <a href="http://saxon.sourceforge.net/">SAXON The XSLT and XQuery
 * Processor</a> is used by default.
 */
@JavaLegacyArtifact
public class XslTransformer {

    static {
        // Defines the used XSL processor.
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
    }

    private final Transformer transformer;

    private static ErrorListener errorListener() {
        return new ErrorListener() {

            @Override
            public void warning(TransformerException exception) throws TransformerException {
                logs().appendWarning("XML transformation warning", exception);
            }

            @Override
            public void error(TransformerException exception) throws TransformerException {
                System.out.println(1234);
                logs().appendWarning("XML transformation error", exception);
            }

            @Override
            public void fatalError(TransformerException exception) throws TransformerException {
                System.out.println(1235);
                logs().appendWarning("Fatal error XML transformation", exception);
            }
        };
    }

    public XslTransformer(InputStream xsl, URIResolver uriSolver) {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setErrorListener(errorListener());
        factory.setURIResolver(uriSolver);
        try {
            Templates template = factory.newTemplates(new StreamSource(xsl));
            transformer = template.newTransformer();
            transformer.setErrorListener(errorListener());
            transformer.setParameter("siteFolder", Paths.get(".").toAbsolutePath().toString() + File.separator);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] transform(Path input) {
        try {
            final var transformed = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(Xml.parse(input), input.toString())
                    , new StreamResult(transformed));
            return transformed.toByteArray();
        } catch (TransformerException e) {
            throw new RuntimeException("Cannot transform file: " + input, e);
        }
    }

    public void transform(Path input, Path output) {
        /*
         * The call of close on BufferedOutputStream, does cause a flush.
         *
         * @see <a href=
         * "http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/java/io/FilterOutputStream.java#FilterOutputStream.close%28%29"
         * />
         */
        try (final var outputStream = new BufferedOutputStream(new FileOutputStream(output.toFile()))) {
            try (final var inputStream = new FileInputStream(input.toFile())) {
                transform(inputStream, outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot transform file: " + input, e);
        }
    }

    public void transform(InputStream input, OutputStream output) {
        try {
            Source source = new StreamSource(input);
            Result result = new StreamResult(output);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public String transform(InputStream content) {
        final var resultContainer = new ByteArrayOutputStream();
        transform(content, resultContainer);
        return resultContainer.toString();
    }

    public String transform(String content) {
        try (final var input = new BufferedInputStream(new ByteArrayInputStream(content.getBytes()))) {
            return transform(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void create(OutputStream output) {
        try {
            Source source = new StreamSource();
            Result result = new StreamResult(output);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
