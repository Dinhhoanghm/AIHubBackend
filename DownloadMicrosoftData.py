# Install required packages if needed (you can run this separately first)
# pip install azure-storage-blob azure-identity azureml-opendatasets pandas matplotlib

# Import required libraries
from azureml.opendatasets import NycTlcGreen
import pandas as pd
from datetime import datetime
from dateutil import parser
import matplotlib.pyplot as plt

def main():
  # Setting a date range for the data
  end_date = parser.parse('2018-06-06')
  start_date = parser.parse('2018-05-01')

  print("Loading NYC TLC Green Taxi data...")
  # Loading the NYC TLC Green Taxi data for the specified date range
  nyc_tlc = NycTlcGreen(start_date=start_date, end_date=end_date)

  # Convert to pandas DataFrame (better for regular Python scripts)
  nyc_tlc_df = nyc_tlc.to_pandas_dataframe()

  # Display top 5 rows
  print("\nFirst 5 rows:")
  print(nyc_tlc_df.head(5))

  # Display data statistics
  print("\nData statistics:")
  print(nyc_tlc_df.describe())

  # Save the data to a CSV file
  print("\nSaving data to CSV file...")
  nyc_tlc_df.to_csv("nyc_green_taxi_data.csv", index=False)
  print("Data saved to nyc_green_taxi_data.csv")

if __name__ == "__main__":
  main()