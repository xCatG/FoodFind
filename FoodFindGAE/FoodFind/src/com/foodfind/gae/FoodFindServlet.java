package com.foodfind.gae;

import java.io.IOException;
import javax.servlet.http.*;

import com.singly.client.SinglyService;
import com.google.gson.*;

@SuppressWarnings("serial")
public class FoodFindServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		SinglyService svc = null;
		
		ResultObj r = new ResultObj("dummy", "http://www.google.com/", "dummy Restaurant", "1.0mi", "120.1123132","90.100012", 3.99f);
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		
		
		resp.setContentType("text/plain");
		resp.getWriter().println(g.toJson(r));
		
	}
	
	
	
}
