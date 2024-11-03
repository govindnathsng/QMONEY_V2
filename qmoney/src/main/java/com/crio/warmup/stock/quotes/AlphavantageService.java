
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.text.SimpleDateFormat;
import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  IMP: Do remember to write readable and maintainable code, There will be few functions like
  //    Checking if given date falls within provided date range, etc.
  //    Make sure that you write Unit tests for all such functions.
  //  Note:
  //  1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.
  private RestTemplate restTemplate;


  protected AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    // this.restTemplate = new RestTemplate();
  }
  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException ,StockQuoteServiceException,RuntimeException{
    // TODO Auto-generated method stub
    // return null; 
    List<Candle> candlesList = new ArrayList<>();
    try {
      
    String url = buildUri(symbol);
    // AlphavantageDailyResponse[] alphaCandles = restTemplate.getForObject(url, AlphavantageDailyResponse[].class);
    String jsonResponse = restTemplate.getForObject(url, String.class);
    // System.out.println("JSONResponce"+jsonResponse);
    ObjectMapper objectMapper = new ObjectMapper();
       
    objectMapper.registerModule(new JavaTimeModule());

    AlphavantageDailyResponse response = objectMapper.readValue(jsonResponse, AlphavantageDailyResponse.class);
    // System.out.println("response"+response);

    
        Map<LocalDate, AlphavantageCandle> candlesMap = response.getCandles();
        
        
        for (Map.Entry<LocalDate, AlphavantageCandle> entry : candlesMap.entrySet()) {
            AlphavantageCandle alphavantageCandle = entry.getValue();
            // System.out.println(entry.getValue().getOpen());
            alphavantageCandle.setDate(entry.getKey()); // Set the date using the map key
            // System.out.println(entry.getKey());
            if(alphavantageCandle.getDate().isAfter(from) && alphavantageCandle.getDate().isBefore(to)){
            candlesList.add(alphavantageCandle);
            }
            else if (alphavantageCandle.getDate().isEqual(from) || alphavantageCandle.getDate().isEqual(to)) {
              candlesList.add(alphavantageCandle);
          }
        }
        Comparator<Candle> dateComparator = (candle1, candle2) -> {
          
              LocalDate d1 = candle1.getDate();
              LocalDate d2 = candle2.getDate();
              // Double d1 = candle1.getOpen();
              // Double d2 = candle2.getOpen();
              return d1.compareTo(d2);
      };

        Collections.sort(candlesList ,dateComparator);
        
        // System.out.println(candlesList.get(0).getOpen());
      } catch (NullPointerException e) {
        throw new StockQuoteServiceException("Alphavantage returned invalid Response",e);
      }
        return candlesList;
   
  }
      
    //     AlphavantageDailyResponse[] alphaCandles = objectMapper.readValue(jsonResponse, AlphavantageDailyResponse[].class);
    
    // List<Candle> candlesList = new ArrayList<>();
 
    // if (alphaCandles != null) {
    //   for (AlphavantageDailyResponse response : alphaCandles) {
    //     Map<LocalDate, AlphavantageCandle> candlesMap = response.getCandles();
  
  //       for (Map.Entry<LocalDate, AlphavantageCandle> entry : candlesMap.entrySet()) {
  //         AlphavantageCandle candle = entry.getValue();
  //         candle.setDate(entry.getKey()); // Set the date using the map key
  //         candlesList.add(candle); // Add the modified candle to the list
  //       }
  //     }
  //   }
  
  //   return candlesList;
    
  // }

  protected String buildUri(String symbol) {
    String BASE_URL = "https://www.alphavantage.co/query";
    String FUNCTION_PARAM = "TIME_SERIES_DAILY";
    String API_KEY = "LFSDZ8TBZLEW92B1";
    
    String completeUrl = String.format(
                "%s?function=%s&symbol=%s&apikey=%s",
                BASE_URL, FUNCTION_PARAM, symbol, API_KEY);

        return completeUrl;
    }
}

