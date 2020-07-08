package stock_holding;

public class Holding
{
    private String stockSymbol;
    private double quantity;

    public Holding(String stockSymbol, double quantity)
    {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
    }

    public String getStockSymbol()
    {
        return stockSymbol;
    }

    public double getQuantity()
    {
        return quantity;
    }
}