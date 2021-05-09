package com.rabo.customerstatementprocessor.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class CustomerStatementRequest {

    @EqualsAndHashCode.Include
    private Long transactionReference;
    @EqualsAndHashCode.Exclude
    private Double startBalance;
    @EqualsAndHashCode.Exclude
    private Double mutation;
    @EqualsAndHashCode.Exclude
    private Double endBalance;
    @EqualsAndHashCode.Exclude
    private String description;
    @EqualsAndHashCode.Exclude
    private String accountNumber;
}
