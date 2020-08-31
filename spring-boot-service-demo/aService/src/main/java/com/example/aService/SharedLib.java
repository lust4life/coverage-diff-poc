package com.example.aService;

public class SharedLib {

    public static <T> T UseInnerClass(T data) {
        var idData = InnerSharedLib.Identity(data);
        CommonLogInfo("the identity data of " + data + " is " + idData);
        return idData;
    }

    public static Integer DoubleNumber(Integer num) {
        CommonLogInfo(String.format("double number %s is %s", num, num * 2));
        return num * 2;
    }

    public static <T> void CommonLogInfo(T data) {
        System.out.println(data);
    }

    /**
     * Inner shared lib with some generic type signature
     */
    public static class InnerSharedLib {
        public static <T> T Identity(T data) {
            CommonLogInfo("calling identity method before return");
            return data;
        }
    }
}
