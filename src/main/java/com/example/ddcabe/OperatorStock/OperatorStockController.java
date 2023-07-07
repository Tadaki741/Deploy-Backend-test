package com.example.ddcabe.OperatorStock;

import com.example.ddcabe.Config.UpdateStocksWebSocketHandler;
import com.example.ddcabe.HttpResponse.ResponseError;
import com.example.ddcabe.Socket.SocketIOController;
import com.example.ddcabe.Stock.Stock;
import com.example.ddcabe.User.User;
import com.example.ddcabe.User.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/assign")
@CrossOrigin(origins = "*")
public class OperatorStockController {
    private final OperatorStockService operatorStockService;
    private final UserService userService;
    //    private final Producer stockUpdateKafkaProducer;
    private final SocketIOController socketIOController;
    private final UpdateStocksWebSocketHandler updateStocksWebSocketHandler;


    public OperatorStockController(OperatorStockService operatorStockService, UserService userService, SocketIOController socketIOController, UpdateStocksWebSocketHandler updateStocksWebSocketHandler) {
        this.operatorStockService = operatorStockService;
        this.userService = userService;
//        this.stockUpdateKafkaProducer = stockUpdateKafkaProducer;
        this.socketIOController = socketIOController;
        this.updateStocksWebSocketHandler = updateStocksWebSocketHandler;
    }

    /**
     * Add the stock for a given operator.
     *
     * @param operatorUsername The username of the operator.
     * @param stocks           The list of stocks to be updated.
     * @return ResponseEntity representing the response status.
     */
    @PostMapping()
    @MessageMapping("/updateStocks")
    @SendTo("/topic/stocks")
    public ResponseEntity<?> addStocks(@RequestParam(name = "operatorName") String operatorUsername, @RequestBody List<Stock> stocks) {
        // Find the operator by username
        Optional<User> operatorOptional = userService.findByUsername(operatorUsername);
        if (operatorOptional.isPresent()) {
            updateStocksWebSocketHandler.sendUpdatedDataToClients(stocks);
            // Add stocks to the operator's stock scan and send stock updates through socket.io
            operatorStockService.addStocks(operatorOptional, stocks);
            for (Stock stock : stocks) {
                socketIOController.sendStockUpdate(stock);
            }
            return ResponseEntity.ok(stocks);
        } else {
            // Return error response if operator is not found
            ResponseError errorResponse = new ResponseError("Operator not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
