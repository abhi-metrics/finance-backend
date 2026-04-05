package com.finance.service;

import com.finance.dto.request.RecordRequest;
import com.finance.dto.response.RecordResponse;
import com.finance.exception.ResourceNotFoundException;
import com.finance.model.FinancialRecord;
import com.finance.model.RecordType;
import com.finance.model.User;
import com.finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service for financial record CRUD operations with filtering and pagination.
 */
@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;

    /**
     * Create a new financial record.
     */
    @Transactional
    public RecordResponse createRecord(RecordRequest request, User currentUser) {
        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .description(request.getDescription())
                .createdBy(currentUser)
                .build();

        recordRepository.save(record);
        return mapToResponse(record);
    }

    /**
     * Get all records with optional filtering by type, category, and date range.
     * Supports pagination and sorting.
     */
    public Page<RecordResponse> getRecords(RecordType type, String category,
                                           LocalDate startDate, LocalDate endDate,
                                           int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));

        Page<FinancialRecord> records;

        boolean hasType = type != null;
        boolean hasCategory = category != null && !category.isBlank();
        boolean hasDateRange = startDate != null && endDate != null;

        if (hasType && hasCategory && hasDateRange) {
            records = recordRepository.findByTypeAndCategoryAndDateBetween(
                    type, category, startDate, endDate, pageable);
        } else if (hasType && hasCategory) {
            records = recordRepository.findByTypeAndCategory(type, category, pageable);
        } else if (hasType && hasDateRange) {
            records = recordRepository.findByTypeAndDateBetween(type, startDate, endDate, pageable);
        } else if (hasCategory && hasDateRange) {
            records = recordRepository.findByCategoryAndDateBetween(category, startDate, endDate, pageable);
        } else if (hasType) {
            records = recordRepository.findByType(type, pageable);
        } else if (hasCategory) {
            records = recordRepository.findByCategory(category, pageable);
        } else if (hasDateRange) {
            records = recordRepository.findByDateBetween(startDate, endDate, pageable);
        } else {
            records = recordRepository.findAll(pageable);
        }

        return records.map(this::mapToResponse);
    }

    /**
     * Get a single record by ID.
     */
    public RecordResponse getRecordById(Long id) {
        FinancialRecord record = findRecordById(id);
        return mapToResponse(record);
    }

    /**
     * Update an existing record.
     */
    @Transactional
    public RecordResponse updateRecord(Long id, RecordRequest request) {
        FinancialRecord record = findRecordById(id);

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setDescription(request.getDescription());

        recordRepository.save(record);
        return mapToResponse(record);
    }

    /**
     * Delete a record by ID.
     */
    @Transactional
    public void deleteRecord(Long id) {
        FinancialRecord record = findRecordById(id);
        recordRepository.delete(record);
    }

    // ---- Helper methods ----

    private FinancialRecord findRecordById(Long id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial Record", "id", id));
    }

    private RecordResponse mapToResponse(FinancialRecord record) {
        return RecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .date(record.getDate())
                .description(record.getDescription())
                .createdByUsername(record.getCreatedBy().getUsername())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
