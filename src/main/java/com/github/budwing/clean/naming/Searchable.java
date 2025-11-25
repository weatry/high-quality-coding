package com.github.budwing.clean.naming;

public class Searchable {
    public static final String SLOGAN = "USE SEARCHABLE NAMES";

    /**
     * It's easier to search code with descriptive names.
     */
    private int age = 30;
    private String birthday = "1990-01-01";
    private String customerId = "C123456";

    public void week(String s) {
        System.out.println("Age: " + age + ", Birth Date: " + birthday + ", Customer ID: " + customerId);
        // DAYS_IN_WEEK is easier to search than 7
        final int DAYS_IN_WEEK = 7;
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            System.out.println("day in a week: " + i);
        }
    }
}
