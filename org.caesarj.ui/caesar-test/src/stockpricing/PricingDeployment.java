/*
 * Created on Nov 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package stockpricing;
import java.util.HashMap;
import java.util.Map;

import client.*;

/**
 * @author klaus
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public deployed class PricingDeployment {
  /*
  	static Map map = new HashMap(); 
  
	static {
		map.put("Klaus", new PerRequestDiscountPricing());
		map.put("Egon", new PerStockQuoteDiscountPricing());
		map.put("Mira", new PerRequestRegularPricing());
	}
  */
   void around(Client c) :  ( execution( void Client.run(String []) ) && this(c)) {
/*
	Object pricing = map.get(c.getName());
	
	if(pricing!=null) {
		deploy(pricing) {
			proceed(c);
		}
	}
	else {
		proceed(c);
	}
*/

  	if (c.getName().equals("Klaus")) {
  		deploy(discount) {
  			proceed(c);
  		}
  	} else if (c.getName().equals("Egon")) {
  		deploy(sqdiscount) {
  			proceed(c);
  		}
  	} else if (c.getName().equals("Mira")) {
  		deploy(regular) { 
  			proceed(c);
  		}
  	} else proceed(c);

  }
  
  private PerRequestDiscountPricing discount = new PerRequestDiscountPricing();
  private PerStockQuoteDiscountPricing sqdiscount = new PerStockQuoteDiscountPricing();  
  private PerRequestRegularPricing regular = new PerRequestRegularPricing();
  
}
