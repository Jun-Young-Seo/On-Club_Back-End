package com.springboot.club_house_api_server.budget.repository;

import com.springboot.club_house_api_server.budget.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    // 1. 특정 계좌의 거래 내역 조회 (accountId로)
    @Query("SELECT t FROM TransactionEntity t WHERE t.account.accountId = :accountId")
    List<TransactionEntity> findByAccountId(@Param("accountId") Long accountId);

    // 2. 특정 클럽의 거래 내역 조회 (clubId로)
    @Query("SELECT t FROM TransactionEntity t WHERE t.club.clubId = :clubId")
    List<TransactionEntity> findByClubId(@Param("clubId") Long clubId);

    // 3. 특정 날짜에 발생한 거래 내역 조회
    @Query("SELECT t FROM TransactionEntity t WHERE t.transactionDate = :transactionDate")
    List<TransactionEntity> findByTransactionDate(@Param("transactionDate") LocalDate transactionDate);

    // 4. 특정 기간 동안 발생한 거래 내역 조회
    @Query("SELECT t FROM TransactionEntity t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    List<TransactionEntity> findByTransactionDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 5. 거래 유형(입금/출금)으로 거래 내역 조회
    @Query("SELECT t FROM TransactionEntity t WHERE t.transactionCategory = :category")
    List<TransactionEntity> findByTransactionCategory(@Param("category") String category);

    // 6. 특정 거래자(transactionWho)의 거래 내역 조회
    @Query("SELECT t FROM TransactionEntity t WHERE t.transactionWho = :who")
    List<TransactionEntity> findByTransactionWho(@Param("who") String who);

    // 7. 특정 클럽에서 특정 거래자가 남긴 거래 내역 조회
    @Query("SELECT t FROM TransactionEntity t WHERE t.club.clubId = :clubId AND t.transactionWho = :who")
    List<TransactionEntity> findByClubIdAndTransactionWho(@Param("clubId") Long clubId, @Param("who") String who);

    // 8. 특정 클럽에서 특정 기간 동안 발생한 거래 내역 조회
    @Query("SELECT t FROM TransactionEntity t WHERE t.club.clubId = :clubId AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<TransactionEntity> findByClubIdAndDateRange(@Param("clubId") Long clubId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 9. 클럽 이름으로 거래 내역 조회 (JOIN 활용)
    @Query("SELECT t FROM TransactionEntity t WHERE t.club.clubName = :clubName")
    List<TransactionEntity> findByClubName(@Param("clubName") String clubName);

    // 10. 거래 설명(transactionDescription)에 특정 키워드가 포함된 거래 내역 조회
    @Query("SELECT t FROM TransactionEntity t WHERE t.transactionDescription LIKE %:keyword%")
    List<TransactionEntity> findByDescriptionContaining(@Param("keyword") String keyword);

    // 11. 특정 계좌의 거래 내역 중 특정 키워드가 포함된 거래 내역 조회
    @Query("SELECT t FROM TransactionEntity t WHERE t.account.accountId = :accountId AND t.transactionDescription LIKE %:keyword%")
    List<TransactionEntity> findByAccountIdAndDescriptionContaining(@Param("accountId") Long accountId, @Param("keyword") String keyword);

    // 12. 클럽 이름과 거래 유형(입금/출금)으로 거래 내역 조회
    @Query("SELECT t FROM TransactionEntity t WHERE t.club.clubName = :clubName AND t.transactionCategory = :category")
    List<TransactionEntity> findByClubNameAndCategory(@Param("clubName") String clubName, @Param("category") String category);
}
