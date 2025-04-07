package com.example.rpc.server.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClassScannerTest {

    private Class<? extends Annotation> annotationClass;

    @BeforeEach
    public void setUp() {
        annotationClass = TestAnnotation.class;
    }

    @AfterEach
    public void tearDown() throws IOException {
        // 清除生成的测试类文件和编译后的类文件
        String packageName = "com.example.rpc.server.test";
        deletePackageDirectory("target/test-classes/" + packageName.replace('.', '/'));
        deletePackageDirectory("target/classes/" + packageName.replace('.', '/'));
    }

    @Test
    public void scan_PackageWithAnnotatedClasses_ReturnsClasses() throws Exception {
        // 准备
        String packageName = "com.example.rpc.server.test";
        createTestClass("TestService1", true, packageName);
        createTestClass("TestService2", true, packageName);
        createTestClass("TestService3", false, packageName);

        // 扫描带有 @TestAnnotation 注解的类
        List<Class<?>> result = ClassScanner.scan(packageName, annotationClass);

        // 验证结果
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getSimpleName().equals("TestService1")));
        assertTrue(result.stream().anyMatch(c -> c.getSimpleName().equals("TestService2")));
        assertFalse(result.stream().anyMatch(c -> c.getSimpleName().equals("TestService3")));
    }

    @Test
    public void scan_PackageWithoutAnnotatedClasses_ReturnsEmptyList() throws Exception {
        // 准备
        String packageName = "com.example.rpc.server.test";
        createTestClass("TestService1", false, packageName);
        createTestClass("TestService2", false, packageName);

        // 扫描带有 @TestAnnotation 注解的类
        List<Class<?>> result = ClassScanner.scan(packageName, annotationClass);

        // 验证结果
        assertTrue(result.isEmpty());
    }

    @Test
    public void scan_NonExistentPackage_ReturnsEmptyList() throws Exception {
        // 准备
        String packageName = "non.existent.package";

        // 扫描不存在的包
        List<Class<?>> result = ClassScanner.scan(packageName, annotationClass);

        // 验证结果
        assertTrue(result.isEmpty());
    }

    @Test
    public void scan_ClassDoesNotExist_ThrowsClassNotFoundException() throws Exception {
        // 准备
        String packageName = "com.example.rpc.server.test";
        createTestClass("NonExistentClass", false, packageName);

        // 操作和断言
        try {
            ClassScanner.scan(packageName, annotationClass);
        } catch (ClassNotFoundException e) {
            assertEquals("com.example.rpc.server.test.NonExistentClass", e.getMessage());
        }
    }

    private void createTestClass(String className, boolean withAnnotation, String packageName) throws IOException, InterruptedException {
        StringBuilder classContent = new StringBuilder();
        classContent.append("package ").append(packageName).append(";\n");
        if (withAnnotation) {
            classContent.append("@com.example.rpc.server.core.ClassScannerTest.TestAnnotation\n");
        }
        classContent.append("public class ").append(className).append(" {}\n");

        // 将类内容写入文件
        File packageDir = new File("target/test-classes/" + packageName.replace('.', '/'));
        if (!packageDir.exists()) {
            packageDir.mkdirs();
        }
        File classFile = new File(packageDir, className + ".java");
        try (FileWriter writer = new FileWriter(classFile)) {
            writer.write(classContent.toString());
        }

        // 编译类文件
        Process process = Runtime.getRuntime().exec("javac -cp target/test-classes -d target/test-classes target/test-classes/" + packageName.replace('.', '/') + "/" + className + ".java");
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Compilation failed with exit code " + exitCode);
        }
    }

    @Test
    public void scan_PackageWithAnnotatedClasses_ReturnsClassesB() throws Exception {
        // 准备
        String packageName = "com.example.rpc.server.test";
        createTestClass("TestService1", true, packageName);
        createTestClass("TestService2", true, packageName);
        createTestClass("TestService3", false, packageName);

        // 扫描带有 @TestAnnotation 注解的类
        List<Class<?>> result = ClassScanner.scan(packageName, annotationClass);

        // 验证结果
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getSimpleName().equals("TestService1")));
        assertTrue(result.stream().anyMatch(c -> c.getSimpleName().equals("TestService2")));
        assertFalse(result.stream().anyMatch(c -> c.getSimpleName().equals("TestService3")));
    }

    @Test
    public void scan_PackageWithoutAnnotatedClasses_ReturnsEmptyListB() throws Exception {
        // 准备
        String packageName = "com.example.rpc.server.test";
        createTestClass("TestService1", false, packageName);
        createTestClass("TestService2", false, packageName);

        // 扫描带有 @TestAnnotation 注解的类
        List<Class<?>> result = ClassScanner.scan(packageName, annotationClass);

        // 验证结果
        assertTrue(result.isEmpty());
    }

    @Test
    public void scan_NonExistentPackage_ReturnsEmptyListB() throws Exception {
        // 准备
        String packageName = "non.existent.package";

        // 扫描不存在的包
        List<Class<?>> result = ClassScanner.scan(packageName, annotationClass);

        // 验证结果
        assertTrue(result.isEmpty());
    }

    @Test
    public void scan_ClassDoesNotExist_ThrowsClassNotFoundExceptionB() throws Exception {
        // 准备
        String packageName = "com.example.rpc.server.test";
        createTestClass("NonExistentClass", false, packageName);

        // 操作和断言
        try {
            ClassScanner.scan(packageName, annotationClass);
        } catch (ClassNotFoundException e) {
            assertEquals("com.example.rpc.server.test.NonExistentClass", e.getMessage());
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
    }

    private void deletePackageDirectory(String packagePath) throws IOException {
        File packageDir = new File(packagePath);
        if (packageDir.exists()) {
            deleteDirectory(packageDir);
        }
    }

    private void deleteDirectory(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        throw new IOException("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
        }
        if (!dir.delete()) {
            throw new IOException("Failed to delete directory: " + dir.getAbsolutePath());
        }
    }
}
