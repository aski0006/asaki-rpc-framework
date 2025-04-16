package com.example.rpc.function.interfaces;

import com.example.rpc.function.model.Recruitment;

import java.util.List;

public interface RecruitmentModify {
    Recruitment searchRecruitmentById(Long id);
    void updateRecruitmentById(Long id, Recruitment recruitment);
    void deleteRecruitmentById(Long id);
    void addRecruitment(Recruitment recruitment);
    List<Recruitment> getAllRecruitments();
}
