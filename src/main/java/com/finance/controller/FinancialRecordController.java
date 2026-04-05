package com.finance.controller;

import com.finance.dto.request.RecordRequest;
import com.finance.dto.response.ApiResponse;
import com.finance.dto.response.RecordResponse;
import com.finance.model.RecordType;
import com.finance.model.User;
import com.finance.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Financial records controller.
 * - Create, Update, Delete: ADMIN only
 * - Read / Filter: ADMIN and ANALYST
 */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    /**
     * Create a new financial record.
     * POST /api/records
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RecordResponse>> createRecord(
            @Valid @RequestBody RecordRequest request,
            @AuthenticationPrincipal User currentUser) {
        RecordResponse record = recordService.createRecord(request, currentUser);
        return new ResponseEntity<>(
                ApiResponse.success("Record created successfully", record),
                HttpStatus.CREATED);
    }

    /**
     * Get all records with optional filtering.
     * GET /api/records?type=INCOME&category=Salary&startDate=2026-01-01&endDate=2026-12-31&page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<Page<RecordResponse>>> getRecords(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RecordResponse> records = recordService.getRecords(type, category, startDate, endDate, page, size);
        return ResponseEntity.ok(ApiResponse.success("Records retrieved successfully", records));
    }

    /**
     * Get a single record by ID.
     * GET /api/records/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<RecordResponse>> getRecordById(@PathVariable Long id) {
        RecordResponse record = recordService.getRecordById(id);
        return ResponseEntity.ok(ApiResponse.success("Record retrieved successfully", record));
    }

    /**
     * Update a record.
     * PUT /api/records/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RecordResponse>> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody RecordRequest request) {
        RecordResponse record = recordService.updateRecord(id, request);
        return ResponseEntity.ok(ApiResponse.success("Record updated successfully", record));
    }

    /**
     * Delete a record.
     * DELETE /api/records/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return ResponseEntity.ok(ApiResponse.success("Record deleted successfully", null));
    }
}
