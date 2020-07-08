package stock_holding;

import java.time.LocalDate;

public class Transaction
{
    private LocalDate date;
    private String stockSymbol;
    private double quantity;
    private TransactionType type;

    public Transaction(LocalDate date, String stockSymbol, double quantity, TransactionType type)
    {
        this.date = date;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.type = type;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public String getStockSymbol()
    {
        return stockSymbol;
    }

    public double getQuantity()
    {
        return quantity;
    }

    public TransactionType getType()
    {
        return type;
    }
}
