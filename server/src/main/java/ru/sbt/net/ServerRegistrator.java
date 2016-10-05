package ru.sbt.net;

import ru.sbt.net.workTask.Worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ServerRegistrator {
    private static volatile String methodName;
    private static volatile Object[] args;

    public static void listen(int port, Object impl) {
        ServerSocket serverSocket;
        Object execStatus;
        try {
            serverSocket = new ServerSocket(port);
            try (Socket getData = serverSocket.accept()) {
                execStatus = receiveParameters(getData);
                if (execStatus == null) {
                    sendResult(getData, invokeCalculation(impl));
                } else {
                    sendResult(getData, execStatus);
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Unable to connect to client. Added to server log");
        }
    }

    private static Object receiveParameters(Socket getData) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(getData.getInputStream());
            methodName = (String) inputStream.readObject();
            args = (Object[]) inputStream.readObject();
        } catch (IOException e) {
            return e;
        } catch (ClassNotFoundException e) {
            return e;
        }
        return null;
    }

    private static Object invokeCalculation(Object impl) {
        Double result;
        try {
            Method implCalculation = impl.getClass().getMethod(methodName, int.class, int.class);
            result = (Double) implCalculation.invoke(impl, args);
        } catch (NoSuchMethodException e) {
            return e;
        } catch (InvocationTargetException e) {
            return e;
        } catch (IllegalAccessException e) {
            return e;
        }
        return result;
    }

    private static void sendResult(Socket sendData, Object result) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(sendData.getOutputStream());
            outputStream.writeObject(result);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List<Thread> simpleThreadPool = new ArrayList<>();
        Runnable worker = new Worker();
        simpleThreadPool.add(new Thread(worker));
        while (true) {
            for (Thread poolItem : simpleThreadPool) {
                switch (poolItem.getState()) {
                    case NEW: {
                        poolItem.start();
                        break;
                    }
                    case TERMINATED:
                        simpleThreadPool.remove(poolItem);
                        simpleThreadPool.add(new Thread(worker));
                        break;
                }
            }
        }
    }
}