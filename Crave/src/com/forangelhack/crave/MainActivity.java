package com.forangelhack.crave;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import com.google.gson.*;
import android.widget.RatingBar;

/*import com.singly.android.client.AsyncApiResponseHandler;
import com.singly.android.client.SinglyClient;
import com.singly.android.client.SinglyClient.Authentication;
import com.singly.android.component.AuthenticatedServicesActivity;
import com.singly.android.component.FriendsListActivity;
*/
public class MainActivity extends Activity {
    private SearchView mInput = null;
    private TextView line1;
    private TextView line2;
    private RatingBar spicyBar;
    private View resultView;
    private TextView moreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	
	mInput = (SearchView) findViewById(R.id.searchView);
	line1 = (TextView) findViewById(R.id.textLine1);
	line2 = (TextView) findViewById(R.id.textLine2);
	spicyBar = (RatingBar) findViewById(R.id.spicyBar);
	View resultView = findViewById(R.id.resultView);
	moreText = (TextView) findViewById(R.id.someText);

    }

    private void initSingly()
    {
	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "Go home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_map:
                Toast.makeText(this, "bring up map view", Toast.LENGTH_SHORT).show();                
                break;

            case R.id.menu_checkin:
                Toast.makeText(this, "Check ih", Toast.LENGTH_SHORT).show();
                break;

/*            case R.id.menu_share:
                Toast.makeText(this, "Tapped share", Toast.LENGTH_SHORT).show();
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSubmit(View v){
	/*String searchTerm = ((TextView)mInput).getText().toString();*/
	
	String searchTerm = ((SearchView)mInput).getContext().toString();

	SearchTask s = new SearchTask(searchTerm, null, null, null);
	s.execute(this);
    }

    private void updateDisplay(){
	if(resultz == null)
	    return;
	
	String rd = "";
	for(ResultObj r: resultz){
	    rd+=r.dishName;
	}

	((TextView)findViewById(R.id.textView1)).setText(rd);
    }

    private ResultObj[] resultz = null;

   

    private class SearchTask extends AsyncTask<Context, JSONArray, JSONArray> {
	private static final String SVC_URL = "http://foodcravefinder.appspot.com/foodfind"; 
	String q;
	String uid;
	String lat;
	String lon;
	SearchTask(String searchTerm, String uid, String lat, String lon){
	    this.q = searchTerm;
	    this.uid = uid;
	    this.lat = lat;
	    this.lon = lon;
	}

	public JSONArray doInBackground(Context... args) {
	    try {
		Context ctx = args[0];
		String qUrl = SVC_URL +"?q="+q;
		JSONArray j = NetworkUtil.commonInit(qUrl, ctx);
		return j;
	    }
	    catch(Exception e){
		e.printStackTrace();
		return null;
	    }
	}

	public void onPostExecute(JSONArray result) {
	    if ( result == null ) {
		return;
	    }

	    JSONObject objz[] = new JSONObject[result.length()];
	    resultz  = new ResultObj[result.length()];
	    Gson g = new Gson();
	    for(int i = 0; i < result.length(); i++){
		try{
		    objz[i] = result.getJSONObject(i);
		    resultz[i] = g.fromJson(objz[i].toString(), ResultObj.class);
		}
		catch(Exception e){

		}
	    }

	    updateDisplay();
	}


    }

    
}
