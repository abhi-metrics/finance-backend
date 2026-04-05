package com.finance.repository;

import com.finance.model.FinancialRecord;
import com.finance.model.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // ---- Filtered queries ----

    Page<FinancialRecord> findByType(RecordType type, Pageable pageable);

    Page<FinancialRecord> findByCategory(String category, Pageable pageable);

    Page<FinancialRecord> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<FinancialRecord> findByTypeAndCategory(RecordType type, String category, Pageable pageable);

    Page<FinancialRecord> findByTypeAndDateBetween(RecordType type, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<FinancialRecord> findByCategoryAndDateBetween(String category, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<FinancialRecord> findByTypeAndCategoryAndDateBetween(RecordType type, String category,
                                                              LocalDate startDate, LocalDate endDate, Pageable pageable);

    // ---- Dashboard aggregation queries ----

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.type = :type")
    BigDecimal sumAmountByType(@Param("type") RecordType type);

    @Query("SELECT COUNT(r) FROM FinancialRecord r")
    long countAllRecords();

    @Query("SELECT r.category, SUM(r.amount), COUNT(r) FROM FinancialRecord r " +
           "WHERE r.type = :type GROUP BY r.category ORDER BY SUM(r.amount) DESC")
    List<Object[]> getCategorySummary(@Param("type") RecordType type);

    @Query("SELECT YEAR(r.date), MONTH(r.date), r.type, SUM(r.amount) " +
           "FROM FinancialRecord r " +
           "GROUP BY YEAR(r.date), MONTH(r.date), r.type " +
           "ORDER BY YEAR(r.date) DESC, MONTH(r.date) DESC")
    List<Object[]> getMonthlyTrends();

    List<FinancialRecord> findTop10ByOrderByCreatedAtDesc();
}
