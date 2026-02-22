package com.cmm.mit.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SummaryResponse(
    LocalDate from,
    LocalDate to,
    BigDecimal incomeTotal,
    BigDecimal expenseTotal,
    BigDecimal netTotal,
    List<AccountRollup> byAccount
) {}
