/*
 * Created on Nov 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package stockinformationbroker;

import java.util.*;
/**
 * @author klaus
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StockInformationBroker {
  private StockInformationBroker() {}
  private static StockInformationBroker instance = new StockInformationBroker();
  
  
  static public StockInformationBroker getInstance() {
  	return instance;	
  }
  private Hashtable stocksDB = new Hashtable();
  {
  	stocksDB.put("Siemens", new Float(98.23));
  	stocksDB.put("IBM", new Float(98.73));
  	stocksDB.put("Microsoft", new Float(0.23));
	stocksDB.put("SAP", new Float(76.89));
	stocksDB.put("BMW", new Float(33.52));
  }
  public Float getStockQuote(String stock) {
  	return (Float) stocksDB.get(stock);
  }
  
  public StockInfo collectInfo(StockInfoRequest request) {
  	String stocks[] = request.getStocks();
  	StockInfo info = new StockInfo();
  	for (int i=0; i<stocks.length; i++) {
  		info.addQuote(stocks[i], getStockQuote(stocks[i]));
  	}
  	
  	return info;
  }
  
  
	
}
