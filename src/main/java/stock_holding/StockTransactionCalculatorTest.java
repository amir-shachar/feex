package stock_holding;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static stock_holding.CalculatorValidator.EMPTY_LIST_MESSAGE;

class StockTransactionCalculatorTest
{
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<Holding> holdings = new ArrayList<>();
    private LocalDate start;
    private LocalDate finish;

    @BeforeEach
    void clearLists()
    {
        transactions.clear();
        holdings.clear();
        start = LocalDate.now().minusDays(30);
        finish = LocalDate.now().minusDays(3);
    }

    @Test
    void noTransaction_totalShouldBeStockPrice()
    {
        StockTransactionCalculator calculator = new StockTransactionCalculator();
        holdings.add(new Holding("A", 1));
        double result = calculator.getFinalBalance(holdings, start, transactions, finish);

        assertEquals(PriceService.getPrice("A", finish), result);
    }

    @Test
    void oneTransaction_noStocksInEnd_totalShouldBeZero()
    {
        StockTransactionCalculator calculator = new StockTransactionCalculator();
        holdings.add(new Holding("A", 1));
        transactions.add(new Transaction(LocalDate.now().minusDays(3), "A", 1, TransactionType.SELL));
        double result = calculator.getFinalBalance(holdings, start, transactions, finish);

        assertEquals(0, result);
    }

    @Test
    void twoMeasures_secondIsMadeOfOneStock_shouldEqualValueOfStock()
    {
        StockTransactionCalculator calculator = new StockTransactionCalculator();
        holdings.add(new Holding("A", 1));
        transactions.add(new Transaction(LocalDate.now().minusDays(5), "B", 1, TransactionType.BUY));

        double result = calculator.getFinalBalance(holdings, start, transactions, finish);
        transactions.add(new Transaction(LocalDate.now().minusDays(4), "B", 1, TransactionType.SELL));
        double result2 = calculator.getFinalBalance(holdings, start, transactions, finish);

        assertEquals(PriceService.getPrice("B", finish),
                BigDecimal.valueOf(result).subtract(BigDecimal.valueOf(result2)).doubleValue());
    }

    @Test
    void twoMeasures_secondIsHasTransactionOutOfDate_shouldBeEqual()
    {
        StockTransactionCalculator calculator = new StockTransactionCalculator();
        holdings.add(new Holding("A", 1));
        double result = calculator.getFinalBalance(holdings, start, transactions, finish);

        transactions.add(new Transaction(LocalDate.now().minusDays(1), "B", 1, TransactionType.BUY));
        double result2 = calculator.getFinalBalance(holdings, start, transactions, finish);

        assertEquals(result, result2);
    }

    @Test
    void measureSameStocks_givenByInitialHoldingOrTransactional_shouldBeEqual()
    {
        StockTransactionCalculator calculator = new StockTransactionCalculator();
        holdings.add(new Holding("A", 1));
        holdings.add(new Holding("B", 1));
        double result = calculator.getFinalBalance(holdings, start, transactions, finish);
        holdings.clear();

        transactions.add(new Transaction(LocalDate.now().minusDays(5), "B", 1, TransactionType.BUY));
        transactions.add(new Transaction(LocalDate.now().minusDays(7), "A", 1, TransactionType.BUY));
        double result2 = calculator.getFinalBalance(holdings, start, transactions, finish);

        assertEquals(result, result2);
    }

    @Test
    void bothListsEmpty_shouldThrowIllegalArgumentException()
    {
        StockTransactionCalculator calculator = new StockTransactionCalculator();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> calculator.getFinalBalance(holdings, start, transactions, finish));

        assertEquals(exception.getMessage(), EMPTY_LIST_MESSAGE);
    }

    @Test
    void illegalDates_shouldThrowIllegalArgumentException()
    {
        StockTransactionCalculator calculator = new StockTransactionCalculator();
        holdings.add(new Holding("A", 1));
        start = LocalDate.now().minusDays(31);
        finish = LocalDate.now().minusDays(32);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> calculator.getFinalBalance(holdings, start, transactions, finish));

        assertEquals(exception.getMessage(), CalculatorValidator.INVALID_DATES_MESSAGE);
    }

    @Test
    void sellMoreThanAccountHas_shouldThrowIllegalArgumentException()
    {
        StockTransactionCalculator calculator = new StockTransactionCalculator();
        holdings.add(new Holding("A", 1));
        transactions.add(new Transaction(LocalDate.now().minusDays(5), "A", 2, TransactionType.SELL));
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate finish = LocalDate.now().minusDays(1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> calculator.getFinalBalance(holdings, start, transactions, finish));

        assertTrue(exception.getMessage().contains("This Account cannot sell"));
    }

    @Test
    void sellStockThatDoesNotExist_shouldThrowNoSuchElementException()
    {
        transactions.add(new Transaction(LocalDate.now().minusDays(5), "A", 2, TransactionType.SELL));
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate finish = LocalDate.now().minusDays(1);
        StockTransactionCalculator calculator = new StockTransactionCalculator();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> calculator.getFinalBalance(holdings, start, transactions, finish));

        assertTrue(exception.getMessage().contains("does not exist"));
    }


}