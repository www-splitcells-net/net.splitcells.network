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
package net.splitcells.dem.utils.reflection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.io.File;
import java.net.URL;

import static net.splitcells.dem.data.set.list.Lists.list;

@JavaLegacy
public class ClassRelatedI implements ClassRelated {

    public static ClassRelated classRelated() {
        return new ClassRelatedI();
    }

    private ClassRelatedI() {
    }

    @Override
    public List<Class<?>> allClassesOf(String rootPackage) {
        final var classLoader = Thread.currentThread().getContextClassLoader();
        final var path = rootPackage.replace('.', '/');
        try {
            final var resources = classLoader.getResources(path);
            final List<File> dirs = list();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            final List<Class<?>> classes = list();
            for (File directory : dirs) {
                try {
                    classes.addAll(findClasses(directory, rootPackage));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            return classes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        final List<Class<?>> classes = list();
        if (!directory.exists()) {
            return classes;
        }
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String classPath = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                if (!classPath.contains("$")) {
                    classes.add(Class.forName(classPath));
                }
            }
        }
        return classes;
    }

    @Override
    public List<Class<?>> allClasses() {
        return allClassesOf("");
    }

    @Override
    public String simplifiedName(Class<?> arg) {
        return arg.getName().split("\\$")[0];
    }

}
