/*
 * Created on Nov 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package stockinformationbroker;

/**
 * @author klaus
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StockInfoRequest {
	private String stocks[];
    public StockInfoRequest(String stocks[]) {
  	  this.stocks = stocks;
    }
    public String[] getStocks() { return stocks;}
}
