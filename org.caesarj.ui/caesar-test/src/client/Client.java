/*
 * Created on Nov 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package client;
import stockinformationbroker.*;
import java.util.*;
import stockpricing.*;
import pricing.*;
import org.caesarj.runtime.*;
/**
 * @author klaus
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Client {
	private String name;
	public Client(String name) {
		this.name=name;
	}
	public String getName() { 
		return name;
	}
	public void run(String stocks[]) {
		StockInfoRequest request = new StockInfoRequest(stocks);
		StockInfo si = StockInformationBroker.getInstance().collectInfo(request);
		for (int i =0; i<stocks.length; i++) {
			System.out.println(name+":"+stocks[i]+"--"+si.getQuote(stocks[i]));
		}
	}

	public static void main(String[] args) {
	  Client egon = new Client("Egon");
	  Client mira = new Client("Mira");
	  Client christa = new Client("Christa");
	  Client klaus = new Client("Klaus");

	  egon.run(new String[]{"IBM", "Siemens"});
	  mira.run(new String[]{"Microsoft"});
	  mira.run(new String[]{"BMW"});
	  klaus.run(new String[]{"IBM", "SAP"});
	  egon.run(new String[]{"SAP", "Microsoft", "BMW"});
 	  egon.run(new String[]{"SAP", "Microsoft"});
	  mira.run(new String[]{"IBM"});
	  klaus.run(new String[]{"BMW", "Microsoft"}); 	  klaus.run(new String[]{"IBM"});
	  christa.run(new String[]{"IBM","Siemens"});

	}
}
