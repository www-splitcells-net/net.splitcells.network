/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.website;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Optional;

import static java.util.Arrays.asList;

public class SourceValidatorViaSchema implements SourceValidator {
    public static SourceValidatorViaSchema validatorViaSchema(Path schemaPath) {
        return new SourceValidatorViaSchema(schemaPath);
    }

    private final javax.xml.validation.Validator xmlValidator;

    private SourceValidatorViaSchema(Path schemaPath) {
        try {
            xmlValidator = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(schemaPath.toFile())
                    .newValidator();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<String> validate(Path validationSubject) {
        Source xmlFile = new StreamSource(validationSubject.toFile());
        // TODO schemaFactory.setProperty("http://saxon.sf.net/feature/xsd-version", "1.1");
        try {
            xmlValidator.validate(xmlFile);
        } catch (SAXException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return Optional.of(
                    asList(e.toString()
                            .split("\\;"))
                            .stream()
                            .reduce((a, b) -> a + b)
                            + sw.toString());
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return Optional.of(sw.toString());
        }
        return Optional.empty();
    }
}
