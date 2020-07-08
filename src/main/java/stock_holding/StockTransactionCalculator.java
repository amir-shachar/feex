package stock_holding;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class StockTransactionCalculator
{
    private final Map<String, Double> stockAmounts = new HashMap<>();

    public double getFinalBalance(List<Holding> initialHoldings, LocalDate initialDate,
                                  List<Transaction> transactions, LocalDate finalDate)
    {
        assertValidInput(initialHoldings, initialDate, transactions, finalDate);
        generateInitialStockAmounts(initialHoldings);
        processTransactions(finalDate, sortTransactionsByDate(transactions));
        return calculateStockValues(finalDate);
    }

    private List<Transaction> sortTransactionsByDate(List<Transaction> transactions)
    {
        return transactions.stream().
                sorted(Comparator.comparing(Transaction::getDate)).collect(Collectors.toList());
    }

    private void processTransactions(LocalDate finalDate, List<Transaction> sortedTrans)
    {
        for (Transaction currentTrans : sortedTrans)
        {
            if (passedTheFinishDate(finalDate, currentTrans))
            {
                break;
            }
            updateStockAmount(currentTrans, currentTrans.getStockSymbol(), currentTrans.getQuantity());
        }
    }

    private void assertValidInput(List<Holding> initialHoldings, LocalDate initialDate, List<Transaction> transactions,
                                  LocalDate finalDate)
    {
        CalculatorValidator.assertValidInput(initialHoldings, initialDate, transactions, finalDate);
    }

    private boolean passedTheFinishDate(LocalDate finalDate, Transaction currentTrans)
    {
        return !(currentTrans.getDate().isBefore(finalDate) || currentTrans.getDate().isEqual(finalDate));
    }

    private void updateStockAmount(Transaction currentTrans, String symbol, double quantity)
    {
        if (currentTrans.getType().equals(TransactionType.SELL))
        {
            assertQuantitiesAreValid(symbol, quantity);
            stockAmounts.put(symbol, stockAmounts.get(symbol) - quantity);
        } else
        {
            addStocks(symbol, quantity);
        }
    }

    private void addStocks(String symbol, double quantity)
    {
        if (!stockAmounts.containsKey(symbol))
        {
            stockAmounts.put(symbol, quantity);
        } else
        {
            stockAmounts.put(symbol, stockAmounts.get(symbol) + quantity);
        }
    }

    private void assertQuantitiesAreValid(String symbol, double quantity)
    {
        if (!stockAmounts.containsKey(symbol))
        {
            throw new NoSuchElementException("The Stock Symbol " + symbol +
                    " cannot be sold since it does not exist in the accounts holdings.");
        }
        if (stockAmounts.get(symbol) - quantity < 0)
        {
            throw new IllegalArgumentException("This Account cannot sell " + quantity + " stocks, since it only has "
                    + stockAmounts.get(symbol));
        }
    }

    private void generateInitialStockAmounts(List<Holding> initialHoldings)
    {
        stockAmounts.clear();
        initialHoldings.forEach(hold -> stockAmounts.put(hold.getStockSymbol(), hold.getQuantity()));
    }

    private double calculateStockValues(LocalDate date)
    {
        return stockAmounts.entrySet().stream().map(
                hold -> BigDecimal.valueOf(PriceService.getPrice(hold.getKey(), date) * hold.getValue()))
                .reduce(BigDecimal::add).orElseThrow().doubleValue();
    }
}
class CalculatorValidator
{

    public static String NULL_VALUE_MESSAGE = "Parameters passed to calculator were null. Null value not allowed.";
    public static String EMPTY_LIST_MESSAGE = "Both lists cannot be empty.";
    public static String INVALID_DATES_MESSAGE = "Initial date cannot be later than final date.";

    static void assertValidInput(List<Holding> initialHoldings, LocalDate initialDate, List<Transaction> transactions,
                                 LocalDate finalDate)
    {
        assertNoNullValues(initialHoldings, transactions, initialDate, finalDate);
        assertListsNotEmpty(initialHoldings, transactions);
        assertDatesAeSequential(initialDate, finalDate);
    }
    private static void assertDatesAeSequential(LocalDate initialDate, LocalDate finalDate)
    {
        if (finalDate.isBefore(initialDate))
        {
            throw new IllegalArgumentException(INVALID_DATES_MESSAGE);
        }
    }

    private static void assertListsNotEmpty(List<Holding> initialHoldings, List<Transaction> transactions)
    {
        if (initialHoldings.isEmpty() && transactions.isEmpty())
        {
            throw new IllegalArgumentException(EMPTY_LIST_MESSAGE);
        }
    }

    private static void assertNoNullValues(List<Holding> initialHoldings, List<Transaction> transactions,
                                           LocalDate initialDate, LocalDate finalDate)
    {
        if (initialHoldings == null || transactions == null || initialDate == null || finalDate == null)
        {
            throw new NullPointerException(NULL_VALUE_MESSAGE);
        }
    }
}