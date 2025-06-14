package com.vansh.personal_finance_manager.personal_finance_manager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryType type; // INCOME or EXPENSE

    private boolean isDefault; // true = system category, false = user-defined

    @ManyToOne
    private User user; // null if default
}

