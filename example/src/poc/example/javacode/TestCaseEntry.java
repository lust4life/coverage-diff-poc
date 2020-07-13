package poc.example.javacode;

import poc.example.javacode.some.inner.folder.SharedLib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public static void main(String[] args) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        var run = new TestCaseEntry();
        var keep = true;
        while (keep) {
            System.out.println("run which case 1/2/3 ?");
            var cmd = reader.readLine();
            switch (cmd) {
                case "1":
                    run.TestCase1();
                    break;
                case "2":
                    run.TestCase2();
                    break;
                case "3":
                    run.TestCase3();
                    break;
                default:
                    System.out.println("bye!");
                    keep = false;
                    break;
            }
        }
    }
    
}