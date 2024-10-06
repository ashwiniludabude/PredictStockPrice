# PredictStockPrice

## Overview

The Stock Price Predictor is a Java application that predicts future stock prices based on historical data stored in CSV files.The program processes CSV files without headers, retrieves 10 data points from random timestamps, and predicts the next three stock prices using a simple prediction algorithm.

## Features

- Random selection of data points for prediction.
- Predicts the next three stock prices based on historical data.
- Outputs results to new CSV files

### Prerequisites

- **Java 11** or higher
- **Apache Commons CSV** library

### Executable file
https://github.com/ashwiniludabude/PredictStockPrice/blob/main/predictStock/src/main/java/Cucumbee_ass/predictStock/StockPricePredict.java

### Output Format
The program will generate output CSV files with the same name as the input files, suffixed with _predictions, containing the original data points along with the predicted prices.

### Access Information
The CSV files are formatted correctly with three columns: Stock-ID, Timestamp, and Stock Price.
The output files will be created in the same directory as the input CSV files.

### Language Versions
Java: 11 or higher
