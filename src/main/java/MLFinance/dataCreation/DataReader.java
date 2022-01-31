package main.java.MLFinance.dataCreation;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class DataReader {

    public static List<String[]> load_csv(String fileName) throws Exception{
        InputStream in = DataReader.class.getResourceAsStream("/"+fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        //System.out.println(Paths.get(System.getProperty("user.dir")));
        //Reader reader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")));
        return readLines(reader);
    }


    // DEPRECATED
    public static List<String[]> readAll(Reader reader) throws Exception {
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> list = new ArrayList<>();
        list = csvReader.readAll();
        reader.close();
        csvReader.close();
        return list;
    }

    /*
    Iterates over the CSV file line-by-line to put the pricing data into
    an Arraylist for use by the DataframeCreator.
    [DATE, TIME, OPEN, HIGH, LOW, CLOSE, VOLUME]
     */
    public static List<String[]> readLines(Reader reader) throws Exception {
        List<String[]> list = new ArrayList<>();
        CSVReader csvReader = new CSVReader(reader);

        String[] line;
        while ((line = csvReader.readNext()) != null) {
            list.add(line);
        }

        reader.close();
        csvReader.close();
        System.out.println("Read " + list.size() + " lines.");
        return list;
    }

    /*
    Prints the first 10 entries.
     */
    public static void printHead(List<String[]> in){
        System.out.println("Printing Head...");
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < in.get(i).length; j++){
                System.out.print(in.get(i)[j] + " ");
            }
            System.out.println();
        }
    }

    /*
    Prints the first 10 entries in a list containing Doubles
     */
    public static void printHeadDouble(List<Double[]> in){
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < in.get(i).length; j++){
                System.out.print(in.get(i)[j] + " ");
            }
            System.out.println();
        }
    }

    public static void printHeadFloat(float[][] in){
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < in[i].length; j++){
                System.out.print(in[i][j] + " ");
            }
            System.out.println();
        }
    }
}
