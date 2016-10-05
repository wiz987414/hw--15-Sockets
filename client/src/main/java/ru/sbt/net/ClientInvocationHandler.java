package ru.sbt.net;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

class ClientInvocationHandler implements InvocationHandler {
    private final String host;
    private final int port;

    ClientInvocationHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try (Socket client = new Socket(this.host, this.port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
            objectOutputStream.writeObject(method.getName());
            objectOutputStream.writeObject(args);
            objectOutputStream.flush();

            ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
            Object result = inputStream.readObject();
            if (result.getClass() == Double.class) return result;
            assert (result.getClass() == Throwable.class);
            throw new RuntimeException((Throwable) result);
        }
    }
}