package ru.sbt.net;

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.reflect.Proxy.newProxyInstance;

public class NetClientFactory {
    private final String host;
    private final int port;

    private NetClientFactory(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private <T> T createClient(Class<T> interfaceClass) {
        return (T) newProxyInstance(getSystemClassLoader(), new Class[]{interfaceClass}, new ClientInvocationHandler(host, port));
    }

    public static void main(String[] args) {
        NetClientFactory factory = new NetClientFactory("localhost", 5000);
        Calculator client = factory.createClient(Calculator.class);
        System.out.println(client.calculate(1, 2));
    }
}
