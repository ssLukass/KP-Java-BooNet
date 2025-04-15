package com.example.boonet.core.entities;

class ExeptionHandler{

    public void divide(int a, int b) {
        try {
            int result = a / b;
            System.out.println("Результат деления: " + result);
        } catch (ArithmeticException e) {
            System.out.println("Ошибка: Деление на ноль невозможно!");
        }
    }

    public void parseNumber(String input) {
        try {
            int number = Integer.parseInt(input);
            System.out.println("Вы ввели число: " + number);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Неверный формат числа!");
        }
    }


    public void riskyOperation() {
        try {
            String str = null;
            System.out.println(str.length()); // вызовет NullPointerException
        } catch (Exception e) {
            System.out.println("Произошло исключение: " + e.getClass().getSimpleName());
            System.out.println("Сообщение: " + e.getMessage());
        }
    }
}
