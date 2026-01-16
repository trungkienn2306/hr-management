package com.kiennt.hrManagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "transfer_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_department_id", nullable = false)
    private Department fromDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_department_id", nullable = false)
    private Department toDepartment;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "transfer_date", nullable = false)
    @Builder.Default
    private LocalDateTime transferDate = LocalDateTime.now();
}
