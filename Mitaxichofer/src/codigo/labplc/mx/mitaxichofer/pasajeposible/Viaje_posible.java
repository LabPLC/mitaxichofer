package codigo.labplc.mx.mitaxichofer.pasajeposible;

import java.io.IOException;

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import codigo.labplc.mx.mitaxichofer.R;
import codigo.labplc.mx.mitaxichofer.tracking.Taximetro_choferActivity;
import codigo.labplc.mx.mitaxichofer.trip.MitaxiTripActivity;

public class Viaje_posible extends Activity {

	private TextView viaje_posible_tv_numero_pasajeros;
	private TextView viaje_posible_tv_tiempo_origen;
	private TextView viaje_posible_tv_distancia_origen;
	private TextView viaje_posible_tv_direccion_origen;
	private TextView viaje_posible_tv_tiempo_destino;
	private TextView viaje_posible_tv_distancia_destino;
	private TextView viaje_posible_tv_direccion_destino;
	private Button viaje_posible_btn_cancel;
	private Button viaje_posible_btn_ok;
	private String pk_viaje=null,origen=null,destino=null,Sorigen=null,Sdestino=null;
	private String pasajeros= "1",timpoOrigen="0",tiempoDestino="0",distanciaOrigen="0",distanciaDestino="0";
	private String mascotas,discapacitados,bicicleta,placa;
	private ImageView viaje_posible_iv_destino, viaje_posible_iv_origen;
	private String uuid, referencia;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viaje_posible);
		
		
		Bundle bundle = getIntent().getExtras();
		
		pk_viaje = bundle.getString("pk_viaje");
		origen = bundle.getString("origen");
		destino = bundle.getString("destino");
		pasajeros = bundle.getString("pasajeros");
		mascotas = bundle.getString("mascotas");
		discapacitados = bundle.getString("discapacitados");
		bicicleta = bundle.getString("bicicleta");
		placa = bundle.getString("placa");
		referencia = bundle.getString("referencia");
		
		SharedPreferences prefs = getSharedPreferences("MisPreferenciasChofer",Context.MODE_PRIVATE);
		uuid = prefs.getString("uuid", null);
		
		//traemos las direcciones, distancia y tiempo
		origen  = origen.replaceAll("[()]", "");
		String sorigen[] = origen.split(",");
		
		destino  = destino.replaceAll("[()]", "");
		String sdestino[] = destino.split(",");
		
		
		//datos de origen destino
		String consulta = "http://datos.labplc.mx/~mikesaurio/taxi.php?act=chofer&type=getGoogleData&lato="
				+sorigen[0]+"&lngo="+sorigen[1]
				+"&latd="+ sdestino[0]+"&lngd="+ sdestino[1]+"&filtro=todo";
		String querty = doHttpConnection(consulta);
		
		try {
		//mostramos los resultados al chofer en el viaje
		JSONObject jObj = new JSONObject(querty);
			Sorigen = jObj.getString("origin_addresses");
			Sdestino = jObj.getString("destination_addresses");
			 JSONArray rows = jObj.getJSONArray("rows"); // Get all JSONArray rows
	            for(int i=0; i < rows.length(); i++) { // Loop over each each row
	                JSONObject row = rows.getJSONObject(i); // Get row object
	                JSONArray elements = row.getJSONArray("elements"); // Get all elements for each row as an array

	                for(int j=0; j < elements.length(); j++) { // Iterate each element in the elements array
	                    JSONObject element =  elements.getJSONObject(j); // Get the element object
	                    JSONObject duration = element.getJSONObject("duration"); // Get duration sub object
	                    JSONObject distance = element.getJSONObject("distance"); // Get distance sub object
	                    tiempoDestino=duration.getString("text");
	                    distanciaDestino	= distance.getString("text");
	                }
	            }
		} catch (JSONException e) {
			e.printStackTrace();
		}	 
		
		
	String[] ubicacionTaxi =	Taximetro_choferActivity.tokens;
	String consulta2 = "http://datos.labplc.mx/~mikesaurio/taxi.php?act=chofer&type=getGoogleData&lato="
			+ubicacionTaxi[0]+"&lngo="+ubicacionTaxi[1]
			+"&latd="+ sorigen[0]+"&lngd="+ sorigen[1]+"&filtro=velocidad";
	String querty2 = doHttpConnection(consulta2);
	querty2= querty2.replaceAll("\"","");
	String[] Squerty2 = querty2.split(",");
		//peticion de mi ubicacion al usuario y del usuario al destino
		
		
		viaje_posible_tv_numero_pasajeros = (TextView) findViewById(R.id.viaje_posible_tv_numero_pasajeros);
		viaje_posible_tv_numero_pasajeros.append(pasajeros+"");
		
		viaje_posible_tv_direccion_origen = (TextView) findViewById(R.id.viaje_posible_tv_direccion_origen);
		viaje_posible_tv_direccion_origen.append(Sorigen+"");
		
		viaje_posible_tv_direccion_destino = (TextView) findViewById(R.id.viaje_posible_tv_direccion_destino);
		viaje_posible_tv_direccion_destino.append(Sdestino+"");

		viaje_posible_tv_tiempo_destino = (TextView) findViewById(R.id.viaje_posible_tv_tiempo_destino);
		viaje_posible_tv_tiempo_destino.setText(tiempoDestino);
		
		viaje_posible_tv_distancia_destino = (TextView) findViewById(R.id.viaje_posible_tv_distancia_destino);
		viaje_posible_tv_distancia_destino.setText(distanciaDestino);
		
		
		viaje_posible_tv_tiempo_origen = (TextView) findViewById(R.id.viaje_posible_tv_tiempo_origen);
		viaje_posible_tv_tiempo_origen.setText(Squerty2[0]);
		
		viaje_posible_tv_distancia_origen = (TextView) findViewById(R.id.viaje_posible_tv_distancia_origen);
		viaje_posible_tv_distancia_origen.setText(Squerty2[1]);
		
		viaje_posible_btn_cancel = (Button) findViewById(R.id.viaje_posible_btn_cancel);
		viaje_posible_btn_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//cambiar el viaje a cancelado
				String consulta = "http://codigo.labplc.mx/~mikesaurio/taxi.php?act=viaje&type=update&pk_chofer="+uuid+"&estado=cancelado";
				String querty = doHttpConnection(consulta);
				//http://codigo.labplc.mx/~mikesaurio/taxi.php?act=viaje&type=update&pk_chofer=759f9adc-2a0c-4107-a229-6e277b01c874&estado=cancelado
			
				//cambiar mi estatus a libre
				String consulta2 = "http://codigo.labplc.mx/~mikesaurio/taxi.php?act=pasajero&type=updateStatusChofer&pk="+uuid+"&status=libre";
				String querty2 = doHttpConnection(consulta2);

				
				
				Viaje_posible.this.finish();
			}
		});
		
		viaje_posible_btn_ok = (Button) findViewById(R.id.viaje_posible_btn_ok);
		viaje_posible_btn_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//cambiar el viaje a aceptado
				String consulta = "http://codigo.labplc.mx/~mikesaurio/taxi.php?act=viaje&type=update&pk_chofer="+uuid+"&estado=aceptado";
				String querty = doHttpConnection(consulta);
				
//iniciamos el traking
				
				SharedPreferences prefs = getSharedPreferences("MisPreferenciasChofer",Context.MODE_PRIVATE);
				String uuid = prefs.getString("uuid", null);
				
				Intent intent = new Intent(Viaje_posible.this,MitaxiTripActivity.class);
				intent.putExtra("pk_viaje", pk_viaje);
				intent.putExtra("pk_chofer", uuid);
				intent.putExtra("placa", placa);
				intent.putExtra("origen", origen);
				intent.putExtra("destino", destino);
				intent.putExtra("referencia", referencia);
            	startActivity(intent);
				
			}
		});
		
		
		viaje_posible_iv_origen = (ImageView) findViewById(R.id.viaje_posible_iv_origen);
		viaje_posible_iv_origen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//pinta en googlemaps el origen y mis coordenadas
				Intent intent = new Intent(Viaje_posible.this,MapaOrigenDestino.class);
				intent.putExtra("punto", origen);
				intent.putExtra("direccion", Sorigen);
				startActivity(intent);
			}
		});
		
		viaje_posible_iv_destino = (ImageView) findViewById(R.id.viaje_posible_iv_destino);
		viaje_posible_iv_destino.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//pinta en googlemaps el destino 
				Intent intent = new Intent(Viaje_posible.this,MapaOrigenDestino.class);
				intent.putExtra("punto", destino);
				intent.putExtra("direccion", Sdestino);
				startActivity(intent);

			}
		});
		
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
