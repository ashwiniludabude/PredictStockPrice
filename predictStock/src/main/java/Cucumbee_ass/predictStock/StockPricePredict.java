package Cucumbee_ass.predictStock;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StockPricePredict {
		    public static void main(String[] args) {
		        String exchangeFolder = "C:\\Users\\ashwini\\Downloads\\(TC1)(TC2) stock_price_data_files\\(TC1)(TC2) stock_price_data_files\\LSE"; 
		        int numFiles = 2; // Number of files 
		        processExchange(exchangeFolder, numFiles);
		    }
		    public static void processExchange(String exchangeFolder, int numFiles) {
		        File folder = new File(exchangeFolder);	        
		        if (!folder.exists() || !folder.isDirectory()) {
		            System.out.println("Directory does not exist or is not a directory");
		            return;
		        }
		        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));
		        System.out.println("Checking the existence of files in: " + exchangeFolder);
		        if (files == null || files.length == 0) {
		            System.out.println("No .csv files found in the directory");
		            return;
		        }
		        System.out.println("Files found at location: " + Arrays.toString(files));
		        List<File> selectedFiles = selectFiles(files, numFiles);
		        for (File file : selectedFiles) {
		            try {
		                List<DataPoint> dataPoints = getDataPoints(file);
		                double[] nextPrices = predictNextValues(dataPoints);
		                savePredictions(file, dataPoints, nextPrices);
		            } catch (IOException e) {
		                System.err.println("Error in processing this file: " + file.getName());
		                e.printStackTrace();
		            }
		        }
		    }
		    public static List<File> selectFiles(File[] files, int numFiles) {
		        List<File> selectedFiles = new ArrayList<>();
		        Random rand = new Random();
		        Set<Integer> selectedIndices = new HashSet<>();
		        while (selectedFiles.size() < Math.min(numFiles, files.length)) {
		            int index = rand.nextInt(files.length);
		            if (!selectedIndices.contains(index)) {
		                selectedFiles.add(files[index]);
		                selectedIndices.add(index);
		            }
		        }
		        return selectedFiles;
		    }
		    public static List<DataPoint> getDataPoints(File file) throws IOException {
		        List<DataPoint> dataPoints = new ArrayList<>();
		        try (Reader reader = Files.newBufferedReader(file.toPath());
		             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) { 
		            // Iterating through the records
		            for (CSVRecord record : csvParser) {
		                String stockId = record.get(0); // First column
		                String timestamp = record.get(1); // Second column
		                double price = Double.parseDouble(record.get(2)); // Third column
		                dataPoints.add(new DataPoint(stockId, timestamp, price));
		            }
		        }
		        Random rand = new Random();
		        int startIndex = rand.nextInt(Math.max(0, dataPoints.size() - 10)); 
		        return dataPoints.subList(startIndex, startIndex + 10);
		    }
		    public static double[] predictNextValues(List<DataPoint> dataPoints) {
		        double[] lastPrices = dataPoints.stream().mapToDouble(dp -> dp.price).toArray();
		        Arrays.sort(lastPrices);
		        double secondHighest = lastPrices[lastPrices.length - 2];
		        double nextPrice1 = secondHighest;
		        double difference1 = lastPrices[lastPrices.length - 1] - nextPrice1;
		        double nextPrice2 = nextPrice1 + (difference1 / 2);
		        double difference2 = nextPrice2 - nextPrice1;
		        double nextPrice3 = nextPrice2 + (difference2 / 4);
		        return new double[]{nextPrice1, nextPrice2, nextPrice3};
		    }
		    public static void savePredictions(File file, List<DataPoint> dataPoints, double[] nextPrices) throws IOException {
		        String outputFilePath = file.getAbsolutePath().replace(".csv", "_predictions.csv");
		        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
		            writer.write("Stock-ID,Timestamp,Stock Price\n");
		            for (DataPoint dp : dataPoints) {
		                writer.write(String.format("%s,%s,%.2f\n", dp.stockId, dp.timestamp, dp.price));
		            }
		            // To get the last timestamp and increment it by one day
		            String lastTimestamp = dataPoints.get(dataPoints.size() - 1).timestamp;
		            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); 
		            LocalDate lastDate = LocalDate.parse(lastTimestamp, formatter);
		            for (int i = 0; i < nextPrices.length; i++) {		            	
		            	LocalDate predictedTimestamp = lastDate.plusDays(i + 1);
		                writer.write(String.format("%s,%s,%.2f\n", dataPoints.get(0).stockId, predictedTimestamp.format(formatter), nextPrices[i]));
		            }
		        }
		    }
		    static class DataPoint {
		        String stockId;
		        String timestamp;
		        double price;
		        DataPoint(String stockId, String timestamp, double price) {
		            this.stockId = stockId;
		            this.timestamp = timestamp;
		            this.price = price;
		        }
		    }
}