/*
 * Created on Nov 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package pricing;
 
/**
 * @author klaus
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DiscountPricing provides Pricing {
  public class Customer {
	private float balance; 
	private int discountstate = 0;
	public void charge(Item it) {
	  if (discountstate++ == 2) {
	  	discountstate = 0;
		System.out.println("DiscountPricing: 3for2 discount - charged nothing to customer "+name()+", balance is still: "+balance);
		return;
	  }
	  float diff = it.price();
	  balance -= diff;
	  System.out.println("DiscountPricing: charged "+diff+" to customer "+name()+", balance is: "+balance);
	}
	public float balance() {
		return balance;
	}
  }
}