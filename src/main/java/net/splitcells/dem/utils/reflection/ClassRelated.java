package net.splitcells.dem.utils.reflection;

import java.io.InputStream;
import java.util.List;

public interface ClassRelated {

	List<Class<?>> allClasses();

	List<Class<?>> allClassesOf(String root_package);

	Class<?> callerClass();

	Class<?> callerClass(int i);

	String simplifiedName(Class<?> arg);

	default InputStream resourceOfClass(Class<?> clazz, String resourceName) {
		return clazz.getClassLoader().getResourceAsStream(resourceName);
	}

}
