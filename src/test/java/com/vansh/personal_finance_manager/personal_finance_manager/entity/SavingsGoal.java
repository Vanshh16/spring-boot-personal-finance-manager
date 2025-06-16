package com.vansh.personal_finance_manager.personal_finance_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String goalName;

    private BigDecimal targetAmount;

    private LocalDate startDate;

    private LocalDate targetDate;

    private BigDecimal currentProgress = BigDecimal.ZERO;

    @ManyToOne
    private User user;
}
