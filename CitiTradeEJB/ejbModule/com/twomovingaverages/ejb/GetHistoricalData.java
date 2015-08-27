package com.twomovingaverages.ejb;
 
import java.net.*;
import java.util.ArrayList;
import java.io.*;
 
public class GetHistoricalData {
 
        private ArrayList dateStrList = new ArrayList();
        private ArrayList openList = new ArrayList();
        private ArrayList lowList = new ArrayList();
        private ArrayList highList = new ArrayList();
        private ArrayList closeList = new ArrayList();
        private ArrayList volumeList = new ArrayList();
        private ArrayList adjCloseList = new ArrayList();
 
        private URL url = null;
        private URLConnection urlConn = null;
        private InputStreamReader  inStream = null;
 
         GetHistoricalData(String urlStr){
          try {
            //Open Connection the Yahoo Finance URL
            this.url  = new URL(urlStr);
            this.urlConn = url.openConnection();
 
            //Start Reading
            this.urlConn = this.url.openConnection();
            this.inStream = new InputStreamReader(this.urlConn.getInputStream());
            BufferedReader buff= new BufferedReader(this.inStream);
            String stringLine;
 
            buff.readLine(); //Read the firstLine. This is the header.
 
                while((stringLine = buff.readLine()) != null) //While not in the header
                {
 
                      String [] dohlcav = stringLine.split("\\,"); //date, ohlc, adjustedclose
 
                        String date = dohlcav[0];
                        double open = Double.parseDouble(dohlcav[1]);
                        double high = Double.parseDouble(dohlcav[2]);
                        double low = Double.parseDouble(dohlcav[3]);
                        double close = Double.parseDouble(dohlcav[4]);
                        long volume = Long.parseLong(dohlcav[5]);
                        double adjClose = Double.parseDouble(dohlcav[6]);
 
                        //Set the Data
                        dateStrList.add(date);
                        openList.add(open);
                        highList.add(high);
                        lowList.add(low);
                        closeList.add(close);
                        volumeList.add(volume);
                        adjCloseList.add(adjClose);
                }
 
            }catch (MalformedURLException e) {
                System.out.println(e.getMessage());
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
       }
 
         //return the data
         public ArrayList getDate(){
             return dateStrList;
         }
         public ArrayList getOpen(){
             return openList;
         }
         public ArrayList getHigh(){
             return highList;
         }
         public ArrayList getLow(){
             return lowList;
         }
         public ArrayList getClose(){
             return closeList;
         }
         public ArrayList getAdjClose(){
             return adjCloseList;
         }
         public ArrayList getVolume(){
             return volumeList;
         }
 
}