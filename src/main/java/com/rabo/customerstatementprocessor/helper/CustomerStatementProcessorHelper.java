package com.rabo.customerstatementprocessor.helper;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.rabo.customerstatementprocessor.constants.StatementConstants;
import com.rabo.customerstatementprocessor.model.CustomerStatementRequest;

@Service
public class CustomerStatementProcessorHelper {

    private static final Logger LOG = LogManager.getLogger(CustomerStatementProcessorHelper.class);
    
    /**
     * This method to validate duplicate transaction references
     * 
     * @param customerStatements - List of Input Customer Statements
     * @return List<CustomerStatementRequest> - List of Customer Statements who are having duplicate transaction reference 
     */
    public List<CustomerStatementRequest> validateDuplicateTransactionReference(List<CustomerStatementRequest> customerStatements) {
        
        LOG.info(StatementConstants.DUPLICATE_REFERENCE_VALIDATION);
        
        List<CustomerStatementRequest> duplicate =
                customerStatements.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(m -> m.getValue() > 1)
                .map(Entry<CustomerStatementRequest, Long>::getKey)
                .collect(Collectors.toList());
        
        return customerStatements.stream().map(stmt -> {
            for (CustomerStatementRequest duplicateStmt : duplicate) {
                if (duplicateStmt.getTransactionReference().equals(stmt.getTransactionReference())) {
                    return stmt;
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

    }

    /**
     * This method to validate End Balance
     * 
     * @param customerStatements - List of Input Customer Statements
     * @return List<CustomerStatementRequest> - List of Customer Statements those are failed in end balance validation
     */
    public List<CustomerStatementRequest> validateEndBalance(List<CustomerStatementRequest> customerStatements) {
        LOG.info(StatementConstants.END_BALANCE_VALIDATION);
        return customerStatements.stream().filter(customerStatement -> !isValid(customerStatement)).collect(Collectors.toList());
    }

    /**
     * This method to check if endBalance is equal to the startBalance and mutation calculation
     * 
     * @param customerStatement - Input Customer Statement
     * @return boolean - returns true if there is endBalance is equal to the addition/subtraction of startBalance and mutation, otherwise false.
     */
    private boolean isValid(CustomerStatementRequest customerStatement) {
        return Math.round(customerStatement.getEndBalance() - customerStatement.getStartBalance()) == Math.round(customerStatement.getMutation());
    }
}
