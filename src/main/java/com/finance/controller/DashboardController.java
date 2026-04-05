package com.finance.controller;

import com.finance.dto.response.*;
import com.finance.model.RecordType;
import com.finance.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Dashboard controller providing aggregated financial data.
 * Accessible to all authenticated users (VIEWER, ANALYST, ADMIN).
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get overall financial summary (total income, expenses, net balance).
     * GET /api/dashboard/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummary>> getSummary() {
        DashboardSummary summary = dashboardService.getSummary();
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved", summary));
    }

    /**
     * Get category-wise summary.
     * GET /api/dashboard/category-summary?type=INCOME
     */
    @GetMapping("/category-summary")
    public ResponseEntity<ApiResponse<List<CategorySummary>>> getCategorySummary(
            @RequestParam(required = false) RecordType type) {
        List<CategorySummary> summaries = dashboardService.getCategorySummary(type);
        return ResponseEntity.ok(ApiResponse.success("Category summary retrieved", summaries));
    }

    /**
     * Get monthly income and expense trends.
     * GET /api/dashboard/monthly-trends
     */
    @GetMapping("/monthly-trends")
    public ResponseEntity<ApiResponse<List<MonthlyTrend>>> getMonthlyTrends() {
        List<MonthlyTrend> trends = dashboardService.getMonthlyTrends();
        return ResponseEntity.ok(ApiResponse.success("Monthly trends retrieved", trends));
    }

    /**
     * Get recent financial activity (last 10 records).
     * GET /api/dashboard/recent-activity
     */
    @GetMapping("/recent-activity")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getRecentActivity() {
        List<RecordResponse> recentActivity = dashboardService.getRecentActivity();
        return ResponseEntity.ok(ApiResponse.success("Recent activity retrieved", recentActivity));
    }
}
