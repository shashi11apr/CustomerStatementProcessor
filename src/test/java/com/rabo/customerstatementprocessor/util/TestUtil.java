package com.rabo.customerstatementprocessor.util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rabo.customerstatementprocessor.model.CustomerStatementRequest;
import com.rabo.customerstatementprocessor.model.CustomerStatementResponse;
import com.rabo.customerstatementprocessor.model.ErrorRecord;

public class TestUtil {

    public static final String SUCCESSFUL = "SUCCESSFUL";
    public static final String DUPLICATE_REFERENCE = "DUPLICATE_REFERENCE";
    public static final String INCORRECT_END_BALANCE = "INCORRECT_END_BALANCE";
    public static final String DUPLICATE_REFERENCE_INCORRECT_END_BALANCE = "DUPLICATE_REFERENCE_INCORRECT_END_BALANCE";
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    /**
     * This method is used to load the JSON content to object.
     * 
     * @param fileName - Name of file containing JSON value
     * @return List<CustomerStatementRequest> - List of Customer Statements
     * @throws JsonParseException - Exception while converting value from JSON to Object
     * @throws JsonMappingException - Exception while converting value from JSON to Object
     * @throws IOException - Exception while retrieve value from file
     */
    public static List<CustomerStatementRequest> getCustomerStatementsUsingJSon(final String fileName)
            throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        List<CustomerStatementRequest> customerStatements =
                Arrays.asList(mapper.readValue(Paths.get("src/test/resources/" + fileName).toFile(), CustomerStatementRequest[].class));

        return customerStatements;
    }

    /**
     * This method is used to create customer statement invalid request.
     */
    public static List<CustomerStatementRequest> createCustomerStatementInvalidRequest() {

        CustomerStatementRequest customerStatementRequest = new CustomerStatementRequest();
        customerStatementRequest.setTransactionReference(1234l);
        customerStatementRequest.setDescription("Tikke from Mohammad");
        customerStatementRequest.setStartBalance(30.0);

        CustomerStatementRequest customerStatementRequest1 = new CustomerStatementRequest();
        customerStatementRequest1.setTransactionReference(1234l);
        customerStatementRequest1.setDescription("Tikke from Mohammad");
        customerStatementRequest1.setStartBalance(30.0);

        List<CustomerStatementRequest> statementRequests = Arrays.asList();
        statementRequests.add(customerStatementRequest);
        statementRequests.add(customerStatementRequest1);

        return statementRequests;
    }

    /**
     * This method is used to create list of customer statements of incorrect endbalance.
     */
    public static List<CustomerStatementRequest> createFailedStatementInCorrectEndBalanceList() {

        CustomerStatementRequest customerStatementRequest = new CustomerStatementRequest();
        customerStatementRequest.setTransactionReference(120003l);
        customerStatementRequest.setAccountNumber("NL74ABNA0248990274");
        customerStatementRequest.setDescription("Tulip from Peter");
        customerStatementRequest.setStartBalance(50233.0);
        customerStatementRequest.setMutation(10.0);
        customerStatementRequest.setEndBalance(60.0);

        List<CustomerStatementRequest> statementRequests = new ArrayList<>();
        statementRequests.add(customerStatementRequest);
        return statementRequests;
    }

    /**
     * This method is used to create list of customer statements of duplicate transaction reference.
     */
    public static List<CustomerStatementRequest> createFailedStatementDuplicateReferenceList() {

        CustomerStatementRequest customerStatementRequest = new CustomerStatementRequest();
        customerStatementRequest.setTransactionReference(120001l);
        customerStatementRequest.setAccountNumber("NL69ABNA0433641114");
        customerStatementRequest.setDescription("Tickets for cinema");
        customerStatementRequest.setStartBalance(1000.0);
        customerStatementRequest.setMutation(-300.0);
        customerStatementRequest.setEndBalance(700.0);

        CustomerStatementRequest customerStatementRequest1 = new CustomerStatementRequest();
        customerStatementRequest1.setTransactionReference(120001l);
        customerStatementRequest1.setAccountNumber("NL93ABNA0585619023");
        customerStatementRequest1.setDescription("Tickets from John");
        customerStatementRequest1.setStartBalance(50.0);
        customerStatementRequest1.setMutation(10.0);
        customerStatementRequest1.setEndBalance(60.0);

        List<CustomerStatementRequest> statementRequests = new ArrayList<>();
        statementRequests.add(customerStatementRequest);
        statementRequests.add(customerStatementRequest1);
        return statementRequests;
    }

    /**
     * This method is used to create expected customer statement response using the status and failure records.
     * @param result - Reason of failed records
     * @param expectedFailedRecords - List of failed records
     * @return CustomerStatementResponse - Customer statements validation response
     */
    public static CustomerStatementResponse expectedCustomerResponse(String result, List<CustomerStatementRequest> expectedFailedRecords) {
        CustomerStatementResponse customerStatementResponse = new CustomerStatementResponse();
        List<ErrorRecord> errorRecords = new ArrayList<>();
        ErrorRecord errorRecord = new ErrorRecord();
        for (CustomerStatementRequest failedRecords : expectedFailedRecords) {

            errorRecord.setReference(failedRecords.getTransactionReference());
            errorRecord.setAccountNumber(failedRecords.getAccountNumber());
            errorRecords.add(errorRecord);
        }

        customerStatementResponse.setResult(result);
        customerStatementResponse.setErrorRecords(errorRecords);

        return customerStatementResponse;
    }
}
