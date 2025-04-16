package com.example.rpc.function.tools;


import com.example.rpc.function.model.Recruitment;
import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecruitmentDataLoader {
    // 全局静态列表模拟数据库
    public static final List<Recruitment> RECRUITMENT_DB = new ArrayList<>();

    // 数据加载器（静态初始化块）
    static {
        try {
            // 使用Gson进行JSON解析
            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(
                    Objects.requireNonNull(RecruitmentDataLoader.class.getResourceAsStream("/recruitments.json")),
                    StandardCharsets.UTF_8);

            // 解析JSON到Java对象
            RecruitmentData data = gson.fromJson(reader, RecruitmentData.class);

            // 填充全局列表
            if (data != null && data.recruitments != null) {
                RECRUITMENT_DB.addAll(data.recruitments);
            }
        } catch (Exception e) {
            System.err.println("Error loading recruitment data: " + e.getMessage());
        }
    }
    // JSON数据结构对应的Java类
    private static class RecruitmentData {
        List<Recruitment> recruitments;
    }

}
