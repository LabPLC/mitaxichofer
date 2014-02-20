package codigo.labplc.mx.mitaxichofer.pasajeposible;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import codigo.labplc.mx.mitaxichofer.R;

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
	private String pk_viaje=null,origen=null,destino=null;
	private String pasajeros= "1";
	private String mascotas,discapacitados,bicicleta;
	private ImageView viaje_posible_iv_destino, viaje_posible_iv_origen;
	
	
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
			origen = jObj.getString("origin_addresses");
			destino = jObj.getString("destination_addresses");
			destino = jObj.getString("destination_addresses");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
		
		//peticion de mi ubicacion al usuario y del usuario al destino
		
		
		viaje_posible_tv_numero_pasajeros = (TextView) findViewById(R.id.viaje_posible_tv_numero_pasajeros);
		viaje_posible_tv_numero_pasajeros.append(pasajeros+"");
		
		viaje_posible_tv_direccion_origen = (TextView) findViewById(R.id.viaje_posible_tv_direccion_origen);
		viaje_posible_tv_direccion_origen.append(origen+"");
		
		viaje_posible_tv_direccion_destino = (TextView) findViewById(R.id.viaje_posible_tv_direccion_destino);
		viaje_posible_tv_direccion_destino.append(destino+"");


		
		
		
		
		viaje_posible_btn_cancel = (Button) findViewById(R.id.viaje_posible_btn_cancel);
		viaje_posible_btn_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//cambiar el viaje a cancelado
				//cambiar mi estatus a libre
				Viaje_posible.this.finish();
			}
		});
		
		viaje_posible_btn_ok = (Button) findViewById(R.id.viaje_posible_btn_ok);
		viaje_posible_btn_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//cambiar el viaje a aceptado
				//tracking
				
			}
		});
		
		
		viaje_posible_iv_origen = (ImageView) findViewById(R.id.viaje_posible_iv_origen);
		viaje_posible_iv_origen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//pinta en googlemaps el origen y mis coordenadas
				
			}
		});
		
		viaje_posible_iv_destino = (ImageView) findViewById(R.id.viaje_posible_iv_destino);
		viaje_posible_iv_destino.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//pinta en googlemaps el destino 
				
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
