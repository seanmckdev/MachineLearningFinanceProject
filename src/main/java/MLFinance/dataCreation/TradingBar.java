package main.java.MLFinance.dataCreation;

import java.time.ZonedDateTime;

public class TradingBar {
    private final ZonedDateTime zdt;
    private final float open;
    private final float high;
    private final float low;
    private final float close;
    private final float volume;

    public TradingBar(ZonedDateTime zdt, float open, float high, float low, float close, float volume) {
        this.zdt = zdt;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public ZonedDateTime getZDT(){
        return zdt;
    }

    public float getOpen(){
        return open;
    }

    public float getHigh(){
        return high;
    }

    public float getLow(){
        return low;
    }

    public float getClose(){
        return close;
    }

    public float getVolume(){
        return volume;
    }
}
