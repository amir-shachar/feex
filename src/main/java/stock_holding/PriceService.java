package stock_holding;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PriceService
{
    private static final Map<Map.Entry<String, LocalDate>, Double> stockPriceHistory = new ConcurrentHashMap<>();

    public static double getPrice(String symbol, LocalDate date)
    {
        AbstractMap.SimpleEntry<String, LocalDate> currentKey = new AbstractMap.SimpleEntry<>(symbol, date);
        if (stockPriceHistory.containsKey(currentKey))
        {
            return stockPriceHistory.get(currentKey);
        } else
        {
            double newPrice = ((int) (Math.random() * 1000.0)) / 1000.0;
            stockPriceHistory.put(currentKey, newPrice);
            return newPrice;
        }
    }
}
