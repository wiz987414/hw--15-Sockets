package ru.sbt.net;

public class CalculatorImpl implements Calculator {
    public Double calculate(int a, int b) {
        return (double) (a + b - 410 * 12);
    }
}
