
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import org.jfree.data.category.DefaultCategoryDataset;

public class DataOperations {
    // create class variables
    static URL csvFileURL;
    static URLConnection conn;
    static BufferedReader br = null;
    static String nextLine = "";
    static String cvsSplitBy = ",";
    static String url;
    static InputStreamReader inputStream;

    // this method will retrieve a csv file containing the stock information, and return this data as a dataset readable by the linechart
    public static DefaultCategoryDataset createDataSet(String stockSymbol, String[] startDateArr, String[] endDateArr) {
        // create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ArrayList<String> stockQuotes = new ArrayList<String>();

        // minus 1 from the month as per the API get request requirements
        int endMonth = Integer.parseInt(endDateArr[0]);
        endMonth--;
        int startMonth = Integer.parseInt(startDateArr[0]);
        startMonth--;

        // build the dynamic URL
        String quotes = "http://ichart.finance.yahoo.com/table.csv?s=" + stockSymbol + "&d=" + endMonth + "&e="
                + endDateArr[1] + "&f=" + endDateArr[2] + "&g=d&a=" + startMonth + "&b=" + startDateArr[1] + "&c="
                + startDateArr[2] + "&ignore=.csv";
        System.out.println(quotes); // print the URL to the console

        try {
            csvFileURL = new URL(quotes); // URL object
            conn = csvFileURL.openConnection(); // open a connection
            inputStream = new InputStreamReader(conn.getInputStream()); // create the input stream 
            BufferedReader buff = new BufferedReader(inputStream); // create a buffered reader to read the data recieved from the csv file
            
            while ((nextLine = buff.readLine()) != null) { // read csv file and add data to the stockQuotes arrayList
                stockQuotes.add(nextLine);
            }
        } catch (FileNotFoundException e) { // handle possible checked exceptions
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally { // finally block
            if (br != null) {
                try {
                    inputStream.close(); // close open resources
                    br.close();  
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        stockQuotes.remove(0); // remove the row name String that came from the csv file        
        Collections.sort(stockQuotes); // sort the ArrayList so the data is shown in the correct order on the linechart
        
        // FYI - The Order of Stock info in each String seperated by a comma is as follows :  Date,Open,High,Low,Close,Volume,Adj Close 
        // break up the String ArrayList into seperate values and add them individually to the dataset
        for (String s : stockQuotes) {
            System.out.println(s); // print the stock quote String to the console
            String[] tempArr = s.split(","); // break up the String into a String array with each piece of info given its own index 
            dataset.addValue(Double.parseDouble(tempArr[4]), "Stock Price", tempArr[0]); // add the required stock info to the dataset
        }
        return dataset; // return the dataset
    }
}
