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
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import codigo.labplc.mx.mitaxichofer.R;
import codigo.labplc.mx.mitaxichofer.trip.beans.TaxiDriver;
import codigo.labplc.mx.mitaxichofer.trip.dialogues.Dialogues;
import codigo.labplc.mx.mitaxichofer.trip.location.AnimationFactory;
import codigo.labplc.mx.mitaxichofer.trip.location.GMapV2Direction;
import codigo.labplc.mx.mitaxichofer.trip.location.LocationUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * 
 * @author zace3d
 * 
 */
public class MitaxiTripActivity extends LocationActivity implements OnClickListener {
	private GoogleMap map;

	private ArrayList<LatLng> listTaxiRoute = new ArrayList<LatLng>();
	
	private LatLng userPosition = null,userPositionDestino=null;
	private LatLng taxiPosition = null;

	private GMapV2Direction googleMapsDirections = new GMapV2Direction();

	private TaxiDriver taxiDriver;
	private String pk_viaje;
	private String pk_chofer;
	private String placa;
	private String origen="";
	private String destino,referencia;
	private String tiempo= "0",distancia="0";
	private String titulo ="Mi ubicación";
	private String direccionOrigen="",direccionDestino="";
	private boolean flagBtnViaje = true;
	
	TextView mitaxi_trip_tv_titulo_direccion,mitaxi_trip_tv_direccion,mitaxi_trip_tv_referencia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mitaxi_trip);
		
		
		Bundle bundle = getIntent().getExtras();
		
		pk_viaje = bundle.getString("pk_viaje");	
		pk_chofer = bundle.getString("pk_chofer");
		placa = bundle.getString("placa");
		origen = bundle.getString("origen");
		destino = bundle.getString("destino");
		referencia = bundle.getString("referencia");
		
		Log.d("pk_viaje", pk_viaje);
		Log.d("pk_chofer", pk_chofer);
		Log.d("origen", origen);
		Log.d("destino", destino);
		Log.d("placa", placa);
		Log.d("referencia", placa);
		
		
		
		
		
		
		//traemos las direcciones, distancia y tiempo
				placa = placa.replaceAll(" ", "");
		
				origen  = origen.replaceAll("[()]", "");
				String sorigen[] = origen.split(",");
				
				destino  = destino.replaceAll("[()]", "");
				String sdestino[] = destino.split(",");
				
				userPosition = new LatLng(Double.parseDouble(sorigen[0]),Double.parseDouble(sorigen[1]));
				userPositionDestino = new LatLng(Double.parseDouble(sdestino[0]),Double.parseDouble(sdestino[1]));
				
				
				
				
				
				//datos de origen destino
				String consulta = "http://datos.labplc.mx/~mikesaurio/taxi.php?act=chofer&type=getGoogleData&lato="
						+sorigen[0]+"&lngo="+sorigen[1]
						+"&latd="+ sdestino[0]+"&lngd="+ sdestino[1]+"&filtro=todo";
				String querty = doHttpConnection(consulta);
				try {
				//mostramos los resultados al chofer en el viaje
				JSONObject jObj = new JSONObject(querty);
				direccionOrigen = jObj.getString("origin_addresses");
				direccionDestino = jObj.getString("destination_addresses");
				} catch (JSONException e) {
					e.printStackTrace();
				}	 
		
		
		this.initUI();
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(broadcastReceiver, new IntentFilter(TaxiRouteService.PATH));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
	}

	/**
	 * Broadcast to receive the list of founded locations from the service and paint the direction into the map
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				listTaxiRoute = (ArrayList<LatLng>) bundle.getSerializable(TaxiRouteService.ID_LIST_TAXI_ROUTE);

				if (listTaxiRoute != null && listTaxiRoute.size() > 0) {
					LatLng latLng = listTaxiRoute.get(listTaxiRoute.size() - 1);

					map.clear();

					drawRouteBetweenTwoPositions(latLng, userPosition);
				}
			}
		}
	};

	/**
	 * Create the interface of the taxi driver route
	 */
	public void initUI() {
        
        
        // Nombre y apellido del taxista
         mitaxi_trip_tv_titulo_direccion = (TextView) findViewById(R.id.mitaxi_trip_tv_titulo_direccion);
        mitaxi_trip_tv_titulo_direccion.setText("Dirección Origen");
        
        // Placa del auto que usa el taxista
        mitaxi_trip_tv_direccion = (TextView) findViewById(R.id.mitaxi_trip_tv_direccion);
        mitaxi_trip_tv_direccion.setText(direccionOrigen);
        
        // Modelo del auto que usa el taxista
         mitaxi_trip_tv_referencia = (TextView) findViewById(R.id.mitaxi_trip_tv_referencia);
        mitaxi_trip_tv_referencia.setText(referencia.replaceAll("+", " "));
      
        // Driver position button
        findViewById(R.id.mitaxi_trip_btn_driverPosition).setOnClickListener(this);
        
        // Start Trip button
     final  Button mitaxi_trip_btn_starttrip= (Button) findViewById(R.id.mitaxi_trip_btn_starttrip);
       mitaxi_trip_btn_starttrip.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(flagBtnViaje){
			mitaxi_trip_btn_starttrip.setText("Viaje Terminado");
			titulo = "Mi destino";
			mitaxi_trip_tv_titulo_direccion.setText("Dirección destino");
			mitaxi_trip_tv_direccion.setText(direccionDestino);
			mitaxi_trip_tv_referencia.setText("");
			drawRouteBetweenTwoPositions(userPosition, userPositionDestino);
			userPosition = userPositionDestino;
			flagBtnViaje = false;
			}else {
				//cambiar a libre el status y  a finalizado el viaje
				MitaxiTripActivity.this.finish();
			}
			
		}
	});
       
       
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		if (map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mitaxi_trip_map)).getMap();
			if (map != null) {
				if (setUpMap()) {
					initMap();
				}
			}
		}
	}

	public void initMap() {
		map.setMyLocationEnabled(true);
		map.setBuildingsEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.getUiSettings().setZoomControlsEnabled(true); // ZOOM
		map.getUiSettings().setCompassEnabled(true); // COMPASS
		map.getUiSettings().setZoomGesturesEnabled(true); // GESTURES ZOOM
		map.getUiSettings().setRotateGesturesEnabled(true); // ROTATE GESTURES
		map.getUiSettings().setScrollGesturesEnabled(true); // SCROLL GESTURES
		map.getUiSettings().setTiltGesturesEnabled(true); // TILT GESTURES
		// map.setPadding(10, 100, 20, 50);

		map.setOnMyLocationButtonClickListener(this);
		map.setOnMapClickListener(this);
		map.setOnMarkerClickListener(this);
		// map.setOnMapLongClickListener(this);
		map.setOnMarkerDragListener(this);

		// Start the service to obtain the last taxi driver locations
		this.startService();
	}

	/**
	 * Add a marker to the map with the route time aprox
	 * 
	 * @param time
	 * @param latLng
	 */
	public void addMarkerIntoMap(String time, LatLng latLng) {
		
		LocationUtils.addMarker(map, latLng, "Taxi", false, "A: " + distancia+" aproximadamente: " + tiempo+ " de ti",
				BitmapDescriptorFactory.fromResource(R.drawable.mi_taxi_assets_taxi_on));

		this.taxiPosition = latLng;
		
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, map.getMaxZoomLevel() - 5));
	}

	/**
	 * Start drawing a PolyLine between two points
	 * 
	 * @param startPosition
	 * @param endPosition
	 */
	public void drawRouteBetweenTwoPositions(LatLng startPosition, LatLng endPosition) {
		String consulta2 = "http://datos.labplc.mx/~mikesaurio/taxi.php?act=chofer&type=getGoogleData&lato="
				+startPosition.latitude+"&lngo="+startPosition.longitude
				+"&latd="+endPosition.latitude+"&lngd="+endPosition.longitude+"&filtro=velocidad";
		String querty2 = doHttpConnection(consulta2);
		querty2= querty2.replaceAll("\"","");
		String[] Squerty2 = querty2.split(",");
		tiempo = Squerty2[0];
		distancia =Squerty2[1];
		
		MarkerOptions marker = new MarkerOptions().position(endPosition).title(titulo);
		marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
		map.addMarker(marker);
		googleMapsDirections.getDocument(this, startPosition, endPosition, GMapV2Direction.MODE_DRIVING);
	}

	/**
	 * Get a list with the direction and draw it into the map
	 * 
	 * @param doc
	 */
	public void drawRouteOnMap(Document doc) {
		ArrayList<LatLng> directionPoint = googleMapsDirections.getDirection(doc);
		PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.BLUE);

		for (int i = 0; i < directionPoint.size(); i++) {
			rectLine.add(directionPoint.get(i));
		}

		map.addPolyline(rectLine);
	}

	
	
	
	/**
	 * Start the service to obtain the last taxi driver locations
	 */
	public void startService() {
		Intent intent = new Intent(getBaseContext(), TaxiRouteService.class);
		intent.putExtra("pk_chofer", pk_chofer);
		startService(intent);
	}

	/**
	 * Set up the map
	 * 
	 * @return true if map is set up; false otherwise
	 */
	public boolean setUpMap() {
		if (!checkReady()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * 
	 * @return true if map is ready; false otherwise
	 */
	private boolean checkReady() {
		if (map == null) {
			Dialogues.Log(getApplicationContext(), getString(R.string.map_not_ready), Log.INFO);
			return false;
		}
		return true;
	}

	/**
	 * Get last Location known
	 * 
	 * @return a Location object
	 */
	public Location getLocation() {
		// If Google Play Services is available
		if (servicesConnected()) {

			// Get the current location
			return mLocationClient.getLastLocation();
		}
		return null;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if(findViewById(R.id.mitaxi_trip_ll_driverinfo).getVisibility() == View.GONE)
			AnimationFactory.fadeIn(this, findViewById(R.id.mitaxi_trip_ll_driverinfo));
		
		return false;
	}
	
	@Override
	public void onMapClick(LatLng point) {
		if(findViewById(R.id.mitaxi_trip_ll_driverinfo).getVisibility() == View.VISIBLE)
			AnimationFactory.fadeOut(this, findViewById(R.id.mitaxi_trip_ll_driverinfo));
	}
	
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.mitaxi_trip_btn_driverPosition:
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(taxiPosition, map.getMaxZoomLevel() - 5));
				break;
				
			/*	
			case R.id.mitaxi_trip_btn_starttrip:
				
				titulo = "Mi destino";
				mitaxi_trip_tv_titulo_direccion.setText("Dirección destino");
				mitaxi_trip_tv_direccion.setText(direccionDestino);
				mitaxi_trip_tv_referencia.setText("");
				drawRouteBetweenTwoPositions(userPosition, userPositionDestino);
				userPosition = userPositionDestino;
				break;*/
		}
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