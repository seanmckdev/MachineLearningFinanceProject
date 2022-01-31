package main.java.MLFinance;

import main.java.MLFinance.dataCreation.DataReader;
import main.java.MLFinance.dataCreation.DataframeCreator;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String[]> dataframe = new ArrayList<>();

        try {
            dataframe = DataReader.load_csv("USDJPY.csv");
        } catch (Exception e) {
            System.out.println("DID NOT READ!");
            e.printStackTrace();
        }

        DataReader.printHead(dataframe);
        DataframeCreator dfc = new DataframeCreator(dataframe);
        //dfc.printHead();
        //DataReader.printHeadDouble(dfc.rawOutData);

    }
}
