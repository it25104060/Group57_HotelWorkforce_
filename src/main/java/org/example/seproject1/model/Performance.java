package org.example.seproject1.model;

public class Performance {
    private Integer reviewId;
    private Integer employeeId;
    private String employeeName;
    private String reviewDate;
    private String reviewPeriod;
    private Integer rating;
    private String feedback;
    private String reviewerName;
    private String status;

    public Performance() {}

    public Performance(Integer reviewId, Integer employeeId, String employeeName, String reviewDate,
                       String reviewPeriod, Integer rating, String feedback, String reviewerName, String status) {
        this.reviewId = reviewId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.reviewDate = reviewDate;
        this.reviewPeriod = reviewPeriod;
        this.rating = rating;
        this.feedback = feedback;
        this.reviewerName = reviewerName;
        this.status = status;
    }

    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getReviewDate() { return reviewDate; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }

    public String getReviewPeriod() { return reviewPeriod; }
    public void setReviewPeriod(String reviewPeriod) { this.reviewPeriod = reviewPeriod; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}