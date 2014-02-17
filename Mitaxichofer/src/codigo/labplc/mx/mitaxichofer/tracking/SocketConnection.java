package codigo.labplc.mx.mitaxichofer.tracking;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;

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
				System.out.println("Servidor de eventos activa '" + args[0] + "'");
				try {
					  JSONObject jsonObj = new JSONObject(args[0].toString());
		                String disposicion = jsonObj.getString("disposicion");
		                disposicion  = disposicion.replaceAll(" ", "");
		                if(disposicion.equals("libre")){
		                	 System.out.println("*******" + "Libre"+ "");
		                	 
		                }else if(disposicion.equals("ocupˆdo")){
		                	 System.out.println("*******" + "ocupado"+ "");
		                	 
		                }else if(disposicion.equals("pendiente")){
		                	 System.out.println("*******" + "pendiente"+ "");
		                	 //tiene que pedir los datos del viaje que se encuentra en la tabla viaje
		        
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
}
