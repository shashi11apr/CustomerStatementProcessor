package com.rabo.customerstatementprocessor.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rabo.customerstatementprocessor.model.CustomerStatementResponse;

public interface StatementResponseUtil {

    public static ResponseEntity<CustomerStatementResponse> prepareEntityForStatusOk(CustomerStatementResponse customerStatementResponse) {
        return ResponseEntity.status(HttpStatus.OK).body(customerStatementResponse);
    }
    
	public static ResponseEntity<CustomerStatementResponse> prepareEntityForStatusBadRequest(CustomerStatementResponse customerStatementResponse) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customerStatementResponse);
	}

	public static ResponseEntity<CustomerStatementResponse> prepareEntityForInternalServerError(CustomerStatementResponse customerStatementResponse) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customerStatementResponse);
    }	
}
