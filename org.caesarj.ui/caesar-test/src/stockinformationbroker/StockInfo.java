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
public class StockInfo {
  private Hashtable infos = new Hashtable();
  public void addQuote(String stock, Float f) {
  	infos.put(stock,f);
  }
  public float getQuote(String stock) {
  	Float f = (Float) infos.get(stock);
  	return f.floatValue();
  }
}
