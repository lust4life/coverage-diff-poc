package com.poc;

public class TestCaseEntry {

    public void TestCase1() {
        DoubleNumIfMoreThan5(10);
    }

    public void TestCase2() {
        DoubleNumIfMoreThan5(2);
    }

    public void TestCase3() {
        SharedLib.CommonLogInfo("do nothing");
    }

    private Integer DoubleNumIfMoreThan5(int num) {
        if (num > 5) {
            return SharedLib.DoubleNumber(num);
        } else {
            return SharedLib.UseInnerClass(num);
        }
    }
}
