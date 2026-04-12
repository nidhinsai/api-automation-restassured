package com.nidhinsai.api.models;

public class UserPayload {
    private String name;
    private String job;

    public UserPayload() {
    }

    public UserPayload(String name, String job) {
        this.name = name;
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}