import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/StorePickup")
public class StorePickup extends HttpServlet {
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
	    Utilities utility = new Utilities(request, pw);
		HttpSession session = request.getSession(true);		
		
		// get the payment details like credit card no address processed from cart servlet	
		
		String userName = session.getAttribute("username").toString();
		String userAddress=request.getParameter("userAddress");
		String creditCardNo=request.getParameter("creditCardNo");
		String FirstName=request.getParameter("FirstName");
		String LastName=request.getParameter("LastName");
		String City=request.getParameter("City");
		String State=request.getParameter("State");
		String zip=request.getParameter("zip");
		String PhoneNumber=request.getParameter("PhoneNumber");
		String CardName=request.getParameter("CardName");
		String Month=request.getParameter("Month");
		String Year=request.getParameter("Year");
		String cvv=request.getParameter("cvv");
		String delivery=request.getParameter("delivery");
		System.out.print("the user address is" +userAddress);
		System.out.print(creditCardNo);
		System.out.print(delivery);
		
		if(!userAddress.isEmpty() && !creditCardNo.isEmpty() && !FirstName.isEmpty()  && !LastName.isEmpty() && !City.isEmpty() && !State.isEmpty()
			&& !zip.isEmpty() && !PhoneNumber.isEmpty() && !CardName.isEmpty() && !Month.isEmpty() && !Year.isEmpty() && !cvv.isEmpty())
		{
			//Random rand = new Random(); 
			//int orderId = rand.nextInt(100);
			int orderId=utility.getOrderPaymentSize()+1;

			//iterate through each order

			for (OrderItem oi : utility.getCustomerOrders())
			{
				//set the parameter for each column and execute the prepared statement
				utility.storePayment(orderId,oi.getName(),oi.getPrice(),userAddress,creditCardNo,
				FirstName,LastName,City,State,zip,PhoneNumber,CardName,Month,Year,cvv);
			}

			//remove the order details from cart after processing
			
			OrdersHashMap.orders.remove(utility.username());		
		
			//get the order product details	on clicking submit the form will be passed to submitorder page	
			
			utility.printHtml("Header.html");
			utility.printHtml("LeftNavigationBar.html");
			pw.print("<form name ='StorePickup' action='Payment' method='post'>");		
			pw.print("<div id='content'><div class='post'><h2 class='title meta'>");
			pw.print("<a style='font-size: 24px;'>Order</a>");
			pw.print("</h2><div class='entry'>");
			pw.print("<p style='font-size: 16px;color:#106dc4;padding-top:10px;font-weight:bold'>Customer Name:  " + userName +"</p>");
			pw.print("<p style='font-size: 16px;color:#106dc4;font-weight:bold'>Selected Delivery Type:  " + delivery +"</p>");
			if(delivery.equals("Store Pick-up")) {
				pw.print("<div><p style='font-size:16px; font-weight:bold; color:#106dc4;'>Select a store to pick-up your order:</p>");
				pw.print("<table style='border-collapse: collapse; width: 100%;'>");
				pw.print("<tr><td style='font-size:13px;'><input type='radio' name='storeAddress' value='BestDeal, 555 W Roosevelt Rd, Chicago, IL 60607' checked> BestDeal </br>555 W Roosevelt Rd,</br> Chicago, IL 60607</td>");
				pw.print("<td style='font-size:13px;'><input type='radio' name='storeAddress' value='BestDeal, 1000 W North Ave, Chicago, IL 60642'> BestDeal </br>1000 W North Ave,</br> Chicago, IL 60642</td></tr><br>");
				pw.print("<tr><td style='font-size:13px;'><input type='radio' name='storeAddress' value='BestDeal, 2100 N Elston Ave, Chicago, IL 60614'> BestDeal </br>2100 N Elston Ave,</br> Chicago, IL 60614</td>");
				pw.print("<td style='font-size:13px;'><input type='radio' name='storeAddress' value='BestDeal, 2358 S Harlem Ave, North Riverside, IL 60546'> BestDeal </br>2358 S Harlem Ave,</br> North Riverside, IL 60546</td></tr><br>");
				pw.print("<tr><td style='font-size:13px;'><input type='radio' name='storeAddress' value='BestDeal, 7602 S Cicero Ave, Burbank, IL 60459'> BestDeal </br>7602 S Cicero Ave,</br> Burbank, IL 60459</td>");
				pw.print("<td style='font-size:13px;'><input type='radio' name='storeAddress' value='BestDeal, 4100A N Harlem Ave, Norridge, IL 60706'> BestDeal </br>4100A N Harlem Ave,</br> Norridge, IL 60706</td></tr><br>");
				pw.print("<tr><td style='font-size:13px;'><input type='radio' name='storeAddress' value='BestDeal, 1334 Winston Plaza, Melrose Park, IL 60160'> BestDeal </br>1334 Winston Plaza,</br> Melrose Park, IL 60160</td>");
				pw.print("<td style='font-size:13px;'><input type='radio' name='storeAddress' value='BestDeal, 2301 Howard St, Evanston, IL 60202'> BestDeal </br>2301 Howard St,</br> Evanston, IL 60202</td></tr><br>");
				pw.print("<tr><td style='font-size:13px;'><input type='radio' name='storeAddress' value='BestDeal, 5425 Touhy Ave, Skokie, IL 60077'> BestDeal </br>5425 Touhy Ave,</br> Skokie, IL 60077</td>");
				pw.print("<td style='font-size:13px;'><input type='radio' name='storeAddress' value='BestDeal, 11 Countryside Plaza, Countryside, IL 60525'> BestDeal </br>11 Countryside Plaza,</br> Countryside, IL 60525</td></tr><br>");
				pw.print("</table></div>");
			} else {
				String homeAddress = userAddress + ", " + City + ", " + State + ", " + zip;
				pw.print("<p style='font-size:16px; font-weight:bold; color:#106dc4;'>Please confirm your delivery address:</p>");
				pw.print("<p style='font-size: 13px;color:#106dc4;'><input type='hidden' name='storeAddress' value='homeAddress'><input type='hidden' name='homeAddress' value='home'>Delivery Address:  </br>" 
				+ homeAddress
				+ "</p>");
				pw.print("<div><table style='width: 25%; border-collapse: collapse;'><tr><td><input type='radio' name='addrConf' checked>Yes</td>");
				pw.print("<td><input type='radio' name='addrConf'>No</td></tr></table></div>");
			}
			
			pw.print("<br><br><table><tr><td colspan='2' style='text-align:center;'>");
			pw.print("<input type='submit' name='CheckOut' value='CheckOut' class='btnbuy'>");
			pw.print("</td></tr></table></form>");
			pw.print("</div></div></div>");				
			utility.printHtml("Footer.html");
		} 			
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
	}
 
}