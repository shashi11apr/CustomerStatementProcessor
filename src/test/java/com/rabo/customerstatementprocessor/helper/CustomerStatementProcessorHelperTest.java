package com.rabo.customerstatementprocessor.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rabo.customerstatementprocessor.model.CustomerStatementRequest;
import com.rabo.customerstatementprocessor.util.TestUtil;

@ExtendWith(MockitoExtension.class)
public class CustomerStatementProcessorHelperTest {

    @InjectMocks
    private CustomerStatementProcessorHelper customerStatementProcessorHelper;
    
    @Test
    @DisplayName("Should return list of failed customer statement records whose references are duplicate")
    public void validateCustomerStatementsDuplicateReferenceSuccess() throws JsonParseException, JsonMappingException, IOException {
        
        List<CustomerStatementRequest> expectedFailureRecords = TestUtil.createFailedStatementDuplicateReferenceList();
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Duplicate_Reference.json");
        List<CustomerStatementRequest> response = customerStatementProcessorHelper.validateDuplicateTransactionReference(customerStatementRequests);
        
        for (int i = 0; i < response.size(); i++) {
            assertEquals(expectedFailureRecords.get(i).getTransactionReference(), response.get(i).getTransactionReference());
            assertEquals(expectedFailureRecords.get(i).getAccountNumber(), response.get(i).getAccountNumber());
        }
    }
    
    @Test
    @DisplayName("Should return Empty list of failed customer statement records")
    public void validateCustomerStatementsDuplicateReferenceEmpty() throws JsonParseException, JsonMappingException, IOException {
        
        List<CustomerStatementRequest> expectedFailureRecords = new ArrayList<>();
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Success.json");
        List<CustomerStatementRequest> response = customerStatementProcessorHelper.validateDuplicateTransactionReference(customerStatementRequests);
        
        assertEquals(expectedFailureRecords.size(), response.size());
    }
    
    @Test
    @DisplayName("Should return list of failed customer statement records whose End Balance is Incorrect")
    public void validateCustomerStatementsInCorrectEndBalanceSuccess() throws JsonParseException, JsonMappingException, IOException {
        
        List<CustomerStatementRequest> expectedFailureRecords = TestUtil.createFailedStatementInCorrectEndBalanceList();
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_In_Correct_End_Balance.json");
        List<CustomerStatementRequest> response = customerStatementProcessorHelper.validateEndBalance(customerStatementRequests);

        for (int i = 0; i < response.size(); i++) {
            assertEquals(expectedFailureRecords.get(i).getTransactionReference(), response.get(i).getTransactionReference());
            assertEquals(expectedFailureRecords.get(i).getAccountNumber(), response.get(i).getAccountNumber());
        }
    }
    
    @Test
    @DisplayName("Should return Empty list of failed customer statement records")
    public void validateCustomerStatementsInCorrectEndBalanceEmpty() throws JsonParseException, JsonMappingException, IOException {
        
        List<CustomerStatementRequest> expectedFailureRecords = new ArrayList<>();
        List<CustomerStatementRequest> customerStatementRequests = TestUtil.getCustomerStatementsUsingJSon("Customer_Statement_Request_Success.json");
        List<CustomerStatementRequest> response = customerStatementProcessorHelper.validateEndBalance(customerStatementRequests);
        
        assertEquals(expectedFailureRecords.size(), response.size());
    }
}
