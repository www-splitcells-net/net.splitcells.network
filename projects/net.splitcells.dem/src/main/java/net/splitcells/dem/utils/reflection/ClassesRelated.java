package net.splitcells.dem.utils.reflection;

import net.splitcells.dem.utils.ConstructorIllegal;

import java.io.InputStream;
import java.util.List;

import static net.splitcells.dem.utils.reflection.ClassRelatedI.classRelated;

public class ClassesRelated {

	private ClassesRelated() {
		throw new ConstructorIllegal();
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

	/**
	 * Loads the resources of a class, typically located in the src/main/resources of the projects source.
	 * Note that only the resources of {@param clazz}'s project will be ready.
	 * If there is a project with the same {@param resourceName} in the same package, this resource is ignored.
	 *
	 * @param clazz
	 * @param resourceName
	 * @return
	 */
	public static InputStream resourceOfClass(Class<?> clazz, String resourceName) {
		return clazz.getClassLoader().getResourceAsStream(resourceName);
	}
}
