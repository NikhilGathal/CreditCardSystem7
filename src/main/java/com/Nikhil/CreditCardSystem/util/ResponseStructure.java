package com.Nikhil.CreditCardSystem.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseStructure<T> {

    private String message;
    private String httpstatus;
    private Object data; 			// user object

}
