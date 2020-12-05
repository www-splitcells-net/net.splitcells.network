package net.splitcells.dem.utils.reflection;

import net.splitcells.dem.data.set.list.List;

import java.io.InputStream;

public interface ClassRelated {

    List<Class<?>> allClasses();

    List<Class<?>> allClassesOf(String root_package);

    Class<?> callerClass();

    Class<?> callerClass(int i);

    String simplifiedName(Class<?> arg);

}
