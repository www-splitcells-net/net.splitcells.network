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
package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.object.Discoverable;

import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * <p>
 * Instances of this class represent XML/JSON like files.
 * XSD Schema functionality can be created via special classes, that wrap perspectives.
 * </p>
 * <p>
 * XML is used for simple documents, because it provides things in order support needed functionality:
 * it supports namespaces, XSL, XSD and XPATH.
 * The only downside is, that defining schemas with XSD and validating files with these schemas is complicated,
 * costly and 2 languages (Java and XML) need to be supported for things like automatic refactoring.
 * Especially the integration of XSD files into editors is not as easy as one might think.
 * The best way to solve this, is to automatically apply schema validation any time that XML files are processed.
 * </p><p>
 * When these downside are too big, {@link PerspectiveDocument} is used instead.
 * Also keep in mind, that some XML files or just some content may always be XML files, because there may be no alternative
 * format definition.
 * </p><p>
 * Currently {@link PerspectiveDocument} and {@link Perspective} usage are based on XML,
 * because XML is the base of all syntax tree currently.
 * In other words {@link PerspectiveDocument} and {@link Perspective} are compatible to XML,
 * because it can be rendered as XML, but the other way around is not the case.
 * This class will probably be a replacement for XML, if XML does cause to much problems or costs to much development resources.
 * If and when this is the case, a slow migration process will take place, where XML
 * will be made compatible to {@link PerspectiveDocument} and {@link Perspective},
 * by providing an conversion function from XML to {@link PerspectiveDocument} and {@link Perspective}.
 * Compatibility between these system will not be removed after that,
 * because this can be used as bridge with extern file formats.
 * </p><p>
 * Extending classes should always provide an empty constructor,
 * because this is just data and not something like a template.
 * </p>
 */
public abstract class PerspectiveDocument implements Discoverable {

    private final List<String> path;
    private final Perspective perspective;

    protected PerspectiveDocument(Class<?> clazz) {
        this(list(clazz.getPackage().getName().split("\\.")));
    }

    protected PerspectiveDocument(List<String> path) {
        this.path = path;
        perspective = createPerspective();
    }

    protected abstract Perspective createPerspective();

    @Override
    public List<String> path() {
        return path;
    }

    public Perspective perspective() {
        return perspective;
    }
}