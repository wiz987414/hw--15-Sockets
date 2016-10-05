package ru.sbt.net.workTask;

import ru.sbt.net.CalculatorImpl;
import ru.sbt.net.ServerRegistrator;

public class Worker implements Runnable {
    @Override
    public void run() {
        ServerRegistrator.listen(5000, new CalculatorImpl());
    }
}
