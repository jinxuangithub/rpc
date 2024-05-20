package com.sjw.yu.rpc.easy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcResponse implements Serializable {
    private Object result;
    private String message;
    private Class<?> resultType;
    private Throwable exception;

}
