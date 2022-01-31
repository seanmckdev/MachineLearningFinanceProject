package main.java.MLFinance.dataCreation;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.java.MLFinance.dataCreation.Factors.*;

public class DataframeCreator {

    private final int LOOKAHEAD = 60; // How many trading bars to look ahead to find answer for training.
    private final int FRAMESIZE = 60; // Size of frame to create each entry in the training data.

    private BarSeries barSeries;

    public List<String[]> inData;
    public List<Double[]> outData;

    public List<Double[]> rawOutData; // not normalized

    float[][][] bigOHLCV;
    int[] ans; // Series containing whether x + LOOKAHEAD is higher than x to represent correct answers for training 0/1

    /*
    Creates the final dataframe that will be exported as the training data.
     */
    public DataframeCreator(List<String[]> data){
        this.inData = data;
        //outData = reduce();
        barSeries = new BarSeries("USDJPY");
        populateBarSeries();

        rawOutData = new ArrayList<>();

        //int size = (barSeries.getBarCount() - LOOKAHEAD) - FRAMESIZE;
        int size = barSeries.getBarCount() - LOOKAHEAD;
        bigOHLCV = new float[size][FRAMESIZE][5]; //Creates the massive training dataset.
        ans = new int[size];

        createTrainingData();
        //System.out.println(bigOHLCV[0][0][0] + ", " + ans[0]);
        DataReader.printHeadFloat(bigOHLCV[0]);
        System.out.println("Ans: " + ans[0]);

        try {
            Path path = Paths.get("training.csv"); //ClassLoader.getSystemResource("/training.csv").toURI()
            writeToCSV(path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // calculate each factor and write it to a flatefile CSV with the answers for training
    public void writeToCSV(Path path) throws Exception{
        CSVWriter writer = new CSVWriter(new FileWriter(path.toString()));
        final DecimalFormat df = new DecimalFormat("0.0000");

        for(int i = 0; i < bigOHLCV.length; i++){
            String[] trainingOut = new String[7];
            trainingOut[0] = df.format(priceDelta(bigOHLCV[i]));
            trainingOut[1] = df.format(volumeDelta(bigOHLCV[i]));
            trainingOut[2] = df.format(averageDeviancy(bigOHLCV[i]));
            trainingOut[3] = df.format(percentPositiveDelta(bigOHLCV[i]));
            trainingOut[4] = df.format(slope(bigOHLCV[i]));
            trainingOut[5] = df.format(slopeDeviancy(bigOHLCV[i]));

            trainingOut[6] = String.valueOf(ans[i]);

            writer.writeNext(trainingOut);

        }

        writer.close();
    }

    /*
    Populates the BarSeries with data that was loaded into the correct format.
     */
    public void populateBarSeries(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"); //"yyyy-MM-dd HH:mm:ss a"
        ZoneId zoneId = ZoneId.of("America/New_York");

        for(int i=0; i< inData.size(); i++){
            LocalDateTime ldt = LocalDateTime.parse(inData.get(i)[0] + " " + inData.get(i)[1], formatter);
            ZonedDateTime zdt = ZonedDateTime.of(ldt, zoneId);

            float open     = Float.parseFloat(inData.get(i)[2]);
            float high     = Float.parseFloat(inData.get(i)[3]);
            float low      = Float.parseFloat(inData.get(i)[4]);
            float close    = Float.parseFloat(inData.get(i)[5]);
            float volume   = Float.parseFloat(inData.get(i)[6]);

            barSeries.addBar(zdt,open,high,low,close,volume);
        }
        barSeries.printHead();
    }

    // Reduces the raw data into what is needed
    private List<Double[]> reduce(){
        // 0    1    2    3    4   5     6
        // Date Time Open High Low Close Volume

        // 100 minute time frame:
        // New data format
        // index    norm. open      norm. vol       delta       train
        // 0        1-20            21-41           43-62       63

        List<Double[]> newData = new ArrayList<>();
        for(int i=1+FRAMESIZE; i< inData.size()-LOOKAHEAD; i++){
            Double[] row = new Double[63];
            row[0] = Double.valueOf(i);

//            String date_time    = inData.get(i)[0] + " " +  inData.get(i)[1];
//            double openCur      = Double.parseDouble(inData.get(i)[2]);
//            double openPrev     = Double.parseDouble(inData.get(i-1)[2]);
//            double volCur       = Double.parseDouble(inData.get(i)[6]);
//            double volPrev      = Double.parseDouble(inData.get(i-1)[6]);
//            double closeFut     = Double.parseDouble(inData.get(i+LOOKAHEAD)[5]);
//
//            double deltaOpen    = percentDelta(openPrev, openCur);
//            double deltaVol     = percentDelta(volPrev, volCur);
//            double deltaFut     = percentDelta(openCur, closeFut);
//
//            row[1] = round(deltaOpen,5);
//            row[2] = round(deltaVol/10f,5);
//            row[3] = (deltaFut > 0)? 1d : 0d;

            // OPEN
            Double[] normOpens = getNormOpens(i);

            newData.add(row);
        }
        return newData;
    }


    private void createTrainingData(){
        //int size = (barSeries.getBarCount() - LOOKAHEAD) - FRAMESIZE;
        int size = barSeries.getBarCount() - LOOKAHEAD - FRAMESIZE;

        System.out.println("Size: " + size);

        for(int index = 0; index < size; index++){
            float[][] pricing = new float[FRAMESIZE][5];
            for(int i = 0; i < FRAMESIZE; i++){
                pricing[i][0] = barSeries.getBar(index + i).getOpen();
                pricing[i][1] = barSeries.getBar(index + i).getHigh();
                pricing[i][2] = barSeries.getBar(index + i).getLow();
                pricing[i][3] = barSeries.getBar(index + i).getClose();
                pricing[i][4] = barSeries.getBar(index + i).getVolume();
            }

            ans[index] = barSeries.getBar(index + FRAMESIZE + LOOKAHEAD - 1).getClose() > pricing[FRAMESIZE-1][3] ?
                    1 : 0;
            pricing = normalizePricingData(pricing);
            bigOHLCV[index] = pricing;
        }
    }

    private Double[] getNormOpens(int index){
        Double[] opens = new Double[FRAMESIZE];
        for(int i= index, k=0; i < index + FRAMESIZE; i++, k++){
            opens[k] = Double.parseDouble(inData.get(i)[2]);
        }
        return opens;
    }

    public static double percentDelta(double oldVal, double newVal){
        return (newVal - oldVal) / oldVal;
    }

    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public void printHead(){
        for(int i=0; i < 10; i++){
            System.out.println(Arrays.toString(outData.get(i)));
        }
    }

    /*
    Normalizes data to a range between 0.0001 and 1
     */
    public float[][] normalizePricingData(float[][] pricingIn){
        //  0     1     2    3      4
        //  Open  High  Low  Close  Volume
        float[][] pricingOut = pricingIn;
        float minPrice = pricingIn[0][2];
        float maxPrice = pricingIn[0][1];

        float minVol = pricingIn[0][4];
        float maxVol = pricingIn[0][4];

        // find min and max
        for(int i = 1; i < pricingIn.length; i++){
            minPrice = Math.min(pricingIn[i][2], minPrice);
            maxPrice = Math.max(pricingIn[i][1], maxPrice);

            minVol = Math.min(pricingIn[i][4], minVol);
            maxVol = Math.max(pricingIn[i][4], maxVol);
        }
        // a is lowest in range and b is highest in range
        // xNorm = a + ( ((x - xMin) * (b - a)) / (xMax - xMin) )
        final float a = 0.0001f; // must use non-zero value to avoid divide-by-zero errors in calculations.
        final float b = 1f;

        for(int i = 0; i < pricingIn.length; i++){
            for(int j = 0; j < 4; j++){
                float x = pricingIn[i][j];
                pricingOut[i][j] = a + ( ((x - minPrice) * (b - a)) / (maxPrice - minPrice) );
            }
            float x = pricingIn[i][4];
            pricingOut[i][4] = a + ( ((x - minVol) * (b - a)) / (maxVol - minVol) );
        }
        return pricingOut;
    }

}