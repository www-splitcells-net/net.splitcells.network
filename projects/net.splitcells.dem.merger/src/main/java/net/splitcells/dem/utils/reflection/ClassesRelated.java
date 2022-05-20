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
package net.splitcells.dem.utils.reflection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;

import java.util.Optional;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.reflection.ClassRelatedI.classRelated;

public class ClassesRelated {

	private ClassesRelated() {
		throw constructorIllegal();
	}

	private static final ClassRelated INSTANCE = classRelated();

	public static List<Class<?>> allClassesOf(String packageName) {
		return INSTANCE.allClassesOf(packageName);
	}

	public static List<Class<?>> allClasses() {
		return INSTANCE.allClasses();
	}

	public static Class<?> callerClass() {
		return INSTANCE.callerClass();
	}

	public static Class<?> callerClass(int i) {
		return INSTANCE.callerClass(i);
	}

	public static String simplifiedName(Class<?> arg) {
		return INSTANCE.simplifiedName(arg);
	}

    public static boolean isSubClass(Class<?> superClass, Class<?> subClass) {
        return superClass.isAssignableFrom(subClass);
    }

	@SuppressWarnings("unchecked")
    public static <T> Optional<T> downCast(Class<T> type, Object arg) {
		if (isSubClass(type, arg.getClass())) {
			return Optional.of((T) arg);
		}
		return Optional.empty();
	}

	/**
	 * Loads the resources of a class, typically located in the src/main/resources of the projects source.
	 * Note that only the resources of {@param clazz}'s project will be ready.
	 * If there is a project with the same {@param resourceName} in the same package, this resource is ignored.
	 *
	 * @param clazz
	 * @param resourceName
	 * @return
	 */
    @JavaLegacyBody
	public static String resourceOfClass(Class<?> clazz, String resourceName) {
		try {
			return new String(clazz.getClassLoader().getResourceAsStream(resourceName).readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
		} catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
	}
}
