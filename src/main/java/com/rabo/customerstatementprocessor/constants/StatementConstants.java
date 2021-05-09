package com.rabo.customerstatementprocessor.constants;

public class StatementConstants {

    public static final String DUPLICATE_REFERENCE = "DUPLICATE_REFERENCE";
    public static final String INCORRECT_END_BALANCE = "INCORRECT_END_BALANCE";
    public static final String DUPLICATE_REFERENCE_INCORRECT_END_BALANCE = "DUPLICATE_REFERENCE_INCORRECT_END_BALANCE";
    public static final String SUCCESSFUL = "SUCCESSFUL";
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    
    
    /* Logger Constants */
    public static final String END_BALANCE_FAILURE = "Validated end balance failure records";
    public static final String DUPLICATE_REFERENCE_FAILURE = "Validated duplicate transaction reference failure records";
    public static final String JSON_PARSE_FAILURE = "Json parsing failed";
    public static final String INTERNAL_SERVER_FAILURE = "Internal Server Error occurred";
    public static final String END_BALANCE_VALIDATION = "Validating end balance";
    public static final String DUPLICATE_REFERENCE_VALIDATION = "Validating duplicate references";
    public static final String CREATE_STATEMENT_RESPONSE = "Creating Customer Statement Response";
    
    private StatementConstants() {
        
    }
}
