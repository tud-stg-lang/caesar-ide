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
public class RegularPricing provides Pricing {
  public class Customer {
  	private float balance; 
	public void charge(Item it) {
	  float diff = it.price();
	  balance -= diff;
	  System.out.println("RegularPricing: charged "+diff+" to customer "+name()+", balance is: "+balance);
	}
	public float balance() {
		return balance;
	}
	
  }
}
