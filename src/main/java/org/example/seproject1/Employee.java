package org.example.seproject1.model;

public class Employee {
    private Integer employeeId;
    private String employeeCode;
    private Integer userId;
    private String fullName;
    private String nic;
    private String dob;
    private String phone;
    private String email;
    private String address;
    private Integer departmentId;
    private Integer positionId;
    private String hireDate;
    private Double baseSalary;
    private Integer annualLeaveEntitlement;
    private String status;

    public Employee() {}

    // Getters and Setters
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public Integer getPositionId() { return positionId; }
    public void setPositionId(Integer positionId) { this.positionId = positionId; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public Double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(Double baseSalary) { this.baseSalary = baseSalary; }

    public Integer getAnnualLeaveEntitlement() { return annualLeaveEntitlement; }
    public void setAnnualLeaveEntitlement(Integer annualLeaveEntitlement) { this.annualLeaveEntitlement = annualLeaveEntitlement; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
