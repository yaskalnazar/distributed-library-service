package com.yaskal.library.repository;

import com.yaskal.library.model.LoanHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanHistoryRepository extends JpaRepository<LoanHistory, Long> {

}
