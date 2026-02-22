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

  /**
   * Search transactions within a date range with optional type/category filters.
   *
   * <p>NFR note: this method is used when accountId filter is not provided, to keep the query index-friendly
   * at scale.
   */
  @Query("""
      select t from Txn t
      where t.txnDate >= :from and t.txnDate < :to
        and (:type is null or t.type = :type)
        and (:categoryId is null or t.category.id = :categoryId)
      """)
  Page<Txn> searchNoAccount(
      @Param("from") Instant from,
      @Param("to") Instant to,
      @Param("type") TransactionType type,
      @Param("categoryId") UUID categoryId,
      Pageable pageable);

  /**
   * Account-centric search.
   *
   * <p>NFR note: avoids an OR across three account columns (account_id/from_account_id/to_account_id), which
   * can degrade index usage as the transactions table grows.
   */
  @Query(
      value = """
          select * from (
            select t.* from transactions t
            where t.txn_date >= :from and t.txn_date < :to
              and (:type is null or t.type = :type)
              and (:categoryId is null or t.category_id = :categoryId)
              and t.account_id = :accountId
            union
            select t.* from transactions t
            where t.txn_date >= :from and t.txn_date < :to
              and (:type is null or t.type = :type)
              and (:categoryId is null or t.category_id = :categoryId)
              and t.from_account_id = :accountId
            union
            select t.* from transactions t
            where t.txn_date >= :from and t.txn_date < :to
              and (:type is null or t.type = :type)
              and (:categoryId is null or t.category_id = :categoryId)
              and t.to_account_id = :accountId
          ) u
          """,
      countQuery = """
          select count(*) from (
            select t.id from transactions t
            where t.txn_date >= :from and t.txn_date < :to
              and (:type is null or t.type = :type)
              and (:categoryId is null or t.category_id = :categoryId)
              and t.account_id = :accountId
            union
            select t.id from transactions t
            where t.txn_date >= :from and t.txn_date < :to
              and (:type is null or t.type = :type)
              and (:categoryId is null or t.category_id = :categoryId)
              and t.from_account_id = :accountId
            union
            select t.id from transactions t
            where t.txn_date >= :from and t.txn_date < :to
              and (:type is null or t.type = :type)
              and (:categoryId is null or t.category_id = :categoryId)
              and t.to_account_id = :accountId
          ) u
          """,
      nativeQuery = true)
  Page<Txn> searchByAccount(
      @Param("from") Instant from,
      @Param("to") Instant to,
      @Param("type") String type,
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
