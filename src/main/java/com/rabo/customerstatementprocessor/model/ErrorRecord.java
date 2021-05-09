package com.rabo.customerstatementprocessor.model;

import lombok.Data;

@Data
public class ErrorRecord {

    private Long reference;
    private String accountNumber;
}
