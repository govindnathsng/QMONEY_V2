
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {


  private RestTemplate restTemplate;


  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException ,StockQuoteServiceException,RuntimeException{
    // TODO Auto-generated method stub
    // return null;
    String url = buildUrl(symbol, from, to);
    List<Candle> stockStartToEndDate = new ArrayList<>();

    if(from.compareTo(to)>= 0){
      throw new RuntimeException();
    }

    
    // TiingoCandle[] tiingoCandles = restTemplate.getForObject(url, TiingoCandle[].class);
    // RestTemplate restTemplate = new RestTemplate();
    try {
        String jsonResponse = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); 
      
        TiingoCandle[] tiingoCandles = objectMapper.readValue(jsonResponse, TiingoCandle[].class);
        stockStartToEndDate = Arrays.asList(tiingoCandles);
        } 
        catch (NullPointerException e) {
          throw new StockQuoteServiceException("Error occuerd when requesting responce from Tiingo API",e.getCause());
        }
    // if (tiingoCandles != null) {
    //   return Arrays.asList(tiingoCandles);
    // } 
    // else {
    //   return Collections.emptyList();
    // }
    return stockStartToEndDate;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.
  protected String buildUrl(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
         + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
         String apiKey = "9224898acb7286481dd78d82f67322650484b3c3";
         String url = uriTemplate
             .replace("$SYMBOL", symbol)
             .replace("$STARTDATE", startDate.toString())
             .replace("$ENDDATE", endDate.toString())
             .replace("$APIKEY", apiKey);
     
         return url;
}




  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}
