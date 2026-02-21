package com.cmm.mit.repo;

import com.cmm.mit.domain.entity.Txn;
import com.cmm.mit.domain.enums.TransactionType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TxnRepo extends JpaRepository<Txn, UUID> {

  @Query("""
      select t from Txn t
      where t.txnDate >= :from and t.txnDate < :to
        and (:type is null or t.type = :type)
        and (
          :accountId is null
          or t.account.id = :accountId
          or t.fromAccount.id = :accountId
          or t.toAccount.id = :accountId
        )
        and (:categoryId is null or t.category.id = :categoryId)
      """)
  Page<Txn> search(
      @Param("from") Instant from,
      @Param("to") Instant to,
      @Param("type") TransactionType type,
      @Param("accountId") UUID accountId,
      @Param("categoryId") UUID categoryId,
      Pageable pageable);

  @Query("""
      select t from Txn t
      where t.type = com.cmm.mit.domain.enums.TransactionType.EXPENSE
      order by t.txnDate desc
      """)
  List<Txn> findRecentExpenses(Pageable pageable);

  @Query("""
      select coalesce(sum(t.amount), 0)
      from Txn t
      where t.txnDate >= :from and t.txnDate < :to
        and t.type = :type
      """)
  java.math.BigDecimal sumByType(@Param("from") Instant from, @Param("to") Instant to, @Param("type") TransactionType type);

  @Query("""
      select t.category.id, coalesce(sum(t.amount), 0)
      from Txn t
      where t.txnDate >= :from and t.txnDate < :to
        and t.type = com.cmm.mit.domain.enums.TransactionType.EXPENSE
        and t.category is not null
      group by t.category.id
      """)
  List<Object[]> sumExpenseByCategory(@Param("from") Instant from, @Param("to") Instant to);

  @Query("""
      select cast(t.txnDate as date), coalesce(sum(t.amount), 0)
      from Txn t
      where t.txnDate >= :from and t.txnDate < :to
        and t.type = com.cmm.mit.domain.enums.TransactionType.EXPENSE
      group by cast(t.txnDate as date)
      order by cast(t.txnDate as date)
      """)
  List<Object[]> dailyExpenseTrend(@Param("from") Instant from, @Param("to") Instant to);
}
