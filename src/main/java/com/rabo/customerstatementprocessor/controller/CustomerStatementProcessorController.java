package com.rabo.customerstatementprocessor.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.rabo.customerstatementprocessor.api.ICustomersStatementProcessor;
import com.rabo.customerstatementprocessor.constants.StatementConstants;
import com.rabo.customerstatementprocessor.helper.CustomerStatementProcessorHelper;
import com.rabo.customerstatementprocessor.model.CustomerStatementRequest;
import com.rabo.customerstatementprocessor.model.CustomerStatementResponse;
import com.rabo.customerstatementprocessor.model.ErrorRecord;
import com.rabo.customerstatementprocessor.util.StatementResponseUtil;

@RestController
public class CustomerStatementProcessorController implements ICustomersStatementProcessor {

    private static final Logger LOG = LogManager.getLogger(CustomerStatementProcessorController.class);

    @Autowired
    private CustomerStatementProcessorHelper statementProcessorHelper;

    @Override
    public ResponseEntity<CustomerStatementResponse> validateCustomerStatements(List<CustomerStatementRequest> customerStatements) {

        List<CustomerStatementRequest> failureEndBalanceRecords;
        CustomerStatementResponse customerStatementResponse = new CustomerStatementResponse();
        try {
            failureEndBalanceRecords = new ArrayList<>();
            List<CustomerStatementRequest> failureDuplicateRecords;
            if (!customerStatements.isEmpty()) {

                failureEndBalanceRecords = statementProcessorHelper.validateEndBalance(customerStatements);
                LOG.info(StatementConstants.END_BALANCE_FAILURE);
                failureDuplicateRecords = statementProcessorHelper.validateDuplicateTransactionReference(customerStatements);
                LOG.info(StatementConstants.DUPLICATE_REFERENCE_FAILURE);

                if (failureEndBalanceRecords.isEmpty() && !failureDuplicateRecords.isEmpty()) {
                    customerStatementResponse.setResult(StatementConstants.DUPLICATE_REFERENCE);
                } else if (!failureEndBalanceRecords.isEmpty() && failureDuplicateRecords.isEmpty()) {
                    customerStatementResponse.setResult(StatementConstants.INCORRECT_END_BALANCE);
                } else if (!failureEndBalanceRecords.isEmpty() && !failureDuplicateRecords.isEmpty()) {
                    customerStatementResponse.setResult(StatementConstants.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE);
                } else {
                    customerStatementResponse.setResult(StatementConstants.SUCCESSFUL);
                }
            } else {
                LOG.info(StatementConstants.JSON_PARSE_FAILURE);
                customerStatementResponse.setResult(StatementConstants.BAD_REQUEST);
                return StatementResponseUtil
                        .prepareEntityForStatusBadRequest(createStatementValidationResponse(customerStatementResponse, failureEndBalanceRecords));
            }
            failureEndBalanceRecords.addAll(failureDuplicateRecords);
        } catch (NullPointerException ex) {
            LOG.info(StatementConstants.JSON_PARSE_FAILURE);
            customerStatementResponse.setResult(StatementConstants.BAD_REQUEST);
            customerStatementResponse.setErrorRecords(new ArrayList<ErrorRecord>());
            return StatementResponseUtil.prepareEntityForStatusBadRequest(customerStatementResponse);
        } catch (RuntimeException ex) {
            LOG.info(StatementConstants.INTERNAL_SERVER_FAILURE);
            customerStatementResponse.setResult(StatementConstants.INTERNAL_SERVER_ERROR);
            customerStatementResponse.setErrorRecords(new ArrayList<ErrorRecord>());
            return StatementResponseUtil.prepareEntityForInternalServerError(customerStatementResponse);
        } 
        catch (Exception ex) {
            LOG.info(StatementConstants.JSON_PARSE_FAILURE);
            customerStatementResponse.setResult(StatementConstants.BAD_REQUEST);
            customerStatementResponse.setErrorRecords(new ArrayList<ErrorRecord>());
            return StatementResponseUtil.prepareEntityForStatusBadRequest(customerStatementResponse);
        }
        return StatementResponseUtil.prepareEntityForStatusOk(createStatementValidationResponse(customerStatementResponse, failureEndBalanceRecords));
    }

    @ExceptionHandler({JsonParseException.class})
    protected ResponseEntity<CustomerStatementResponse> handleJsonParseException(JsonParseException ex) {

        LOG.info(StatementConstants.JSON_PARSE_FAILURE);
        CustomerStatementResponse customerStatementResponse = new CustomerStatementResponse();
        customerStatementResponse.setResult(StatementConstants.BAD_REQUEST);
        customerStatementResponse.setErrorRecords(new ArrayList<>());
        return StatementResponseUtil.prepareEntityForStatusBadRequest(customerStatementResponse);
    }

    /**
     * This method is to creat Statement Validation Response
     * 
     * @param customerStatementResponse - Customer Statement Response
     * @param failedCustomerStatements - List of failed customer statements
     * @return CustomerStatementResponse - Final customer statment validation response
     */
    private CustomerStatementResponse createStatementValidationResponse(CustomerStatementResponse customerStatementResponse,
            List<CustomerStatementRequest> failedCustomerStatements) {
        LOG.info(StatementConstants.CREATE_STATEMENT_RESPONSE);
        List<ErrorRecord> errorRecords = new ArrayList<>();
        for (CustomerStatementRequest failedCustomerStatement : failedCustomerStatements) {

            ErrorRecord errorRecord = new ErrorRecord();
            errorRecord.setReference(failedCustomerStatement.getTransactionReference());
            errorRecord.setAccountNumber(failedCustomerStatement.getAccountNumber());
            errorRecords.add(errorRecord);
        }
        customerStatementResponse.setErrorRecords(errorRecords);
        return customerStatementResponse;
    }
}
