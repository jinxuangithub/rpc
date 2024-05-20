package com.sjw.shi.rpc.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MockServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
       Class<?> returnType = method.getReturnType();
       return  getDefaultObject(returnType);
    }
    public  Object getDefaultObject(Class<?>returnType){
        if(returnType.isPrimitive()){
            if(returnType==boolean.class){
                return false;
            }else if(returnType==int.class){
                return 5;
            }else if(returnType==long.class){
                return 100L;
            }else if(returnType==float.class){
                return  3.14f;
            }else if(returnType==double.class){
                return 3.141592653589793238462643383;
            }else if (returnType==byte.class){
                return (byte) 0x00;
            }else if(returnType==char.class){
                return 'c';
            }else if(returnType==short.class){
                return (short)5;
            }
        }
        return  null;
    }
}
