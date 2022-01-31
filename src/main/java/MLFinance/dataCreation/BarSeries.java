package main.java.MLFinance.dataCreation;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class BarSeries {
    private final String name;
    List<TradingBar> tradingBars;

    public BarSeries(String name){
        tradingBars = new ArrayList<>();
        this.name = name;
    }

    public void addBar(TradingBar bar){
        tradingBars.add(bar);
    }

    public void addBar(ZonedDateTime zdt, float open, float high, float low, float close, float volume) {
        tradingBars.add(new TradingBar(zdt, open, high, low, close, volume));
    }

    public int getBarCount() {
        return tradingBars.size();
    }

    public TradingBar getBar(int i) {
        return tradingBars.get(i);
    }

    public void printHead(){
        System.out.println("Printing Head...");
        for(int i = 0; i < 10; i++){
            System.out.print(getBar(i).getZDT() + ", ");
            System.out.print(getBar(i).getOpen() + ", ");
            System.out.print(getBar(i).getHigh() + ", ");
            System.out.print(getBar(i).getLow() + ", ");
            System.out.print(getBar(i).getClose() + ", ");
            System.out.print(getBar(i).getVolume());
            System.out.println();
        }
    }
}
