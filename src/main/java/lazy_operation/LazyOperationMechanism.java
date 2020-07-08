package lazy_operation;

import jdk.dynalink.NoSuchDynamicMethodException;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class LazyOperationMechanism
{
    private ConcurrentHashMap<String, Callable<Object>> operationsAccessMap = new ConcurrentHashMap<>();

    public void addOperation(Callable callable, int delayInMillis, String Identifier)
    {
        this.operationsAccessMap.put(Identifier, new LazyInvokedOperation(callable, delayInMillis));
    }

    public Object runOperation(String identifier) throws Exception
    {
        if(operationsAccessMap.containsKey(identifier))
        {
            return operationsAccessMap.get(identifier).call();
        }
        throw new NoSuchDynamicMethodException("Service with Identifier of " + identifier + " does not exist.");
    }
}