package com.cmm.mit.config;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.domain.enums.AccountType;
import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.repo.AccountRepo;
import com.cmm.mit.repo.CategoryRepo;
import com.cmm.mit.service.SettingsService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Data bootstrap for development environments.
 *
 * <p>Seeds a minimal set of default accounts and categories when the database is empty.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataBootstrap {

  private final SettingsService settingsService;

  /**
   * Seed defaults if required.
   */
  @Bean
  CommandLineRunner bootstrap(AccountRepo accountRepo, CategoryRepo categoryRepo) {
    return args -> {
      var settings = settingsService.getOrCreate();

      // Only seed on empty DB to avoid overwriting user data.
      if (accountRepo.count() == 0) {
        log.info("Bootstrapping default accounts");
        accountRepo.save(Account.builder()
            .name("Bank")
            .type(AccountType.BANK)
            .currency(settings.getBaseCurrency())
            .openingBalance(BigDecimal.ZERO)
            .active(true)
            .build());

        accountRepo.save(Account.builder()
            .name("Wallet")
            .type(AccountType.CASH)
            .currency(settings.getBaseCurrency())
            .openingBalance(BigDecimal.ZERO)
            .active(true)
            .build());
      }

      // Only seed on empty DB to avoid overwriting user data.
      if (categoryRepo.count() == 0) {
        log.info("Bootstrapping default categories");
        // Income
        categoryRepo.save(Category.builder().name("Salary").type(CategoryType.INCOME).color("#10B981").icon("paid").active(true).build());

        // Expenses (starter set)
        categoryRepo.save(Category.builder().name("Loan").type(CategoryType.EXPENSE).color("#EF4444").icon("payments").active(true).build());
        categoryRepo.save(Category.builder().name("Credit Card").type(CategoryType.EXPENSE).color("#F97316").icon("credit_card").active(true).build());
        categoryRepo.save(Category.builder().name("Electricity Bill").type(CategoryType.EXPENSE).color("#22C55E").icon("bolt").active(true).build());
        categoryRepo.save(Category.builder().name("Groceries").type(CategoryType.EXPENSE).color("#7C3AED").icon("shopping_cart").active(true).build());
        categoryRepo.save(Category.builder().name("Snacks").type(CategoryType.EXPENSE).color("#A855F7").icon("restaurant").active(true).build());
        categoryRepo.save(Category.builder().name("Kids").type(CategoryType.EXPENSE).color("#06B6D4").icon("child_care").active(true).build());
        categoryRepo.save(Category.builder().name("Trips").type(CategoryType.EXPENSE).color("#3B82F6").icon("flight").active(true).build());
        categoryRepo.save(Category.builder().name("Car Repair").type(CategoryType.EXPENSE).color("#64748B").icon("build").active(true).build());
        categoryRepo.save(Category.builder().name("Fuel").type(CategoryType.EXPENSE).color("#F59E0B").icon("local_gas_station").active(true).build());
        categoryRepo.save(Category.builder().name("Medical").type(CategoryType.EXPENSE).color("#14B8A6").icon("local_hospital").active(true).build());
        categoryRepo.save(Category.builder().name("Wife").type(CategoryType.EXPENSE).color("#EC4899").icon("favorite").active(true).build());
        categoryRepo.save(Category.builder().name("Other").type(CategoryType.EXPENSE).color("#94A3B8").icon("category").active(true).build());
      }
    };
  }
}
