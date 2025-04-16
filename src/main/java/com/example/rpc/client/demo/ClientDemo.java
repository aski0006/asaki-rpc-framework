package com.example.rpc.client.demo;

import com.example.rpc.client.client.RpcClientFactory;
import com.example.rpc.client.exception.RpcClientException;
import com.example.rpc.client.proxy.RpcClientProxy;
import com.example.rpc.function.impl.RecruitmentModifyImpl;
import com.example.rpc.function.interfaces.RecruitmentModify;
import com.example.rpc.function.model.Recruitment;

import java.util.List;

public class ClientDemo {
    public static void main(String[] args) {
        // 创建服务代理（需捕获可能出现的异常）
        try {
            RecruitmentModify recruitmentModify = RpcClientFactory.create(RecruitmentModify.class);

            System.out.println("Remote Server : ");
            System.out.println("Get All Recruitment : ");
            List<Recruitment> list = recruitmentModify.getAllRecruitments();
            for (Recruitment item : list) {
                System.out.println(item);
            }
            System.out.println("-----------------------");
            System.out.println("Add Recruitment : ");
            Recruitment recruitment = new Recruitment(
                    11L,
                    "Java",
                    "Java Developer",
                    "Beijing",
                    "20K ~ 25k",
                    new Recruitment.Requirements(
                            "3 years experience",
                            "USST",
                             List.of("Java", "Spring", "MySQL"),
                            "Good communication skills"
                    ),
                    List.of("Performance Bonus", "Health Insurance", "Paid Leave"),
                    "2023-08-09"
            );
            recruitmentModify.addRecruitment(recruitment);
            System.out.println("----------------------------");
            System.out.println("Get Recruitment By ID : ");
            System.out.println(recruitmentModify.searchRecruitmentById(11L));
            System.out.println("----------------------------");
            System.out.println("Update Recruitment : ");
            recruitment.setSalary("30K ~ 40K");
            recruitmentModify.updateRecruitmentById(11L, recruitment);
            System.out.println("----------------------------");
            System.out.println("Delete Recruitment : ");
            recruitmentModify.deleteRecruitmentById(11L);
            System.out.println("----------------------------");

            System.out.println("Local Function : ");
            RecruitmentModify recruitmentModify2 = new RecruitmentModifyImpl();
            System.out.println("Get All Recruitment : ");
            List<Recruitment> list2 = recruitmentModify2.getAllRecruitments();
            for (Recruitment item : list2) {
                System.out.println(item);
            }
            System.out.println("-----------------------");
            System.out.println("Add Recruitment : ");
            recruitment.setId(12L);
            recruitmentModify2.addRecruitment(recruitment);
            System.out.println("----------------------------");
            System.out.println("Get Recruitment By ID : ");
            System.out.println(recruitmentModify2.searchRecruitmentById(12L));
            System.out.println("----------------------------");
            System.out.println("Update Recruitment : ");
            recruitment.setSalary("30K ~ 40K");
            recruitmentModify2.updateRecruitmentById(12L, recruitment);
            System.out.println("----------------------------");
            System.out.println("Delete Recruitment : ");
            recruitmentModify2.deleteRecruitmentById(12L);
            System.out.println("----------------------------");
            System.out.println("End");
        } catch (RpcClientException e) {
            System.err.println("RPC调用失败: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("系统异常: " + e.getMessage());
        } finally {
            RpcClientProxy.shutdown();
        }
    }
}