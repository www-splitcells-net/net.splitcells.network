package net.splitcells.website.server.translation.to.html;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.Xml;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * The <a href="http://saxon.sourceforge.net/">SAXON The XSLT and XQuery
 * Processor</a> is used by default.
 */
public class XslTransformer {

    static {
        // Defines the used XSL processor.
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
    }

    private final Transformer transformer;

    public XslTransformer(InputStream xsl, URIResolver uriSolver, Map<String, String> parameters) {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setURIResolver(uriSolver);
        try {
            Templates template = factory.newTemplates(new StreamSource(xsl));
            transformer = template.newTransformer();
            transformer.setParameter("siteFolder", Paths.get(".").toAbsolutePath().toString() + File.separator);
            // TODO REMOVE
            parameters.forEach(transformer::setParameter);
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
            transform(new FileInputStream(input.toFile()), outputStream);
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
