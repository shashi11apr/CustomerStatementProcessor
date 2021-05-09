package com.rabo.customerstatementprocessor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import com.rabo.customerstatementprocessor.helper.CustomerStatementProcessorHelper;
import com.rabo.customerstatementprocessor.model.CustomerStatementRequest;
import com.rabo.customerstatementprocessor.model.CustomerStatementResponse;
import com.rabo.customerstatementprocessor.util.TestUtil;

@SpringBootTest
public class CustomerStatementProcessorIntegrationTest {

    @Autowired
    private CustomerStatementProcessorController customerStatementProcessor;

    @Autowired
    CustomerStatementProcessorHelper statementProcessorHelper;

    @Test
    @DisplayName("Should return successful status with empty list")
    public void validateCustomerStatementsSuccess() throws JsonParseException, JsonMappingException, IOException {
        
        CustomerStatementResponse expectedCustomerStatementResponse = TestUtil.expectedCustomerResponse(TestUtil.SUCCESSFUL, new ArrayList<CustomerStatementRequest>());
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Success.json");
        ResponseEntity<CustomerStatementResponse> response = customerStatementProcessor.validateCustomerStatements(customerStatementRequests);
        
        assertEquals(expectedCustomerStatementResponse.getResult(), response.getBody().getResult());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords(), response.getBody().getErrorRecords());
    }

    @Test
    @DisplayName("Should return Incorrect End Balance status with list of failed customer record")
    public void validateCustomerStatementsInCorrectEndBalance() throws JsonParseException, JsonMappingException, IOException {
        
        List<CustomerStatementRequest> expectedFailureRecords = TestUtil.createFailedStatementInCorrectEndBalanceList();
        CustomerStatementResponse expectedCustomerStatementResponse = TestUtil.expectedCustomerResponse(TestUtil.INCORRECT_END_BALANCE, expectedFailureRecords);
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_In_Correct_End_Balance.json");

        ResponseEntity<CustomerStatementResponse> response = customerStatementProcessor.validateCustomerStatements(customerStatementRequests);
        assertEquals(expectedCustomerStatementResponse.getResult(), response.getBody().getResult());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(0).getAccountNumber(), response.getBody().getErrorRecords().get(0).getAccountNumber());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(0).getReference(), response.getBody().getErrorRecords().get(0).getReference());
    }

    @Test
    @DisplayName("Should return Duplicate Reference status with list of failed customer record")
    public void validateCustomerStatementsDuplicateReference() throws JsonParseException, JsonMappingException, IOException {

        List<CustomerStatementRequest> expectedFailureRecords = TestUtil.createFailedStatementDuplicateReferenceList();
        CustomerStatementResponse expectedCustomerStatementResponse = TestUtil.expectedCustomerResponse(TestUtil.DUPLICATE_REFERENCE, expectedFailureRecords);
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Duplicate_Reference.json");

        ResponseEntity<CustomerStatementResponse> response = customerStatementProcessor.validateCustomerStatements(customerStatementRequests);
        assertEquals(expectedCustomerStatementResponse.getResult(), response.getBody().getResult());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(0).getReference(), response.getBody().getErrorRecords().get(0).getReference());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(1).getReference(), response.getBody().getErrorRecords().get(1).getReference());
    }

    @Test
    @DisplayName("Should return Duplicate Reference and Incorrect End Balance status with list of failed customer record")
    public void validateCustomerStatementsDuplicateReferenceAndInCorrectEndBalance() throws JsonParseException, JsonMappingException, IOException {

        List<CustomerStatementRequest> expectedFailureEndBalanceRecords = TestUtil.createFailedStatementInCorrectEndBalanceList();
        List<CustomerStatementRequest> expectedFailureDuplicateReferenceRecords = TestUtil.createFailedStatementDuplicateReferenceList();
        List<CustomerStatementRequest> duplicateReferenceAndInCorrectEndBalanceList = new ArrayList<>();
        duplicateReferenceAndInCorrectEndBalanceList.addAll(expectedFailureDuplicateReferenceRecords);
        duplicateReferenceAndInCorrectEndBalanceList.addAll(expectedFailureEndBalanceRecords);
        CustomerStatementResponse expectedCustomerStatementResponse = TestUtil.expectedCustomerResponse(TestUtil.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE, duplicateReferenceAndInCorrectEndBalanceList);
        List<CustomerStatementRequest> customerStatementRequests =
                TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Duplicate_Reference_Incorrect_End_balance.json");

        ResponseEntity<CustomerStatementResponse> response = customerStatementProcessor.validateCustomerStatements(customerStatementRequests);
        assertEquals(expectedCustomerStatementResponse.getResult(), response.getBody().getResult());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(0).getAccountNumber(), response.getBody().getErrorRecords().get(0).getAccountNumber());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(0).getReference(), response.getBody().getErrorRecords().get(0).getReference());
    }

    @Test
    @DisplayName("Should return Bad Request status with empty list")
    public void validateCustomerStatementsEmptyCustomerRequest() throws JsonParseException, JsonMappingException, IOException {

        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Empty.json");

        ResponseEntity<CustomerStatementResponse> response = customerStatementProcessor.validateCustomerStatements(customerStatementRequests);
        assertEquals("BAD_REQUEST", response.getBody().getResult());
        assertEquals(0, response.getBody().getErrorRecords().size());
    }

    @Test
    @DisplayName("Should return JsonMapperException for invalid data type mapping during parsing json file")
    public void validateCustomerStatementsBadRequest() throws JsonParseException, JsonMappingException, IOException {

        Assertions.assertThrows(JsonMappingException.class, () -> {
            TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Bad_Request.json");
        });
    }
    
    @Test
    @DisplayName("Should return RuntimeException")
    public void validateCustomerStatementsInternalServerError() throws JsonParseException, JsonMappingException, IOException {

        Assertions.assertThrows(RuntimeException.class, () -> {
            customerStatementProcessor.validateCustomerStatements(TestUtil.createCustomerStatementInvalidRequest());
        });
    }
}
