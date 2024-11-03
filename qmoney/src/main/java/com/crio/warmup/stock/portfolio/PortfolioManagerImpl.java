
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.io.IOException;
import java.net.URISyntaxException;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuoteServiceFactory;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {



  private StockQuotesService stockQuotesService;

  
  private RestTemplate restTemplate ;
 
  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
    // this.restTemplate = stockQuotesService.;
  }
  
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
     
  }

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  








  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    //  return null;
    String url = buildUri(symbol, from, to);
    TiingoCandle[] tiingoCandles = restTemplate.getForObject(url, TiingoCandle[].class);

    if (tiingoCandles != null) {
      return Arrays.asList(tiingoCandles);
    } else {
      return Collections.emptyList();
    }
    
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
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




  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws StockQuoteServiceException  {

        long start = System.currentTimeMillis();
        List<AnnualizedReturn> annualizedReturns = new ArrayList<>();

    for (PortfolioTrade trade : portfolioTrades) {
      List<Candle> candles;
      try {
        candles = stockQuotesService.getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
      
      double buyPrice = candles.get(0).getOpen();
      double sellPrice = candles.get(candles.size() - 1).getClose();

      double totalReturn = (sellPrice - buyPrice) / buyPrice;
      // double years = trade.getPurchaseDate().until(endDate).getDays() / 365;
      double years = ChronoUnit.DAYS.between(trade.getPurchaseDate(),endDate) / 365d;

      double annualizedReturn = Math.pow(1 + totalReturn, 1.0 / years) - 1;

      AnnualizedReturn annualizedReturnObj = new AnnualizedReturn(
          trade.getSymbol(), annualizedReturn, totalReturn);
      annualizedReturns.add(annualizedReturnObj);
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    }
    long end = System.currentTimeMillis();
    // System.out.println("time taken "+ (end-start));

    annualizedReturns.sort(getComparator());
    return annualizedReturns; 
  }

public AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade,LocalDate endDate) throws StockQuoteServiceException{
  LocalDate startDate = trade.getPurchaseDate();
  String symbol = trade.getSymbol();
  Double buyPrice = 0.0, sellPrice = 0.0;

  try {
    LocalDate startLocalDate = trade.getPurchaseDate();

    List<Candle> stocksStartToEndFull = stockQuotesService.getStockQuote(symbol, startLocalDate, endDate);

    Collections.sort(stocksStartToEndFull,(candle1,candle2)->{
      return candle1.getDate().compareTo(candle2.getDate());
    });

    Candle stockStartDate = stocksStartToEndFull.get(0);
    Candle stockLatest = stocksStartToEndFull.get(stocksStartToEndFull.size()-1);

    buyPrice = stockStartDate.getOpen();
    sellPrice = stockLatest.getClose();
    endDate = stockLatest.getDate();

  } catch (JsonProcessingException e) {
    //TODO: handle exception
    throw new RuntimeException();
  }
  Double totalReturn = (sellPrice - buyPrice)/buyPrice;

  long daysBetweenPurchaseAndSelling = ChronoUnit.DAYS.between(startDate, endDate);

  Double totalYears =(double) (daysBetweenPurchaseAndSelling) / 365;

  Double annualizedReturn = Math.pow((1 + totalReturn), (1 / totalYears)) - 1;
   
  return new AnnualizedReturn(symbol, annualizedReturn, totalReturn);
}

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
      List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)
      throws InterruptedException, StockQuoteServiceException {
    // TODO Auto-generated method stub
    List<AnnualizedReturn> annualizedReturns = new ArrayList<AnnualizedReturn>();

    List<Future<AnnualizedReturn>> futureReturnsList = new ArrayList<Future<AnnualizedReturn>>();
    final ExecutorService pool = Executors.newFixedThreadPool(numThreads);

    for(int i=0;i<portfolioTrades.size();i++){
      PortfolioTrade trade =portfolioTrades.get(i);
      Callable<AnnualizedReturn> callableTask = () -> {
        return getAnnualizedReturn(trade, endDate);
      };
      Future<AnnualizedReturn> futureReturns = pool.submit(callableTask);
      futureReturnsList.add(futureReturns);

      
      // AnnualizedReturn Return = getAnnualizedReturn(trade, endDate);
      //   annualizedReturns.add(Return);
    }
    // pool.shutdown();
    // pool.awaitTermination(200, TimeUnit.MILLISECONDS);

    for(int i=0;i<portfolioTrades.size();i++){
      Future<AnnualizedReturn> futureReturns = futureReturnsList.get(i);
      try{
        AnnualizedReturn returns = futureReturns.get();
        annualizedReturns.add(returns);
      }catch(ExecutionException e){
        throw new StockQuoteServiceException("Error when calling the API",e);
      }
    }

    // Collections.sort(annualizedReturns, Collections.reverseOrder());
    annualizedReturns.sort(getComparator());


    return annualizedReturns;
  }




  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
