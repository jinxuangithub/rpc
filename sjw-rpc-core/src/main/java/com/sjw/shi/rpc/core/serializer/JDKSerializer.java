package com.sjw.shi.rpc.core.serializer;

import com.sjw.shi.rpc.core.serializer.Serializer;

import java.io.*;

public class JDKSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream
                objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new
                ObjectInputStream(inputStream);
        try {
            return (T) objectInputStream.readObject();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);

        } finally {
            objectInputStream.close();
        }
    }
}
