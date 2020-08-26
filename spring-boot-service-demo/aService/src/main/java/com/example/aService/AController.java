package com.example.aService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AController {

    @GetMapping("/run-testcase/{caseId}")
    public String greeting(@PathVariable Integer caseId) {
        switch (caseId) {
            case 1:
                DoubleNumIfMoreThan5(10);
                break;
            case 2:
                DoubleNumIfMoreThan5(2);
                break;
            case 3:
                SharedLib.CommonLogInfo("do nothing");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + caseId);
        }

        return String.format("test case %s done.", caseId);
    }

    private Integer DoubleNumIfMoreThan5(int num) {
        if (num > 5) {
            return SharedLib.DoubleNumber(num);
        } else {
            return SharedLib.UseInnerClass(num);
        }
    }
}