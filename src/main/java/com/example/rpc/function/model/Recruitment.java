package com.example.rpc.function.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Recruitment {
    private Long id;
    // 使用@SerializedName处理JSON字段名与Java字段名的映射
    @JsonProperty("companyName")
    private String company;

    private String position;
    private String location;

    @JsonProperty("salaryRange")
    private String salary;

    private Requirements requirements;
    private List<String> benefits;

    @JsonProperty("postDate")
    private String publishDate;
    public Recruitment(){}
    public Recruitment(Long id, String company, String position, String location, String salary, Requirements requirements, List<String> benefits, String publishDate) {
        this.id = id;
        this.company = company;
        this.position = position;
        this.location = location;
        this.salary = salary;
        this.requirements = requirements;
        this.benefits = benefits;
        this.publishDate = publishDate;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public String toString() {
        return "Recruitment{" +
                "id=" + id +
                ", company='" + company + '\'' +
                ", position='" + position + '\'' +
                ", location='" + location + '\'' +
                ", salary='" + salary + '\'' +
                ", requirements=" + requirements +
                ", benefits=" + benefits +
                ", publishDate='" + publishDate + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // 嵌套的Requirements类
    public static class Requirements {
        private String experience;
        private String education;
        private List<String> skills;
        private String additional;
        public Requirements(){}
        public Requirements(String experience, String education, List<String> skills, String additional) {
            this.experience = experience;
            this.education = education;
            this.skills = skills;
            this.additional = additional;
        }

        // Getters
        public String getExperience() { return experience; }
        public String getEducation() { return education; }
        public List<String> getSkills() { return skills; }
        public String getAdditional() { return additional; }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public void setEducation(String education) {
            this.education = education;
        }

        public void setSkills(List<String> skills) {
            this.skills = skills;
        }

        public void setAdditional(String additional) {
            this.additional = additional;
        }

        @Override
        public String toString() {
            return "Requirements{" +
                    "experience='" + experience + '\'' +
                    ", education='" + education + '\'' +
                    ", skills=" + skills +
                    ", additional='" + additional + '\'' +
                    '}';
        }
    }

    // Getter方法（根据需要可以添加Setter）
    public String getCompany() { return company; }
    public String getPosition() { return position; }
    public String getLocation() { return location; }
    public String getSalary() { return salary; }
    public Requirements getRequirements() { return requirements; }
    public List<String> getBenefits() { return benefits; }
    public String getPublishDate() { return publishDate; }

}