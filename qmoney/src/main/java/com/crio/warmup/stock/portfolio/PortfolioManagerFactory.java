
package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.quotes.StockQuoteServiceFactory;
import com.crio.warmup.stock.quotes.StockQuotesService;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerFactory {

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Implement the method to return new instance of PortfolioManager.
  //  Remember, pass along the RestTemplate argument that is provided to the new instance.

  public static PortfolioManager getPortfolioManager(RestTemplate restTemplate) {

     return new PortfolioManagerImpl(restTemplate);
  }





  // public static PortfolioManager getPortfolioManager(RestTemplate restTemplate) {

  // }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the method to return new instance of PortfolioManager.
  //  Steps:
  //    1. Create appropriate instance of StoockQuoteService using StockQuoteServiceFactory and then
  //       use the same instance of StockQuoteService to create the instance of PortfolioManager.
  //    2. Mark the earlier constructor of PortfolioManager as @Deprecated.
  //    3. Make sure all of the tests pass by using the gradle command below:
  //       ./gradlew test --tests PortfolioManagerFactory


   public static PortfolioManager getPortfolioManager(String provider,
     RestTemplate restTemplate) {
      try {
         if (provider == null) {
             throw new IllegalArgumentException("Provider cannot be null");
         }
 
         StockQuotesService stockQuotesService = StockQuoteServiceFactory.INSTANCE.getService(provider.toLowerCase(), restTemplate);
 
         if (stockQuotesService == null) {
             throw new IllegalStateException("StockQuotesService is null for provider: " + provider);
         }
 
         return new PortfolioManagerImpl(stockQuotesService);
     } catch (Exception e) {
         // Log the exception for debugging purposes
         e.printStackTrace();
         // Rethrow the exception or handle it as needed
         throw new RuntimeException("Error creating PortfolioManager", e);
     }
   }

}
