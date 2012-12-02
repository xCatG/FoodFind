package com.forangelhack.crave;

public class ResultObj {
	public String pictureUrl;
	public String restaurantName;
	public String dishName;
	public String distance;
	public String lat;
	public String lon;
	public float price;
	public String err;
	
	public ResultObj(){}
	public ResultObj(String itemName, String picUrl, String resName, 
			String distance, String lat, String lon, float price){
		this.dishName = itemName;
		this.pictureUrl = picUrl;
		this.restaurantName = resName;
		this.distance = distance;
		this.lat = lat;
		this.lon = lon;
		this.price = price;
	}
	
	public ResultObj(String error){
		
	}
	
}
