package com.example.rpc.core.Serialize;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonSerializerTest {

    private JacksonSerializer serializer;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        serializer = new JacksonSerializer();
        mapper = new ObjectMapper();
    }

    @Test
    public void serialize_SerializableObject_ReturnsByteArray() {
        // 准备
        TestObject testObject = new TestObject("test", 123);

        // 执行
        byte[] result = serializer.serialize(testObject);

        // 断言
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    public void serialize_NonSerializableObject_ThrowsRuntimeException() {
        // 准备
        NonSerializableObject nonSerializableObject = new NonSerializableObject();

        // 执行和断言
        assertThrows(RuntimeException.class, () -> serializer.serialize(nonSerializableObject));
    }

    @Test
    public void deserialize_ValidJson_ReturnsObject() throws Exception {
        // 准备
        String json = "{\"name\":\"John\", \"age\":30}";
        byte[] bytes = json.getBytes();
        Person expectedPerson = new Person("John", 30);

        // 执行
        Person actualPerson = serializer.deserialize(bytes, Person.class);

        // 断言
        assertEquals(expectedPerson.getName(), actualPerson.getName());
        assertEquals(expectedPerson.getAge(), actualPerson.getAge());
    }

    @Test
    public void deserialize_InvalidJson_ThrowsRuntimeException() {
        // 准备
        String invalidJson = "This is not a valid JSON";
        byte[] bytes = invalidJson.getBytes();

        // 断言
        assertThrows(RuntimeException.class, () -> {
            serializer.deserialize(bytes, Person.class);
        });
    }

    @Test
    public void deserialize_MismatchedJson_ThrowsRuntimeException() throws Exception {
        // 准备
        String json = "{\"name\":\"John\"}"; // 缺少年龄字段
        byte[] bytes = json.getBytes();

        // 断言
        assertThrows(RuntimeException.class, () -> {
            serializer.deserialize(bytes, Person.class);
        });
    }

    // 用于测试的辅助类
    static class TestObject {
        @JsonProperty("name")
        private String name;

        @JsonProperty("age")
        private int age;

        public TestObject() {}

        @JsonCreator
        public TestObject(
                @JsonProperty("name") String name,
                @JsonProperty("age") int age) {
            this.name = name;
            this.age = age;
        }
    }

    // 用于测试的辅助类
    static class NonSerializableObject {
        private transient String secret = "secret"; // 无法序列化
    }

    // 用于测试的辅助类
    static class Person {
        @JsonProperty("name")
        private String name;
        @JsonProperty("age")
        private int age;


        public Person() {}
        @JsonCreator
        public Person(
                 String name,
                 int age
        ) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}
