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

public collaboration interface Pricing {
   public interface Customer {
   	 public provided void charge(Item it);
   	 public provided float balance();
   	 public expected String name();
   }
   public interface Item {
 	 public expected float price();
   }

}
