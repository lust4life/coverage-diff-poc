package poc.test.somepackage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class MockJavaData {
    public void F1() {
    }

    protected double F2(int a) {
        return a;
    }

    public abstract MockJavaData F3();

    // test generic type
    public <DataType extends MockJavaData, T> List<DataType> SomeGenericMethod(T raw, DataType data, Optional<DataType> openGeneric, Map<Integer, List<DataType>> info, int num, List<TestCaseEntry> closedGenericType) {
        return info.getOrDefault(1, List.of());
    }

    private class NestedClass {
        public void F5() {

        }
    }
}

class AnotherTopLevelClass {
    public void F6() {
    }
}