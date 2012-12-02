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
		
		//		ResultObj r = new ResultObj("dummy", "http://www.google.com/", "dummy Restaurant", "1.0mi", "120.1123132","90.100012", 3.99f);
		ResultObj z[] = getRecommendation(rq);//= new ResultObj[3]; 
		//z[0] = r;z[1] = r;z[2] = r;
		resp.setContentType("text/plain");
		resp.getWriter().println(g.toJson(z));
		
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
	
	private ResultObj[] getRecommendation(ReqObj p)
	{
		if(p.qstring.contains("spicy")||p.qstring.contains("chinese"))
		{
			String fsqIdz[] = {"4af63a1df964a5206e0222e3", "4a73ac34f964a5201fdd1fe3", "4af37f39f964a52027ee21e3"};
			String dishImgUrlz[] = {"https://s3-us-west-2.amazonaws.com/foodfinderangelhack/CQHC.jpg",
					"https://s3-us-west-2.amazonaws.com/foodfinderangelhack/SBT.jpg",
					"https://s3-us-west-2.amazonaws.com/foodfinderangelhack/SFF.jpg"};
			String dishNamez[] = {"Chong Qing Hot Chicken", "Spicy Basil Tofu", "Spicy fish fillet"};
			String rnamez[] = {"Szechuan Chef", "Bamboo Garden", "Spiced"};
			ResultObj ret[] = new ResultObj[3];
			float fPrize[] = {8.75f, 9.99f, 7.25f};
			for(int i = 0; i < 3; i++ )
			{
				ret[i] = new ResultObj(dishNamez[i], dishImgUrlz[i],rnamez[i],"","","",fPrize[i]);
			}
			return ret;
		}
		
		
		
		
		return null;
	}
	
	
	
}
