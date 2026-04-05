package com.finance.service;

import com.finance.dto.response.*;
import com.finance.model.RecordType;
import com.finance.model.FinancialRecord;
import com.finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service providing aggregated dashboard data:
 * - Financial summary (total income, expenses, net balance)
 * - Category-wise breakdowns
 * - Monthly income/expense trends
 * - Recent activity feed
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    /**
     * Get overall financial summary.
     */
    public DashboardSummary getSummary() {
        BigDecimal totalIncome = recordRepository.sumAmountByType(RecordType.INCOME);
        BigDecimal totalExpenses = recordRepository.sumAmountByType(RecordType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);
        long totalRecords = recordRepository.countAllRecords();

        return DashboardSummary.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .totalRecords(totalRecords)
                .build();
    }

    /**
     * Get category-wise summary for a given record type.
     * If type is null, returns summary for all types combined.
     */
    public List<CategorySummary> getCategorySummary(RecordType type) {
        if (type == null) {
            // Combine both income and expense by category
            List<CategorySummary> incomeSummary = getCategorySummaryByType(RecordType.INCOME);
            List<CategorySummary> expenseSummary = getCategorySummaryByType(RecordType.EXPENSE);
            List<CategorySummary> combined = new ArrayList<>();
            combined.addAll(incomeSummary);
            combined.addAll(expenseSummary);
            return combined;
        }
        return getCategorySummaryByType(type);
    }

    private List<CategorySummary> getCategorySummaryByType(RecordType type) {
        List<Object[]> results = recordRepository.getCategorySummary(type);
        return results.stream()
                .map(row -> CategorySummary.builder()
                        .category((String) row[0])
                        .totalAmount((BigDecimal) row[1])
                        .count((Long) row[2])
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get monthly income/expense trends.
     * Returns a list sorted by year and month descending.
     */
    public List<MonthlyTrend> getMonthlyTrends() {
        List<Object[]> results = recordRepository.getMonthlyTrends();

        // Group by year-month and combine income/expense
        Map<String, MonthlyTrend> trendMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            int year = (Integer) row[0];
            int month = (Integer) row[1];
            RecordType type = RecordType.valueOf((String) row[2]);
            BigDecimal amount = (BigDecimal) row[3];

            String key = year + "-" + month;
            MonthlyTrend trend = trendMap.computeIfAbsent(key, k ->
                    MonthlyTrend.builder()
                            .year(year)
                            .month(month)
                            .income(BigDecimal.ZERO)
                            .expense(BigDecimal.ZERO)
                            .net(BigDecimal.ZERO)
                            .build());

            if (type == RecordType.INCOME) {
                trend.setIncome(amount);
            } else {
                trend.setExpense(amount);
            }
            trend.setNet(trend.getIncome().subtract(trend.getExpense()));
        }

        return new ArrayList<>(trendMap.values());
    }

    /**
     * Get the most recent financial records.
     */
    public List<RecordResponse> getRecentActivity() {
        List<FinancialRecord> records = recordRepository.findTop10ByOrderByCreatedAtDesc();
        return records.stream()
                .map(record -> RecordResponse.builder()
                        .id(record.getId())
                        .amount(record.getAmount())
                        .type(record.getType())
                        .category(record.getCategory())
                        .date(record.getDate())
                        .description(record.getDescription())
                        .createdByUsername(record.getCreatedBy().getUsername())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
