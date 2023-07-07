package com.example.ddcabe.OperatorStock;

import com.example.ddcabe.Stock.Stock;
import com.example.ddcabe.User.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing operator stock data.
 */
@Service
public class OperatorStockService {
    private final OperatorStockRepository operatorStockRepository;


    /**
     * Constructs an OperatorStockService with the specified OperatorStockRepository.
     *
     * @param operatorStockRepository the repository for operator stock data
     */
    public OperatorStockService(OperatorStockRepository operatorStockRepository) {
        this.operatorStockRepository = operatorStockRepository;
    }

    /**
     * Adds a single stock to the operator's stock scans.
     *
     * @param operator The optional operator performing the stock scan.
     * @param stock    The stock to be added.
     */
    public void addSingleStock(Optional<User> operator, Stock stock) {
        // Create a new OperatorStockScan entity with the given operator, stock, and its quantity.
        OperatorStock operatorStock = new OperatorStock(operator, stock, stock.getQuantity());

        // Save the OperatorStockScan entity to the repository.
        operatorStockRepository.save(operatorStock);
    }

    /**
     * Adds multiple stocks to the operator's stock scans.
     *
     * @param operator The optional operator performing the stock scan.
     * @param stocks   The list of stocks to be added.
     */
    public void addStocks(Optional<User> operator, List<Stock> stocks) {
        for (Stock stock : stocks) {
            // Check if the stock already exists in the operators_stocks table
            if (operatorStockRepository.existsByOperatorAndStock(operator, stock)) {
                continue; // Skip adding the stock if it already exists
            }
            // Create a new OperatorStock entity with the given operator, stock, and its quantity.
            OperatorStock operatorStock = new OperatorStock(operator, stock, stock.getQuantity());
            // Save the OperatorStock entity to the repository.
            operatorStockRepository.save(operatorStock);
        }
    }


}

