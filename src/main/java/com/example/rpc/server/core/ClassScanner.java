package com.example.rpc.server.core;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * <p>ClassScanner 是一个用于扫描指定包下带有特定注解的类的工具类。</p>
 * <p>该类提供了静态方法 `scan`，用于扫描指定包路径下所有带有指定注解的类，并返回这些类的列表。</p>
 */
public class ClassScanner {

    /**
     * <p>扫描指定包路径下所有带有指定注解的类。</p>
     * <p>该方法会递归地扫描指定包路径下的所有子包，并查找带有指定注解的类。</p>
     *
     * @param packageName   要扫描的包路径
     * @param annotationClass 要查找的注解类
     * @return 包含所有带有指定注解的类的列表
     * @throws ClassNotFoundException 如果类加载失败
     * @throws IOException          如果文件操作失败
     */
    public static List<Class<?>> scan(String packageName, Class<? extends Annotation> annotationClass) throws ClassNotFoundException, IOException {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
        System.out.println("Scanning package path: " + path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.getFile());
            if (directory.exists()) {
                scanDirectory(directory, packageName, annotationClass, classes);
            }
        }

        return classes;
    }

    /**
     * <p>递归扫描指定目录下的所有类文件，并查找带有指定注解的类。</p>
     * <p>该方法会遍历目录中的所有文件和子目录，查找以 `.class` 结尾的文件，并检查这些类是否带有指定注解。</p>
     *
     * @param directory     要扫描的目录
     * @param packageName   当前包路径
     * @param annotationClass 要查找的注解类
     * @param classes       存储带有指定注解的类的列表
     * @throws ClassNotFoundException 如果类加载失败
     */
    private static void scanDirectory(File directory, String packageName, Class<? extends Annotation> annotationClass, List<Class<?>> classes) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    scanDirectory(file, packageName + "." + file.getName(), annotationClass, classes);
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(annotationClass)) {
                        classes.add(clazz);
                    }
                }
            }
        }
    }
}
