package com.example.rpc.core;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>ClassScanner 是一个用于扫描指定包下带有特定注解的类的工具类。</p>
 * <p>该类提供了静态方法 `scan`，用于扫描指定包路径下所有带有指定注解的类，并返回这些类的列表。</p>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                // 处理文件系统目录
                System.out.println("Scanning file system directory: " + resource.getFile());
                File directory = new File(resource.getFile());
                if (directory.exists()) {
                    scanDirectory(directory, packageName, annotationClass, classes);
                }
            } else if (resource.getProtocol().equals("jar")) {
                System.out.println("Scanning jar file: " + resource.getFile());
                processJarResource(resource, packageName, annotationClass, classes, classLoader);
            }
        }
        return classes;
    }

    public static List<Class<?>> scanClasses(String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File directory = new File(resource.getFile());
                scanDirectory(directory, packageName, classes);
            }
        }
        return classes;
    }

    private static void scanDirectory(File directory, String packageName, List<Class<?>> classes) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replace(".class", "");
                classes.add(Class.forName(className));
            }
        }
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

    private static void processJarResource(URL jarUrl, String packageName, Class<? extends Annotation> annotationClass, List<Class<?>> classes, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        JarURLConnection jarConn = (JarURLConnection) jarUrl.openConnection();
        JarFile jarFile = jarConn.getJarFile();
        String expectedPath = packageName.replace('.', '/');

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            // 过滤类文件并匹配包路径
            if (entryName.startsWith(expectedPath) && entryName.endsWith(".class")) {
                String className = entryName.replace('/', '.')
                        .substring(0, entryName.length() - 6); // 去掉.class后缀
                Class<?> clazz = classLoader.loadClass(className);
                if (clazz.isAnnotationPresent(annotationClass)) {
                    classes.add(clazz);
                }
            }
        }
        jarFile.close();
    }
}
