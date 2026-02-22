package com.cmm.mit.dto;

import java.math.BigDecimal;

public record AccountRollup(
    AccountRef account,
    BigDecimal income,
    BigDecimal expense,
    BigDecimal transfersOut,
    BigDecimal transfersIn
) {}
