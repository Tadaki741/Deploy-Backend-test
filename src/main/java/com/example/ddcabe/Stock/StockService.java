package com.example.ddcabe.Stock;

import com.example.ddcabe.Session.Session;
import com.example.ddcabe.User.User;
import com.example.ddcabe.User.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository, UserService userService) {
        this.stockRepository = stockRepository;
    }

    /**
     * Adds a list of stocks to the stock repository.
     *
     * @param stocks The list of stocks to be added.
     */
    public void addStocks(List<Stock> stocks) {
        stockRepository.saveAll(stocks);
    }

    /**
     * Updates a list of stocks in the stock repository.
     *
     * @param stocks The list of stocks to be updated.
     */
    public void assignStocksToOperator(User operator, Session session, List<Stock> stocks) {
        List<Stock> existingStocks = getExistingStocks(stocks, session);
        System.out.println("Existing stocks: " + existingStocks.size());
        for (Stock stock : existingStocks) {
            System.out.println("Stock ID: " + stock.getId());
            if (stock.getUser() != null) {
                System.out.println("Stock user: " + stock.getUser().getUsername());
                stock.setUser(null);
            }
        }
        List<UUID> existingStockIds = existingStocks.stream().map(Stock::getId).collect(Collectors.toList());
        stockRepository.updateStocksOperator(operator, existingStockIds);
    }

    /**
     * Retrieves a list of stocks associated with the specified operator ID and session ID.
     *
     * @param stocks The list of stocks to be updated.
     */
    private List<Stock> getExistingStocks(List<Stock> stocks, Session session) {
        List<String> itemIds = stocks.stream().map(Stock::getItemId).collect(Collectors.toList());
        List<String> locations = stocks.stream().map(Stock::getLocation).collect(Collectors.toList());
        List<Integer> quantities = stocks.stream().map(Stock::getQuantity).collect(Collectors.toList());

        return stockRepository.findByItemIdInAndSessionIdAndLocationInAndQuantityIn(
                itemIds,
                session.getId(),
                locations,
                quantities);
    }


    /**
     * Finds a stock by its ID in the given list of stocks.
     *
     * @param stocks  The list of stocks to search in.
     * @param stockId The ID of the stock to find.
     * @return The found Stock object or null if not found.
     */
    private Stock findStockById(List<Stock> stocks, String stockId) {
        for (Stock stock : stocks) {
            if (stock.getItemId().equals(stockId)) {
                return stock;
            }
        }
        return null;
    }

    /**
     * Retrieves a list of stocks associated with the specified operator ID.
     *
     * @param sessionId  The ID of the session.
     * @param operatorId The ID of the operator.
     * @return A list of stocks associated with the specified operator ID and session ID.
     */
    public List<Stock> getAssignedStocksBySessionIdAndOperatorId(UUID sessionId, UUID operatorId) {
        return stockRepository.findAssignedStocksBySessionIdAndOperatorId(sessionId, operatorId);
    }

    /**
     * Retrieves stocks associated with a specific session ID from the stock repository.
     *
     * @param sessionId the ID of the session
     * @return a list of Stock objects
     */
    public List<Stock> getStocksForSession(UUID sessionId) {
        return stockRepository.findBySessionId(sessionId);
    }

    /**
     * Deletes a stock with the specified item ID.
     *
     * @param itemId The ID of the stock item to be deleted.
     * @return true if the stock was deleted, false if it does not exist.
     */
    public boolean deleteStockByItemId(String itemId) {
        Stock stock = stockRepository.findByItemId(itemId);
        if (stock != null) {
            stockRepository.delete(stock);
            return true;
        }
        return false;
    }

    /**
     * Deletes all stocks associated with a specific session ID from the stock repository.
     *
     * @param sessionId the ID of the session
     */
    public int deleteStocksBySessionId(UUID sessionId) {
        return stockRepository.deleteBySessionId(sessionId);
    }
}


//    public void updateStocks(UUID operatorId, List<Stock> updatedStocks) {
//        List<Stock> existingStocks = stockRepository.findByUserId(operatorId);
//
//        for (Stock updatedStock : updatedStocks) {
//            Stock existingStock = findStockById(existingStocks, updatedStock.getItemId());
//            if (existingStock != null) {
//                existingStock.setQuantity(updatedStock.getQuantity());
//            }
//        }
//
//        stockRepository.saveAll(existingStocks);
//    }
//    public void updateSingleStock(Stock updatedStock) {
//        Optional<Stock> existingStockOpt = stockRepository.findById(updatedStock.getItemId());
//        if (existingStockOpt.isPresent()) {
//            Stock existingStock = existingStockOpt.get();
//            existingStock.setQuantity(updatedStock.getQuantity());
//            stockRepository.save(existingStock);
//        }
//    }
