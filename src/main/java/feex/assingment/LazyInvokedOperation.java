package feex.assingment;

import java.util.concurrent.Callable;

public class LazyInvokedOperation implements Callable<Object>
{
    private final Callable<Object> operation;
    private Object returnObject;
    private final int delayInMillis;
    private long lastRunTime=0;

    public LazyInvokedOperation(Callable<Object> callable, int delayInMillis)
    {
        this.operation=callable;
        this.delayInMillis= delayInMillis;

    }

    @Override
    public Object call()
    {
        try
        {
            if (this.lastRunTime + delayInMillis < System.currentTimeMillis())
            {
                returnObject = operation.call();
                this.lastRunTime = System.currentTimeMillis();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return returnObject;
    }
}
