/*
 * Created on Nov 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package stockpricing;
import stockinformationbroker.*;
import pricing.*;
import client.*;

/**
 * @author klaus
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PerStockQuoteBinding binds Pricing {
  public class ClientCustomer binds Customer wraps Client {
	public String name() { 
		return wrappee.getName();
	}
  }
  public class StockItem binds Item wraps String {

	public float price() { 
		return 2;
	}
  }
  after(Client c, String stock):
    (cflow(call(void Client.run(String[])) && target(c))) && 
	( call(Float getStockQuote(String)) && args(stock)) {
		ClientCustomer(c).charge(StockItem(stock));
	}
}
