package com.vansh.personal_finance_manager.personal_finance_manager.repository;


import com.vansh.personal_finance_manager.personal_finance_manager.entity.SavingsGoal;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsGoalRepositoryTest extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUser(User user);
}

