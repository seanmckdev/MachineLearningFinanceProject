package main.java.MLFinance.dataCreation;

public class Factors {
    /*
    All factors *should* results in numbers between 0 and 1 since neuron activation works best in that range

     0     1     2    3      4
     Open  High  Low  Close  Volume
     */

    // Change in price from first bar to last bar in frame
    public static float priceDelta(float[][] pricing){
        final int FRAMESIZE = pricing.length;
        return calcDelta(pricing[0][3], pricing[FRAMESIZE-1][3]);
    }

    // Change in volume from first bar to last bar in frame
    public static float volumeDelta(float[][] pricing){
        final int FRAMESIZE = pricing.length;
        return pricing[FRAMESIZE-1][4] - pricing[0][4];
    }

    // average percent difference from the mean
    public static float averageDeviancy(float[][] pricing){
        final int FRAMESIZE = pricing.length;
        float avg = 0;

        for(int i = 0; i < FRAMESIZE; i++){
            avg += pricing[i][3];
        }

        avg = avg / (float)FRAMESIZE;

        float deviance = 0;

        for(int i = 0; i < FRAMESIZE; i++){
            deviance += Math.abs(calcDelta(avg, pricing[i][3]));
        }
        deviance = deviance / (float)FRAMESIZE;
        return deviance;
    }

    // How many times the closing price was greater than the opening price for each trading bar in the frame
    public static float percentPositiveDelta(float[][] pricing){
        final int FRAMESIZE = pricing.length;
        float count = 0;

        for(int i = 0; i < FRAMESIZE; i++){
            count += pricing[i][3] > pricing[i][0] ? 1 : 0;
        }

        return count / (float)FRAMESIZE;
    }

    // Calculates the simple slope from the start and end of the frame
    public static float slope(float[][] pricing){
        final int FRAMESIZE = pricing.length;

        float x1 = 0, x2 = FRAMESIZE-1;
        float y1 = pricing[0][3], y2 = pricing[FRAMESIZE-1][3];
        float y = y1 - y2;
        float x = x1 - x2;
        float slope = y/x;

        return slope;
    }

    public static float slopeDeviancy(float[][] pricing){
        final int FRAMESIZE = pricing.length;
        float slope = slope(pricing);

        float avgDeviance = 0;

        for(int i = 0; i < FRAMESIZE; i ++){
            float expected = slope*i;
            float real = pricing[i][3];

            avgDeviance += Math.abs(real - expected);
        }
        avgDeviance = avgDeviance / (float)FRAMESIZE;

        return avgDeviance;
    }

    // finds the relative percent change from a to b
    public static float calcDelta(float a, float b){
        return (b - a) / a;
    }
}
