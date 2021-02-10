package net.splitcells.dem.utils.reflection;

import net.splitcells.dem.data.set.list.List;

import java.io.File;
import java.net.URL;

import static net.splitcells.dem.data.set.list.Lists.list;

public class ClassRelatedI extends SecurityManager implements ClassRelated {

    public static ClassRelated classRelated() {
        return new ClassRelatedI();
    }

    private ClassRelatedI() {
    }

    @Override
    public List<Class<?>> allClassesOf(String packageName) {
        final var classLoader = Thread.currentThread().getContextClassLoader();
        final var path = packageName.replace('.', '/');
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
                    classes.addAll(findClasses(directory, packageName));
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
    public Class<?> callerClass() {
        return callerClass(0);
    }

    public Class<?> callerClass(int i) {
        return getClassContext()[1 + i];
    }

    @Override
    public String simplifiedName(Class<?> arg) {
        return arg.getName().split("\\$")[0];
    }

}
