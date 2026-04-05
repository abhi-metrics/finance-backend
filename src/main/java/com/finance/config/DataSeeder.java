package com.finance.config;

import com.finance.model.*;
import com.finance.repository.FinancialRecordRepository;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds the database with a default admin user and sample financial records
 * on first run (only if the admin user does not already exist).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FinancialRecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername("admin")) {
            log.info("Data already seeded. Skipping...");
            return;
        }

        log.info("Seeding database with default users and sample records...");

        // ---- Create default users ----

        User admin = User.builder()
                .username("admin")
                .email("admin@finance.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(admin);

        User analyst = User.builder()
                .username("analyst")
                .email("analyst@finance.com")
                .password(passwordEncoder.encode("analyst123"))
                .role(Role.ANALYST)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(analyst);

        User viewer = User.builder()
                .username("viewer")
                .email("viewer@finance.com")
                .password(passwordEncoder.encode("viewer123"))
                .role(Role.VIEWER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(viewer);

        // ---- Create sample financial records ----

        recordRepository.save(FinancialRecord.builder()
                .amount(new BigDecimal("75000.00"))
                .type(RecordType.INCOME)
                .category("Salary")
                .date(LocalDate.of(2026, 4, 1))
                .description("Monthly salary for April 2026")
                .createdBy(admin)
                .build());

        recordRepository.save(FinancialRecord.builder()
                .amount(new BigDecimal("15000.00"))
                .type(RecordType.INCOME)
                .category("Freelance")
                .date(LocalDate.of(2026, 4, 5))
                .description("Freelance web development project")
                .createdBy(admin)
                .build());

        recordRepository.save(FinancialRecord.builder()
                .amount(new BigDecimal("20000.00"))
                .type(RecordType.EXPENSE)
                .category("Rent")
                .date(LocalDate.of(2026, 4, 1))
                .description("Monthly apartment rent")
                .createdBy(admin)
                .build());

        recordRepository.save(FinancialRecord.builder()
                .amount(new BigDecimal("5000.00"))
                .type(RecordType.EXPENSE)
                .category("Utilities")
                .date(LocalDate.of(2026, 4, 3))
                .description("Electricity and water bill")
                .createdBy(admin)
                .build());

        recordRepository.save(FinancialRecord.builder()
                .amount(new BigDecimal("8000.00"))
                .type(RecordType.EXPENSE)
                .category("Groceries")
                .date(LocalDate.of(2026, 4, 4))
                .description("Monthly grocery shopping")
                .createdBy(admin)
                .build());

        recordRepository.save(FinancialRecord.builder()
                .amount(new BigDecimal("3000.00"))
                .type(RecordType.EXPENSE)
                .category("Transportation")
                .date(LocalDate.of(2026, 4, 2))
                .description("Fuel and cab rides")
                .createdBy(admin)
                .build());

        recordRepository.save(FinancialRecord.builder()
                .amount(new BigDecimal("50000.00"))
                .type(RecordType.INCOME)
                .category("Salary")
                .date(LocalDate.of(2026, 3, 1))
                .description("Monthly salary for March 2026")
                .createdBy(admin)
                .build());

        recordRepository.save(FinancialRecord.builder()
                .amount(new BigDecimal("12000.00"))
                .type(RecordType.EXPENSE)
                .category("Healthcare")
                .date(LocalDate.of(2026, 3, 15))
                .description("Annual health checkup and medicines")
                .createdBy(admin)
                .build());

        log.info("Database seeded successfully! Default users: admin/admin123, analyst/analyst123, viewer/viewer123");
    }
}
