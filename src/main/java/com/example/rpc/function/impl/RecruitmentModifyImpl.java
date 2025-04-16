package com.example.rpc.function.impl;


import com.example.rpc.function.interfaces.RecruitmentModify;
import com.example.rpc.function.model.Recruitment;
import com.example.rpc.function.tools.RecruitmentDataLoader;
import com.example.rpc.server.annotation.RpcService;

import java.util.List;
@RpcService(interfaceClass = RecruitmentModify.class, version = "1.0.0")
public class RecruitmentModifyImpl implements RecruitmentModify {
    private List<Recruitment> db;
    @Override
    public Recruitment searchRecruitmentById(Long id) {
        if(db == null) db = RecruitmentDataLoader.RECRUITMENT_DB;
        return db.stream().filter(recruitment -> recruitment.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void updateRecruitmentById(Long id, Recruitment recruitment) {
        if(db == null) db = RecruitmentDataLoader.RECRUITMENT_DB;
        db.stream().filter(r -> r.getId().equals(id)).forEach(r -> db.set(db.indexOf(r), recruitment));
        System.out.println("更新成功");
    }

    @Override
    public void deleteRecruitmentById(Long id) {
        if(db == null) db = RecruitmentDataLoader.RECRUITMENT_DB;
        db.removeIf(r -> r.getId().equals(id));
        System.out.println("删除成功");
    }

    @Override
    public void addRecruitment(Recruitment recruitment) {
        if(db == null) db = RecruitmentDataLoader.RECRUITMENT_DB;
        db.add(recruitment);
        System.out.println("添加成功");
    }

    @Override
    public List<Recruitment> getAllRecruitments() {
        if(db == null) db = RecruitmentDataLoader.RECRUITMENT_DB;
        return db;
    }

}