/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.website.server.translation.to.html;

import static java.nio.file.Files.newInputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;


public class PathBasedUriResolver implements URIResolver {

    private final Path folder;

    public PathBasedUriResolver(Path folder) {
        this.folder = folder;
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        try {
            final Path path;
            if (href.startsWith("/net.splitcells.website/current/xml")) {
                path = Paths.get("./src/main/xml/" + href.substring("/net.splitcells.website/current/xml".length()));
            } else if (Paths.get(href).isAbsolute()) {
                path = Paths.get(href);
            } else {
                path = folder.resolve(href);
            }
            final var rVal = new StreamSource(newInputStream(path));
            /*
             * Setting systemId to the underlying file in order to resolve relative paths
             * used in the return value.
             */
            rVal.setSystemId(Paths.get(href).toString());
            return rVal;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
