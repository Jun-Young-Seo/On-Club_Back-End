package com.springboot.club_house_api_server.budget.repository;

import com.springboot.club_house_api_server.budget.dto.BudgetResponseDto;
import com.springboot.club_house_api_server.budget.entity.TransactionEntity;
import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    // 1. 특정 계좌의 거래 내역 조회 (accountId로)
    @Query("SELECT t FROM TransactionEntity t WHERE t.account.accountId = :accountId ORDER BY t.transactionDate DESC")
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
    @Query("SELECT t FROM TransactionEntity t WHERE t.transactionDescription = :tel")
    List<TransactionEntity> findByTransactionTel(@Param("tel") String tel);

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

    //이미 DB에 거래내역이 있는 경우 추가하지 않기 위한 bool 메서드
    @Query("SELECT COUNT(t) > 0 FROM TransactionEntity t " +
            "WHERE t.account = :account " +
            "AND t.club = :club " +
            "AND DATE_FORMAT(t.transactionDate, '%Y-%m-%d %H:%i:%s') = DATE_FORMAT(:transactionDate, '%Y-%m-%d %H:%i:%s')")
    boolean isAlreadySavedTransaction(@Param("account") ClubAccountEntity account,
                                      @Param("club") ClubEntity club,
                                      @Param("transactionDate") LocalDateTime transactionDate);

    @Query("SELECT COUNT(t) FROM TransactionEntity t " +
            "WHERE t.account = :account " +
            "AND t.club = :club " +
            "AND DATE_FORMAT(t.transactionDate, '%Y-%m-%d %H:%i:%s') = DATE_FORMAT(:transactionDate, '%Y-%m-%d %H:%i:%s')")
    long countExistingTransactions(@Param("account") ClubAccountEntity account,
                                   @Param("club") ClubEntity club,
                                   @Param("transactionDate") LocalDateTime transactionDate);


    //Transaction DB Update
    @Transactional
    @Modifying
    @Query("UPDATE TransactionEntity t " +
            "SET t.transactionDate = :transactionDate, " +
            "    t.transactionType = :transactionType, " +
            "    t.transactionAmount = :transactionAmount, " +
            "    t.transactionBalance = :transactionBalance, " +
            "    t.transactionCategory = :transactionCategory, " +
            "    t.transactionDescription = :transactionDescription, " +
            "    t.transactionMemo = :transactionMemo, " +
            "    t.transactionDetail = :transactionDetail " +
            "WHERE t.transactionId = :transactionId")
    int updateTransaction(
            @Param("transactionId") Long transactionId,
            @Param("transactionDate") LocalDateTime transactionDate,
            @Param("transactionType") String transactionType,
            @Param("transactionAmount") int transactionAmount,
            @Param("transactionBalance") int transactionBalance,
            @Param("transactionCategory") String transactionCategory,
            @Param("transactionDescription") String transactionDescription,
            @Param("transactionMemo") String transactionMemo,
            @Param("transactionDetail") String transactionDetail
    );

    TransactionEntity getTransactionEntityByTransactionId(long transactionId);



    //=========================For DashBoard==============================
    //대시보등에서의 수입
    @Query("SELECT COALESCE(SUM(t.transactionAmount), 0) " +
            "FROM TransactionEntity t " +
            "WHERE t.transactionType = '입금' " +
            "AND t.account.accountId = :accountId " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    long getMonthlyIncomeByAccountId(@Param("accountId") Long accountId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    //대시보드에서의 지출
    @Query("SELECT COALESCE(SUM(t.transactionAmount), 0) " +
            "FROM TransactionEntity t " +
            "WHERE t.transactionType = '출금' " +
            "AND t.account.accountId = :accountId " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    long getMonthlyExpenseByAccountId(@Param("accountId") Long accountId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    //대시보드에서 지출
//    @Query(value = "SELECT t.transactionBalance " +
//            "FROM TransactionEntity t " +
//            "WHERE t.account.accountId = :accountId " +
//            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
//            "ORDER BY t.transactionDate DESC LIMIT 1", nativeQuery = true)
//    Long getLatestMonthlyBalanceByAccountId(@Param("accountId") Long accountId,
//                                            @Param("startDate") LocalDateTime startDate,
//                                            @Param("endDate") LocalDateTime endDate);
    Optional<TransactionEntity> findTopByAccount_AccountIdOrderByTransactionDateDesc(Long accountId);

    @Query("SELECT t FROM TransactionEntity t " +
            "WHERE t.account.accountId = :accountId " +
            "ORDER BY t.transactionDate DESC LIMIT 3")
    List<TransactionEntity> getLastThreeTransactions(@Param("accountId") Long accountId);


}
