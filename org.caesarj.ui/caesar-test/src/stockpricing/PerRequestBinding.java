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
public class PerRequestBinding binds Pricing {
  public class ClientCustomer binds Customer wraps Client {
    public String name() { 
    	return wrappee.getName();
    }
  }
  public class RequestItem binds Item wraps StockInfoRequest {
	public float price() { 
		return 5;
	}
  }
  after(Client c, StockInfoRequest request):
    ( call(StockInfo collectInfo(StockInfoRequest)) && this(c) && args(request)) {
    	ClientCustomer(c).charge(RequestItem(request));
    }
}
