package feex.assingment;

import java.util.concurrent.Callable;

public class ExampleCallable implements Callable<Integer>
{
    private int count = 0;

    @Override
    public Integer call() throws Exception
    {
        count++;
        return count;
    }
}
