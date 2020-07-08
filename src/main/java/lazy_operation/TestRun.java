package lazy_operation;

public class TestRun
{
    public static void main(String[] args) throws Exception
    {
        LazyOperationMechanism mechanism = new LazyOperationMechanism();
        mechanism.addOperation(new ExampleCallable(), 3_000, "count");

        int i=0;
        while (i<20)
        {
            i++;
            Integer integer = (Integer) mechanism.runOperation("count");
            System.out.println("count returned " + integer);
            Thread.sleep(500);
        }

    }
}
