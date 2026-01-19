package com.kiennt.hrManagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    public String code;

    @Column(name = "full_name", nullable = false, length = 100)
    public String fullName;

    @Email
    @Column(name = "email", unique = true, nullable = false, length = 100)
    public String email;

    @Column(name = "phone", length = 20)
    public String phone;

    @Column(name = "position", length = 100)
    public String position;

    @Column(name = "date_of_birth")
    public LocalDate dateOfBirth;

    @Column(name = "start_date")
    public LocalDate startDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    public Department department;

    @Column(name = "status", nullable = false)
    @Builder.Default
    public Integer status = 1; // 1=ACTIVE, 0=INACTIVE

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    public List<TransferHistory> transferHistories = new ArrayList<>();
}
