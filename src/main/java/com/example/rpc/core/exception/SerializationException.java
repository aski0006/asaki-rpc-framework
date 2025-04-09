package com.example.rpc.core.exception;

/**
 * <p>SerializationException 是一个用于表示序列化过程中发生异常的运行时异常类。</p>
 * <p>该异常通常用于在对象序列化或反序列化过程中捕获和处理错误。</p>
 * <p>主要功能：</p>
 * <ul>
 *     <li><strong>构造函数</strong>: 提供带有详细错误信息和原因的构造函数，便于异常信息的传递和调试。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class SerializationException extends RuntimeException{
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}