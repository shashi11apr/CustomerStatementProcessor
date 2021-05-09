package com.rabo.customerstatementprocessor.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("Customer Statement Response")
@Data
public class CustomerStatementResponse {

    @JsonProperty(value = "result")
    private String result;
    @JsonProperty(value = "errorRecords")
    private List<ErrorRecord> errorRecords;
}
