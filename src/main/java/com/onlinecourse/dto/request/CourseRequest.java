package com.onlinecourse.dto.request;

import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

public class CourseRequest {

    private String name;
    private String description;
    private Integer categoryId;
    private BigDecimal price;
    private String duration;

    private MultipartFile image;
    private MultipartFile videoIntro;

    public CourseRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public MultipartFile getVideoIntro() {
        return videoIntro;
    }

    public void setVideoIntro(MultipartFile videoIntro) {
        this.videoIntro = videoIntro;
    }
}
