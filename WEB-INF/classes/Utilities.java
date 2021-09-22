import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

@WebServlet("/Utilities")

/* 
	Utilities class contains class variables of type HttpServletRequest, PrintWriter,String and HttpSession.

	Utilities class has a constructor with  HttpServletRequest, PrintWriter variables.
	  
*/

public class Utilities extends HttpServlet{
	HttpServletRequest req;
	PrintWriter pw;
	String url;
	HttpSession session; 
	public Utilities(HttpServletRequest req, PrintWriter pw) {
		this.req = req;
		this.pw = pw;
		this.url = this.getFullURL();
		this.session = req.getSession(true);
	}



	/*  Printhtml Function gets the html file name as function Argument, 
		If the html file name is Header.html then It gets Username from session variables.
		Account ,Cart Information ang Logout Options are Displayed*/

	public void printHtml(String file) {
		String result = HtmlToString(file);
		//to print the right navigation in header of username cart and logout etc
		if (file == "Header.html") {
			result=result+"<div id='menu' style='float: right;'><ul>";
			if (session.getAttribute("username")!=null){
				String username = session.getAttribute("username").toString();
				username = Character.toUpperCase(username.charAt(0)) + username.substring(1);
				String usertype = session.getAttribute("usertype").toString();
				System.out.print(usertype);
				switch (usertype){
					case "customer": 
						result = result + "<li><a href='ViewOrder'><span class='glyphicon'>ViewOrder</span></a></li>"
						+ "<li><a><span class='glyphicon'>Hello,"+username+"</span></a></li>"
						+ "<li><a href='Account'><span class='glyphicon'>Account</span></a></li>"
						+ "<li><a href='Logout'><span class='glyphicon'>Logout</span></a></li>"
						+ "<li><a href='Cart'><span class='glyphicon'>Cart("+CartCount()+")</span></a></li></ul></div></div>";
					break;

					case "manager": 
						result = result + "<li><a href='Account'><span class='glyphicon'>Account</span></a></li>"
						+ "<li><a href='Registration'><span class='glyphicon'>AddUser</span></a></li>"
						+ "<li><a href='ManageProducts'><span class='glyphicon'>ManageProducts</span></a></li>"
						+ "<li><a href='Logout'><span class='glyphicon'>Logout</span></a></li>"
						+ "<li><a href='Cart'><span class='glyphicon'>Cart("+CartCount()+")</span></a></li></ul></div></div>";
					break;

					case "retailer": 
						result = result + "<li><a href='ViewOrder'><span class='glyphicon'>ViewOrder</span></a></li>"
						+ "<li><a href='Account'><span class='glyphicon'>Account</span></a></li>"
						+ "<li><a href='ManageProducts'><span class='glyphicon'>ManageProducts</span></a></li>"
						+ "<li><a href='Logout'><span class='glyphicon'>Logout</span></a></li></ul></div></div>";
					break;
				}
				// result = result + "<li><a href='ViewOrder'><span class='glyphicon'>ViewOrder</span></a></li>"
						// + "<li><a><span class='glyphicon'>Hello,"+username+"</span></a></li>"
						// + "<li><a href='Account'><span class='glyphicon'>Account</span></a></li>"
						// + "<li><a href='Logout'><span class='glyphicon'>Logout</span></a></li>";
			}
			else
				result = result + "<li><a href='ViewOrder'><span class='glyphicon'>ViewOrder</span></a></li>"
								+ "<li><a href='Login'><span class='glyphicon'>Login</span></a></li>"
								+ "<li><a href='Cart'><span class='glyphicon'>Cart("+CartCount()+")</span></a></li></ul></div></div>";
				pw.print(result);
		} else
				pw.print(result);
	}
	

	/*  getFullURL Function - Reconstructs the URL user request  */

	public String getFullURL() {
		String scheme = req.getScheme();
		String serverName = req.getServerName();
		int serverPort = req.getServerPort();
		String contextPath = req.getContextPath();
		StringBuffer url = new StringBuffer();
		url.append(scheme).append("://").append(serverName);

		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}
		url.append(contextPath);
		url.append("/");
		return url.toString();
	}

	/*  HtmlToString - Gets the Html file and Converts into String and returns the String.*/
	public String HtmlToString(String file) {
		String result = null;
		try {
			String webPage = url + file;
			URL url = new URL(webPage);
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			result = sb.toString();
		} 
		catch (Exception e) {
		}
		return result;
	} 

	/*  logout Function removes the username , usertype attributes from the session variable*/

	public void logout(){
		session.removeAttribute("username");
		session.removeAttribute("usertype");
	}
	
	/*  logout Function checks whether the user is loggedIn or Not*/

	public boolean isLoggedin(){
		if (session.getAttribute("username")==null)
			return false;
		return true;
	}

	/*  username Function returns the username from the session variable.*/
	
	public String username(){
		if (session.getAttribute("username")!=null)
			return session.getAttribute("username").toString();
		return null;
	}
	
	/*  usertype Function returns the usertype from the session variable.*/
	public String usertype(){
		if (session.getAttribute("usertype")!=null)
			return session.getAttribute("usertype").toString();
		return null;
	}
	
	/*  getUser Function checks the user is a customer or retailer or manager and returns the user class variable.*/
	public User getUser(){
		String usertype = usertype();
		HashMap<String, User> hm=new HashMap<String, User>();
		String TOMCAT_HOME = System.getProperty("catalina.home");
			try
			{		
				FileInputStream fileInputStream=new FileInputStream(new File(TOMCAT_HOME+"\\webapps\\BestDeal\\UserDetails.txt"));
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);	      
				hm= (HashMap)objectInputStream.readObject();
			}
			catch(Exception e)
			{
			}	
		User user = hm.get(username());
		return user;
	}
	
	/*  getCustomerOrders Function gets  the Orders for the user*/
	public ArrayList<OrderItem> getCustomerOrders(){
		ArrayList<OrderItem> order = new ArrayList<OrderItem>(); 
		if(OrdersHashMap.orders.containsKey(username()))
			order= OrdersHashMap.orders.get(username());
		return order;
	}

	/*  getOrdersPaymentSize Function gets  the size of OrderPayment */
	public int getOrderPaymentSize(){
		HashMap<Integer, ArrayList<OrderPayment>> orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
		String TOMCAT_HOME = System.getProperty("catalina.home");
			try
			{
				FileInputStream fileInputStream = new FileInputStream(new File(TOMCAT_HOME+"\\webapps\\BestDeal\\PaymentDetails.txt"));
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);	      
				orderPayments = (HashMap)objectInputStream.readObject();
			}
			catch(Exception e)
			{
			
			}
			int size=0;
			for(Map.Entry<Integer, ArrayList<OrderPayment>> entry : orderPayments.entrySet()){
					 size=size + 1;
					 
			}
			return size;		
	}

	/*  CartCount Function gets  the size of User Orders*/
	public int CartCount(){
		if(isLoggedin())
		return getCustomerOrders().size();
		return 0;
	}
	
	/* StoreProduct Function stores the Purchased product in Orders HashMap according to the User Names.*/

	public void storeProduct(String name,String type,String maker, String acc){
		if(!OrdersHashMap.orders.containsKey(username())){	
			ArrayList<OrderItem> arr = new ArrayList<OrderItem>();
			OrdersHashMap.orders.put(username(), arr);
		}
		ArrayList<OrderItem> orderItems = OrdersHashMap.orders.get(username());
		if(type.equals("tvs")){
			Tv tv;
			tv = SaxParserDataStore.tvs.get(name);
			OrderItem orderitem = new OrderItem(tv.getName(), tv.getPrice(), tv.getImage(), tv.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("soundsystems")){
			SoundSystem soundsystem;
			soundsystem = SaxParserDataStore.soundsystems.get(name);
			OrderItem orderitem = new OrderItem(soundsystem.getName(), soundsystem.getPrice(), soundsystem.getImage(), soundsystem.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("phones")){
			Phone phone = null;
			phone = SaxParserDataStore.phones.get(name);
			OrderItem orderitem = new OrderItem(phone.getName(), phone.getPrice(), phone.getImage(), phone.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("laptops")){
			Laptop laptop = null;
			laptop = SaxParserDataStore.laptops.get(name);
			OrderItem orderitem = new OrderItem(laptop.getName(), laptop.getPrice(), laptop.getImage(), laptop.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("voiceassistants")){	
			VoiceAssistant voiceassistant = SaxParserDataStore.voiceassistants.get(name); 
			OrderItem orderitem = new OrderItem(voiceassistant.getName(), voiceassistant.getPrice(), voiceassistant.getImage(), voiceassistant.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("fitnesswatches")){
			FitnessWatch fitnesswatch = null;
			fitnesswatch = SaxParserDataStore.fitnesswatches.get(name);
			OrderItem orderitem = new OrderItem(fitnesswatch.getName(), fitnesswatch.getPrice(), fitnesswatch.getImage(), fitnesswatch.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("smartwatches")){
			SmartWatch smartwatch = null;
			smartwatch = SaxParserDataStore.smartwatches.get(name);
			OrderItem orderitem = new OrderItem(smartwatch.getName(), smartwatch.getPrice(), smartwatch.getImage(), smartwatch.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("headphones")){
			HeadPhone headphone = null;
			headphone = SaxParserDataStore.headphones.get(name);
			OrderItem orderitem = new OrderItem(headphone.getName(), headphone.getPrice(), headphone.getImage(), headphone.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("virtualrealitys")){
			VirtualReality virtualreality = null;
			virtualreality = SaxParserDataStore.virtualrealitys.get(name);
			OrderItem orderitem = new OrderItem(virtualreality.getName(), virtualreality.getPrice(), virtualreality.getImage(), virtualreality.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("pettrackers")){
			PetTracker pettracker = null;
			pettracker = SaxParserDataStore.pettrackers.get(name);
			OrderItem orderitem = new OrderItem(pettracker.getName(), pettracker.getPrice(), pettracker.getImage(), pettracker.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("accessories")){	
			Accessory accessory = SaxParserDataStore.accessories.get(name); 
			OrderItem orderitem = new OrderItem(accessory.getName(), accessory.getPrice(), accessory.getImage(), accessory.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("warranty")){	 
			OrderItem orderitem = new OrderItem("Warranty", 100.0, "abc.jpg", "Warranty");
			orderItems.add(orderitem);
		}
		
	}
	// store the payment details for orders
	public void storePayment(int orderId,
		String orderName,double orderPrice,String userAddress,String creditCardNo,String FirstName,String LastName,
						String City, String State, String zip, String PhoneNumber, String CardName, String Month, String Year, String cvv){
		HashMap<Integer, ArrayList<OrderPayment>> orderPayments= new HashMap<Integer, ArrayList<OrderPayment>>();
		String TOMCAT_HOME = System.getProperty("catalina.home");
			// get the payment details file 
			try
			{
				FileInputStream fileInputStream = new FileInputStream(new File(TOMCAT_HOME+"\\webapps\\BestDeal\\PaymentDetails.txt"));
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);	      
				orderPayments = (HashMap)objectInputStream.readObject();
			}
			catch(Exception e)
			{
			
			}
			if(orderPayments==null)
			{
				orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
			}
			// if there exist order id already add it into same list for order id or create a new record with order id
			
			if(!orderPayments.containsKey(orderId)){	
				ArrayList<OrderPayment> arr = new ArrayList<OrderPayment>();
				orderPayments.put(orderId, arr);
			}
		ArrayList<OrderPayment> listOrderPayment = orderPayments.get(orderId);		
		OrderPayment orderpayment = new OrderPayment(orderId,username(),orderName,orderPrice,userAddress,creditCardNo,
		FirstName,LastName,City,State,zip,PhoneNumber,CardName,Month,Year,cvv);
		listOrderPayment.add(orderpayment);	
			
			// add order details into file

			try
			{	
				FileOutputStream fileOutputStream = new FileOutputStream(new File(TOMCAT_HOME+"\\webapps\\BestDeal\\PaymentDetails.txt"));
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            	objectOutputStream.writeObject(orderPayments);
				objectOutputStream.flush();
				objectOutputStream.close();       
				fileOutputStream.close();
			}
			catch(Exception e)
			{
				System.out.println("inside exception file not written properly");
			}	
	}

	
	/* tvs Functions returns the Hashmap with all tvs in the store.*/

	public HashMap<String, Tv> getTvs(){
			HashMap<String, Tv> hm = new HashMap<String, Tv>();
			hm.putAll(SaxParserDataStore.tvs);
			return hm;
	}

	/* soundsystems Functions returns the Hashmap with all soundsystems in the store.*/

	public HashMap<String, SoundSystem> getSoundSystems(){
			HashMap<String, SoundSystem> hm = new HashMap<String, SoundSystem>();
			hm.putAll(SaxParserDataStore.soundsystems);
			return hm;
	}
	
	/* phones Functions returns the  Hashmap with all Phones in the store.*/

	public HashMap<String, Phone> getPhones(){
			HashMap<String, Phone> hm = new HashMap<String, Phone>();
			hm.putAll(SaxParserDataStore.phones);
			return hm;
	}
	
	/* laptops Functions returns the Hashmap with all Laptop in the store.*/

	public HashMap<String, Laptop> getLaptops(){
			HashMap<String, Laptop> hm = new HashMap<String, Laptop>();
			hm.putAll(SaxParserDataStore.laptops);
			return hm;
	}

	/* voiceassistants Functions returns the Hashmap with all VoiceAssistants in the store.*/

	public HashMap<String, VoiceAssistant> getVoiceAssistants(){
			HashMap<String, VoiceAssistant> hm = new HashMap<String, VoiceAssistant>();
			hm.putAll(SaxParserDataStore.voiceassistants);
			return hm;
	}

	/* fitnesswatches Functions returns the Hashmap with all FitnessWatch in the store.*/

	public HashMap<String, FitnessWatch> getFitnessWatches(){
			HashMap<String, FitnessWatch> hm = new HashMap<String, FitnessWatch>();
			hm.putAll(SaxParserDataStore.fitnesswatches);
			return hm;
	}

	/* smartwatches Functions returns the Hashmap with all SmartWatch in the store.*/

	public HashMap<String, SmartWatch> getSmartWatches(){
			HashMap<String, SmartWatch> hm = new HashMap<String, SmartWatch>();
			hm.putAll(SaxParserDataStore.smartwatches);
			return hm;
	}

	/* headphones Functions returns the Hashmap with all HeadPhone in the store.*/

	public HashMap<String, HeadPhone> getHeadPhones(){
			HashMap<String, HeadPhone> hm = new HashMap<String, HeadPhone>();
			hm.putAll(SaxParserDataStore.headphones);
			return hm;
	}

	/* virtualrealitys Functions returns the Hashmap with all virtualreality in the store.*/

	public HashMap<String, VirtualReality> getVirtualRealitys(){
			HashMap<String, VirtualReality> hm = new HashMap<String, VirtualReality>();
			hm.putAll(SaxParserDataStore.virtualrealitys);
			return hm;
	}
	
	/* pettrackers Functions returns the Hashmap with all pettracker in the store.*/

	public HashMap<String, PetTracker> getPetTrackers(){
			HashMap<String, PetTracker> hm = new HashMap<String, PetTracker>();
			hm.putAll(SaxParserDataStore.pettrackers);
			return hm;
	}
	
	/* getProducts Functions returns the Arraylist of tvs in the store.*/

	public ArrayList<String> getProductsTvs(){
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, Tv> entry : getTvs().entrySet()){			
			ar.add(entry.getValue().getName());
		}
		return ar;
	}

	/* getProducts Functions returns the Arraylist of soundsystems in the store.*/

	public ArrayList<String> getProductsSoundSystems(){
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, SoundSystem> entry : getSoundSystems().entrySet()){			
			ar.add(entry.getValue().getName());
		}
		return ar;
	}
	
	/* getProducts Functions returns the Arraylist of phones in the store.*/

	public ArrayList<String> getProductsPhones(){		
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, Phone> entry : getPhones().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}
	
	/* getProducts Functions returns the Arraylist of Laptops in the store.*/

	public ArrayList<String> getProductsLaptops(){		
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, Laptop> entry : getLaptops().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}

	/* getProducts Functions returns the Arraylist of VoiceAssistants in the store.*/
	
	public ArrayList<String> getProductsVoiceAssistants(){		
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, VoiceAssistant> entry : getVoiceAssistants().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}

	/* getProducts Functions returns the Arraylist of FitnessWatches in the store.*/

	public ArrayList<String> getProductsFitnessWatches(){		
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, FitnessWatch> entry : getFitnessWatches().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}
	
	/* getProducts Functions returns the Arraylist of SmartWatches in the store.*/

	public ArrayList<String> getProductsSmartWatches(){		
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, SmartWatch> entry : getSmartWatches().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}

	/* getProducts Functions returns the Arraylist of Headphones in the store.*/

	public ArrayList<String> getProductsHeadphones(){		
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, HeadPhone> entry : getHeadPhones().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}

	/* getProducts Functions returns the Arraylist of VirtualRealitys in the store.*/

	public ArrayList<String> getProductsVirtualRealitys(){		
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, VirtualReality> entry : getVirtualRealitys().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}
	
	/* getProducts Functions returns the Arraylist of PetTrackers in the store.*/

	public ArrayList<String> getProductsPetTrackers(){		
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, PetTracker> entry : getPetTrackers().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}
}
