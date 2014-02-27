package codigo.labplc.mx.mitaxichofer.trip;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import codigo.labplc.mx.mitaxichofer.trip.dialogues.Dialogues;

import com.google.android.gms.maps.model.LatLng;

/**
 * 
 * @author zace3d
 * 
 */
public class TaxiRouteService extends Service {
	public static final String PATH = "codigo.labplc.mx.mitaxi.trip";
	public static final String ID_LIST_TAXI_ROUTE = "listTaxiRoute";
	private int TIME_UPDATE_IN_MILIS = 20000;
	private ArrayList<LatLng> listTaxiRoute = new ArrayList<LatLng>();
	private Handler handler = new Handler();
	private int index = 1;
	private String pk_chofer;
	private String lat,lng;
	
	public void initListTaxiRoute() {
		
		Log.d("*****pk_initListTaxiRoute", pk_chofer);	
		
		listTaxiRoute.clear();
		String consulta ="http://datos.labplc.mx/~mikesaurio/taxi.php?act=chofer&type=getcoordenadas&pk_chofer="+pk_chofer;
		
		String querty = doHttpConnection(consulta);

		Log.d("*****CONSULTA", querty);
		
		try{
			  JSONObject json= (JSONObject) new JSONTokener(querty).nextValue();
		      JSONObject json2 = json.getJSONObject("message");
		      JSONObject jsonResponse = new JSONObject(json2.toString());
		      JSONArray cast = jsonResponse.getJSONArray("chofer");
		      for (int i=0; i<cast.length(); i++) {
		          	JSONObject oneObject = cast.getJSONObject(i);
		          	
		   lat = 	oneObject.getString("lat");
		   lng = 	oneObject.getString("lng");
		     
		    }
		
		listTaxiRoute.add(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))); // 19° 31.997', -99° 10.055'
	}catch(Exception e){
		e.getMessage();
	}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Dialogues.Toast(this, "onStartCommand Service", Toast.LENGTH_LONG);
		getMessenger(intent);
		
		runnable.run();
		
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		//Dialogues.Toast(this, "onCreate Service", Toast.LENGTH_LONG);
		
	
	}

	@Override
	public void onStart(Intent intent, int startId) {
		
	
	}

	@Override
	public void onDestroy() {
		Dialogues.Toast(this, "onDestroy Service", Toast.LENGTH_LONG);
		
		if(handler != null)
			handler.removeCallbacks(runnable);
	}

	 /**
     * get Messenger from calling intent
     * @param intent
     */
    private void getMessenger(Intent intent) {
        Bundle extras = intent.getExtras();
        if (null != extras) {
        	pk_chofer = extras.getString("pk_chofer");
        	initListTaxiRoute();
        }
    }
 
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			
			if(index <= listTaxiRoute.size()) {
				publishResults();
				
				handler.postDelayed(this, TIME_UPDATE_IN_MILIS);
			} else {
				if(handler != null)
					handler.removeCallbacks(runnable);
			}
		}
	};

	public ArrayList<LatLng> createList() {
		ArrayList<LatLng> listAux = new ArrayList<LatLng>();
		//for (int i = 0; i < index; i++) {
			listAux.add(listTaxiRoute.get(0));
		//}
		//index++;
		
		return listAux;
	}
	
	private void publishResults() {
		Intent intent = new Intent(PATH);
		intent.putExtra(ID_LIST_TAXI_ROUTE, createList());
		sendBroadcast(intent);
	}
	
	/**
	 * metodo que hace la conexion al servidor con una url especifica
	 * @param url(String) ruta del web service
	 * @return (String) resultado del service
	 */
	public static String doHttpConnection(String url) {
		HttpClient Client = new DefaultHttpClient();
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			HttpGet httpget = new HttpGet(url);
			HttpResponse hhrpResponse = Client.execute(httpget);
			HttpEntity httpentiti = hhrpResponse.getEntity();
			//Log.d("RETURN HTTPCLIENT", EntityUtils.toString(httpentiti));
			return EntityUtils.toString(httpentiti);
		} catch (ParseException e) {
			Log.d("Error ParseEception", e.getMessage() + "");
			return null;
		} catch (IOException e) {
			Log.d("Error IOException", e.getMessage() + "");
			return null;
		}
	}
}