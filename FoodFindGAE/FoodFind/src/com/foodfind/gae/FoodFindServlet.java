package com.foodfind.gae;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.*;

import com.singly.client.SinglyService;
import com.google.gson.*;

@SuppressWarnings("serial")
public class FoodFindServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		SinglyService svc = null;
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		
		ReqObj rq = parseRequest(req);
		if(rq == null)
		{
			resp.setContentType("text/plain");
			ResultObj er = new ResultObj("invalid parameter");
			resp.getWriter().println(g.toJson(er));
			return;
		}
		
		ResultObj r = new ResultObj("dummy", "http://www.google.com/", "dummy Restaurant", "1.0mi", "120.1123132","90.100012", 3.99f);
		
		resp.setContentType("text/plain");
		resp.getWriter().println(g.toJson(r));
		
	}

	private class ReqObj
	{
		String qstring;
		String uid;
		String lat;
		String lon;
	}
	
	private static final String QS = "q";
	private static final String LAT = "lat";
	private static final String LON = "lon";
	private static final String UID = "userid";
	
	private ReqObj parseRequest(HttpServletRequest req) {
		Map<String, String[]> m = req.getParameterMap();
		ReqObj ret = null;
		if(m.containsKey(QS)){
			ret = new ReqObj();
			ret.qstring = m.get(QS)[0];
		}
		else{
			// should we just throw runtime exception?
			return null;
		}
		
		if(m.containsKey(UID))
		{
			ret.uid = m.get(UID)[0];
			// can be null
		}
		
		if(m.containsKey(LAT) && m.containsKey(LON))
		{
			ret.lat = m.get(LAT)[0];
			ret.lon = m.get(LON)[0];
		}
		
		return ret;
		
	}
	
	
	
}
