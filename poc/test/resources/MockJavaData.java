package poc.test.somepackage;

import java.util.Optional;

public abstract class MockJavaData {
    public void F1() {
    }

    protected double F2(int a) {
        return a;
    }

    public abstract MockJavaData F3();

    // test generic type
    public <N extends MockJavaData> Optional<N> F4(Optional<N> start) {
        return start;
    }

    private class NestedClass{
        public void F5(){

        }
    }
}

class AnotherTopLevelClass {
    public void F6() {
    }
}