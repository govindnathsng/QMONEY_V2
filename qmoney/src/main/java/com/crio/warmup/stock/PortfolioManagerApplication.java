
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {

    //  return Collections.emptyList();
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] pTrades = objectMapper.readValue(file, PortfolioTrade[].class);
    List<String> list =new ArrayList<>(); 
    for(PortfolioTrade pTrade : pTrades){
      list.add(pTrade.getSymbol());
    }
    return list;
  }
  // private static void printJsonObject(Object object) throws IOException {
  //   Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
  //   ObjectMapper mapper = new ObjectMapper();
  //   logger.info(mapper.writeValueAsString(object));
  // }



  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }



  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>
  public static class ClosingpriceComparator implements Comparator<TotalReturnsDto> {
    @Override
    public int compare(TotalReturnsDto t1, TotalReturnsDto t2){
      if(t1.getClosingPrice()>t2.getClosingPrice()) return 1;
      else if(t1.getClosingPrice()<t2.getClosingPrice()) return -1;
      return 0;
    }
  }



  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
      
        List<PortfolioTrade> trades = new ArrayList<PortfolioTrade>();
        trades= readTradesFromJson(args[0]);
        LocalDate endDate = LocalDate.parse(args[1]);
        String token = getToken();

        List<TotalReturnsDto> totalReturnsDtos = new ArrayList<>();
        for (PortfolioTrade trade : trades) {
            String url = prepareUrl(trade, endDate, token);
            RestTemplate restTemplate = new RestTemplate();
            TiingoCandle[] tiingoCandle = restTemplate.getForObject(url, TiingoCandle[].class);
            
            double closing_price= getClosingPrice(tiingoCandle); 
            totalReturnsDtos.add(new TotalReturnsDto(trade.getSymbol(), closing_price));
          }

        Collections.sort(totalReturnsDtos,new ClosingpriceComparator());

        List <String> list = new ArrayList<String>();
        for(TotalReturnsDto trd : totalReturnsDtos){
          list.add(trd.getSymbol());
        }
        return list;
    //  return Collections.emptyList();

  }


  private static double getClosingPrice(TiingoCandle[] tiingoCandle) {
    double closingPrice = 0.0; 

    // if (tiingoCandle.length > 0) {
        closingPrice = tiingoCandle[tiingoCandle.length - 1].getClose();
    // }

    return closingPrice;
  }


  
  public static String getToken(){
    return "9224898acb7286481dd78d82f67322650484b3c3";
  }



  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    //  return Collections.emptyList();
     File file = resolveFileFromResources(filename);
        ObjectMapper objectMapper = getObjectMapper();
        return Arrays.asList(objectMapper.readValue(file, PortfolioTrade[].class));
    
  }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    // String startDate = endDate.minusYears(1).toString();
        return "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?startDate=" +
                trade.getPurchaseDate() + "&endDate=" + endDate + "&token=" + token;
    //  return Collections.emptyList();

  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.







  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest


  static Double getOpeningPriceOnStartDate(List<Candle> candles) {

     return candles.get(0).getOpen();
  }




  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
     return candles.get(candles.size()-1).getClose();
  }




  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    String url = prepareUrl(trade, endDate, token);
    RestTemplate restTemplate = new RestTemplate();
    TiingoCandle[] tiingoCandle = restTemplate.getForObject(url, TiingoCandle[].class);
     return Arrays.asList(tiingoCandle);
  }



  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
    //  return Collections.emptyList();
    List<PortfolioTrade> trades = readTradesFromJson(args[0]);
    LocalDate endDate = LocalDate.parse(args[1]);
    String token = getToken();

    List<AnnualizedReturn> annualizedReturns = new ArrayList<>();

    for (PortfolioTrade trade : trades) {
      List<Candle> candles = fetchCandles(trade, endDate, token);

      
        Double buyPrice = getOpeningPriceOnStartDate(candles);
        Double sellPrice = getClosingPriceOnEndDate(candles);

        AnnualizedReturn annualizedReturn = calculateAnnualizedReturns(endDate, trade, buyPrice, sellPrice);
        annualizedReturns.add(annualizedReturn);
      
    }

    Collections.sort(annualizedReturns, Comparator.comparingDouble(AnnualizedReturn::getAnnualizedReturn).reversed());
    return annualizedReturns;
  }



  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        // ChronoUnit.DAYS.between() return long.
        // Long j = 0.25; //you can uncomment and check this as long only takes whole number and rounds up 
      double totalReturn = (sellPrice - buyPrice) / buyPrice;
         
      double years = ChronoUnit.DAYS.between(trade.getPurchaseDate(),endDate) / 365d;
      double annualized_returns = Math.pow((1 + totalReturn), (1.0 / years)) - 1;
      return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturn);
  }
 public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/govindnathsng-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@1573f9fc";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplication.mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "29";


   return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
       toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
       lineNumberFromTestFileInStackTrace});
 }




  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
      //  String contents = readFileAsString(file);
      //  ObjectMapper objectMapper = getObjectMapper();
       
      // return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);

    // Read the trades from the JSON file
    List<PortfolioTrade> portfolioTrades = readTradesFromJson(file);

    // Create an instance of PortfolioManager using PortfolioManagerFactory
    PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(new RestTemplate());

    // Calculate the annualized returns using the PortfolioManager
    return portfolioManager.calculateAnnualizedReturn(portfolioTrades, endDate);
}




  private static void printJsonObject(List<AnnualizedReturn> mainCalculateReturnsAfterRefactor) throws JsonProcessingException {
    ObjectMapper objectMapper = getObjectMapper();
    String json = objectMapper.writeValueAsString(mainCalculateReturnsAfterRefactor);
    System.out.println(json);

  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  
  }
}

