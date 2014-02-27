package codigo.labplc.mx.mitaxichofer.tracking;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import codigo.labplc.mx.mitaxichofer.pasajeposible.Viaje_posible;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author mikesaurio
 *
 */
public class SocketConnection {
	/**
	 * Declaraci—n de variables
	 */
	SocketIO socket;//socket para la conecci—n con el servidor
	Activity act;
	
	public SocketConnection(Activity act) {
		this.act=act;
	}


	/**
	 * Metodo que crea la coneccion con el servior
	 * 
	 * @return SocketIO creado y conectado
	 */
	public SocketIO connection() {
		try {
			//cramos el socket
			socket = new SocketIO("http://codigo.labplc.mx:8008");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//hacemos la conexi—n
		socket.connect(new IOCallback() {
			@Override
			public void onMessage(JSONObject json, IOAcknowledge ack) {
				try {
					System.out.println("Servidor dice:" + json.toString(2));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onMessage(String data, IOAcknowledge ack) {
				System.out.println("Servidor dice: " + data);
			}

			@Override
			public void onError(SocketIOException socketIOException) {
				System.out.println("ERROR en socket");
				socketIOException.printStackTrace();
			}

			@Override
			public void onDisconnect() {
				System.out.println("Conexi—n terminada");
			}

			@Override
			public void onConnect() {
				System.out.println("Conexi—n establicida");
			}

			@TargetApi(19)
			@Override
			public void on(String event, IOAcknowledge ack, Object... args) {
			//	System.out.println("Servidor de eventos activa '" + event + "'");
			//	System.out.println("Servidor de eventos activa '" + args[0] + "'");
				try {
					  JSONObject jsonObj = new JSONObject(args[0].toString());
		                String disposicion = jsonObj.getString("disposicion");
		                disposicion  = disposicion.replaceAll(" ", "");
		                if(disposicion.equals("libre")){
		                	 System.out.println("*******" + "Libre"+ "");
		                	 
		                }else if(disposicion.equals("ocupado")){
		                	 System.out.println("*******" + "ocupado"+ "");
		                	 
		                }else if(disposicion.equals("pendiente")){
		                	 System.out.println("*******" + "pendiente"+ "");
		                	 
		                	 //traer el viaje 
		             		SharedPreferences prefs = act.getSharedPreferences("MisPreferenciasChofer",Context.MODE_PRIVATE);
		            		String uuid = prefs.getString("uuid", null);
		             		String consulta = "http://codigo.labplc.mx/~mikesaurio/taxi.php?act=viaje&type=get&pk_chofer="+uuid;
		    				String querty = doHttpConnection(consulta);
		    				Log.d("*****************zaza", querty+"");
		    						                	 
		    				//mostramos los resultados al chofer en el viaje
		    				JSONObject jObj = new JSONObject(querty);
		    				String pk_viaje = jObj.getString("pk_viaje");	    				
		    				String origen = jObj.getString("origen");
		    				String destino = jObj.getString("destino");
		    				String pasajeros = jObj.getString("pasajeros");
		    				String mascotas = jObj.getString("mascotas");
		    				String discapacitados = jObj.getString("discapacitados");
		    				String bicicleta = jObj.getString("bicicleta");
		    				String placa = jObj.getString("placa");
		    				
		    				
		    				Intent intent = new Intent(act,Viaje_posible.class);
		    				intent.putExtra("pk_viaje", pk_viaje);
		    				intent.putExtra("origen", origen);
		    				intent.putExtra("destino", destino);
		    				intent.putExtra("pasajeros", pasajeros);
		    				intent.putExtra("mascotas", mascotas);
		    				intent.putExtra("discapacitados", discapacitados);
		    				intent.putExtra("bicicleta", bicicleta);
		    				intent.putExtra("placa", placa);

		    				
		    		
		    				
		    				//cambiar a ocupado
		    				String consulta2 = "http://codigo.labplc.mx/~mikesaurio/taxi.php?act=pasajero&type=updateStatusChofer&pk="+uuid+"&status=ocupado";
		    				String querty2 = doHttpConnection(consulta2);
		    				
		    				act.startActivity(intent);
		    			
		    				
		    				
		    				
		                }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
				
			}
		});
		//regresamos el socket ya construido y conectado
		return socket;

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
