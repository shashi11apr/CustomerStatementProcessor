package com.rabo.customerstatementprocessor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.rabo.customerstatementprocessor.helper.CustomerStatementProcessorHelper;
import com.rabo.customerstatementprocessor.model.CustomerStatementRequest;
import com.rabo.customerstatementprocessor.model.CustomerStatementResponse;
import com.rabo.customerstatementprocessor.util.TestUtil;

@ExtendWith(MockitoExtension.class)
public class CustomerStatementProcessorUnitTest extends Mockito {

    @InjectMocks
    private CustomerStatementProcessorController customerStatementProcessor;

    @Mock
    private CustomerStatementProcessorHelper customerStatementProcessorHelper;

    @Test
    @DisplayName("Should return successful status with empty list")
    public void validateCustomerStatementsSuccess() throws JsonParseException, JsonMappingException, IOException {
        
        CustomerStatementResponse expectedCustomerStatementResponse = TestUtil.expectedCustomerResponse(TestUtil.SUCCESSFUL, new ArrayList<CustomerStatementRequest>());
        when(customerStatementProcessorHelper.validateEndBalance(anyList())).thenReturn(Arrays.asList());
        when(customerStatementProcessorHelper.validateDuplicateTransactionReference(anyList())).thenReturn(Arrays.asList());
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
        when(customerStatementProcessorHelper.validateEndBalance(anyList())).thenReturn(expectedFailureRecords);
        when(customerStatementProcessorHelper.validateDuplicateTransactionReference(anyList())).thenReturn(Arrays.asList());
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_In_Correct_End_Balance.json");
        ResponseEntity<CustomerStatementResponse> response = customerStatementProcessor.validateCustomerStatements(customerStatementRequests);

        assertEquals(expectedCustomerStatementResponse.getResult(), response.getBody().getResult());
        for (int i = 0; i < response.getBody().getErrorRecords().size(); i++) {
            assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(i).getAccountNumber(), response.getBody().getErrorRecords().get(i).getAccountNumber());
            assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(i).getReference(), response.getBody().getErrorRecords().get(i).getReference());    
        }
    }
    
    @Test
    @DisplayName("Should return Duplicate Reference status with list of failed customer record")
    public void validateCustomerStatementsDuplicateReference() throws JsonParseException, JsonMappingException, IOException {
        
        List<CustomerStatementRequest> expectedFailureRecords = TestUtil.createFailedStatementDuplicateReferenceList();
        CustomerStatementResponse expectedCustomerStatementResponse = TestUtil.expectedCustomerResponse(TestUtil.DUPLICATE_REFERENCE, expectedFailureRecords);
        when(customerStatementProcessorHelper.validateEndBalance(anyList())).thenReturn(new ArrayList<>());
        when(customerStatementProcessorHelper.validateDuplicateTransactionReference(anyList())).thenReturn(expectedFailureRecords);
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Duplicate_Reference.json");
        ResponseEntity<CustomerStatementResponse> response = customerStatementProcessor.validateCustomerStatements(customerStatementRequests);
        
        assertEquals(expectedCustomerStatementResponse.getResult(), response.getBody().getResult());
        for (int i = 0; i < response.getBody().getErrorRecords().size(); i++) {
            assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(i).getReference(), response.getBody().getErrorRecords().get(i).getReference());
            assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(i).getReference(), response.getBody().getErrorRecords().get(i).getReference());
        }
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
        when(customerStatementProcessorHelper.validateEndBalance(anyList())).thenReturn(expectedFailureEndBalanceRecords);
        when(customerStatementProcessorHelper.validateDuplicateTransactionReference(anyList())).thenReturn(expectedFailureDuplicateReferenceRecords);
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Duplicate_Reference_Incorrect_End_balance.json");
        ResponseEntity<CustomerStatementResponse> response = customerStatementProcessor.validateCustomerStatements(customerStatementRequests);
        
        assertEquals(expectedCustomerStatementResponse.getResult(), response.getBody().getResult());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(0).getAccountNumber(), response.getBody().getErrorRecords().get(0).getAccountNumber());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords().get(0).getReference(), response.getBody().getErrorRecords().get(0).getReference());
    }
    
    @Test
    @DisplayName("Should return Bad Request status with empty list")
    public void validateCustomerStatementsEmptyCustomerRequest() throws JsonParseException, JsonMappingException, IOException {
        
        CustomerStatementResponse expectedCustomerStatementResponse = TestUtil.expectedCustomerResponse(TestUtil.BAD_REQUEST, new ArrayList<>());
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Empty.json");
        ResponseEntity<CustomerStatementResponse> response = customerStatementProcessor.validateCustomerStatements(customerStatementRequests);
        
        assertEquals(expectedCustomerStatementResponse.getResult(), response.getBody().getResult());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords(), response.getBody().getErrorRecords());
    }
    
    @Test
    @DisplayName("Should return Internal Server Error status with empty list")
    public void validateCustomerStatementsInternalServerError() throws JsonParseException, JsonMappingException, IOException {
        
        CustomerStatementResponse expectedCustomerStatementResponse = TestUtil.expectedCustomerResponse(TestUtil.INTERNAL_SERVER_ERROR, new ArrayList<CustomerStatementRequest>());
        when(customerStatementProcessorHelper.validateEndBalance(anyList())).thenThrow(new RuntimeException());
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Success.json");
        ResponseEntity<CustomerStatementResponse> response = customerStatementProcessor.validateCustomerStatements(customerStatementRequests);

        assertEquals(expectedCustomerStatementResponse.getResult(), response.getBody().getResult());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords(), response.getBody().getErrorRecords());
    }
    
    @Test
    @DisplayName("Should return Bad Request status with empty list")
    public void validateCustomerStatementsNullPointerException() throws JsonParseException, JsonMappingException, IOException {
        
        CustomerStatementResponse expectedCustomerStatementResponse = TestUtil.expectedCustomerResponse(TestUtil.BAD_REQUEST, new ArrayList<CustomerStatementRequest>());
        when(customerStatementProcessorHelper.validateEndBalance(anyList())).thenThrow(new NullPointerException());
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Success.json");
        ResponseEntity<CustomerStatementResponse> response = customerStatementProcessor.validateCustomerStatements(customerStatementRequests);

        assertEquals(expectedCustomerStatementResponse.getResult(), response.getBody().getResult());
        assertEquals(expectedCustomerStatementResponse.getErrorRecords(), response.getBody().getErrorRecords());
    }
}
