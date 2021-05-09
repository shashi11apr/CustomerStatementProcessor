package com.rabo.customerstatementprocessor.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rabo.customerstatementprocessor.model.CustomerStatementRequest;
import com.rabo.customerstatementprocessor.model.CustomerStatementResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Customer statement processor API.
 */
@RequestMapping(value = "/statementprocesssor")
@Api(tags = { "Customer Statement Processor" })
public interface ICustomersStatementProcessor {

    @PostMapping(value = "/validatestatements", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, value = "Validate Customer Statements", response = CustomerStatementResponse.class)
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public ResponseEntity<CustomerStatementResponse> validateCustomerStatements(@RequestBody List<CustomerStatementRequest> customerStatements);
}
