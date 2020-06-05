package net.splitcells.dem.utils.reflection;

import java.util.List;

public interface ClassRelated {

	List<Class<?>> allClasses();

	List<Class<?>> allClassesOf(String root_package);

	Class<?> callerClass();

	Class<?> callerClass(int i);

	String simplifiedName(Class<?> arg);

}
