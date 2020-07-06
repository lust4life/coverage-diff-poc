package com.poc;

public class SharedLib {

    public static <T> T UseInnerClass(T data) {
        CommonLogInfo(data);
        return InnerSharedLib.Identity(data);
    }

    public static Integer DoubleNumber(Integer num) {
        CommonLogInfo(num);
        return num * 2;
    }

    public static <T> void CommonLogInfo(T data) {
        System.out.println(data);
    }


    /**
     * InnerBar
     */
    public static class InnerSharedLib {
        public static <T> T Identity(T data) {
            return data;
        }
    }
}
