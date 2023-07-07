package com.example.ddcabe.Stock;

import com.example.ddcabe.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface StockRepository extends JpaRepository<Stock, UUID> {
    void deleteByCreatedAtBefore(LocalDateTime createdAt);

    // Retrieves a Stock object based on the itemId
    Stock findByItemId(String itemId);

    // Retrieves a list of Stock objects based on the sessionId
    List<Stock> findBySessionId(UUID sessionId);

    // Retrieves a list of Stock objects by joining with the User entity and filtering by operatorId
    @Query("SELECT s FROM Stock s JOIN s.user o WHERE o.id = ?1")
    List<Stock> findStocksByOperator(UUID operatorId);

    // Retrieves a list of Stock objects filtering by operatorId and sessionId
    @Query("SELECT stk FROM Stock stk WHERE stk.session.id = :sessionId AND stk.user.id = :operatorId")
    List<Stock> findAssignedStocksBySessionIdAndOperatorId(@Param("sessionId") UUID sessionId, @Param("operatorId") UUID operatorId);

    // Retrieves a list of Stock objects filtering by userId
    @Modifying
    @Query("UPDATE Stock s SET s.user = :operator WHERE s.id IN :stockIds")
    void updateStocksOperator(@Param("operator") User operator, @Param("stockIds") List<UUID> stockIds);

    // Retrieves a list of Stock objects based on the itemIds and sessionId
    List<Stock> findByItemIdInAndSessionIdAndLocationInAndQuantityIn(List<String> itemIds, UUID sessionId, List<String> locations, List<Integer> quantities);

    //Delete all stocks with the given session id
    int deleteBySessionId(UUID sessionId);

}

