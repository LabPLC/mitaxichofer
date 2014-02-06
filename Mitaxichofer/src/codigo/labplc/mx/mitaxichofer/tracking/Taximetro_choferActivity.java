package codigo.labplc.mx.mitaxichofer.tracking;
import io.socket.SocketIO;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import codigo.labplc.mx.mitaxichofer.R;
import codigo.labplc.mx.mitaxichofer.tracking.servicios.ServicioGeolocalizacion;
/**
 * 
 * @author mikesaurio
 *
 */
public class Taximetro_choferActivity extends Activity {
	
	
	/**
	 * Declaración de variables
	 */
	TextView tvCoordenadas;//se mostrarán las coordenadas y la distancia acumulada
	SocketIO socket;//socket para la conección con el servidor
	Button btnRunService ;
	Button btnStopService;
	private LocationManager mLocationManager;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taxi);
		socket=new SocketConnection().connection();
		
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		/**
		 * instancias y escuchas
		 */
		tvCoordenadas = (TextView) findViewById(R.id.tvCoordenadas);
		btnRunService = (Button) findViewById(R.id.btnRunService);
		btnRunService.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					showDialogGPS("GPS apagado", "¿Deseas activarlo?");		
				}else{
					/**
					 * Se inicia el servicio de geolocalización
					 */
					ServicioGeolocalizacion.taxiActivity = Taximetro_choferActivity.this;
					startService(new Intent(Taximetro_choferActivity.this,ServicioGeolocalizacion.class));
					bloquearBoton(true);
				}
				
			}
		});
		btnStopService = (Button) findViewById(R.id.btnStopService);
		btnStopService.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				/**
				 * Se detiene el servicio de geolocalización
				 */
				bloquearBoton(false);
				stopService(new Intent(Taximetro_choferActivity.this,ServicioGeolocalizacion.class));
				tvCoordenadas.setText(getString(R.string.esperando));//regresamos a texto default

			}
		});
	}
	
	/**
	 * bloquea el boton que inicia el servicio
	 * @param b (boolean)
	 */
	public void bloquearBoton(boolean b){
		if(b==true){
			btnRunService.setEnabled(false);
			btnStopService.setEnabled(true);
		}else{
			btnRunService.setEnabled(true);
			btnStopService.setEnabled(false);
		}
		
	}

	/**
	 * manejo de transmiciones
	 */
	private BroadcastReceiver onBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctxt, Intent i) {
			
				//blanqueamos el texto de las coordenadas si esta el texto default
				if (tvCoordenadas.getText().equals(getString(R.string.esperando))) {
					tvCoordenadas.setText("");
				}
				
				String datos = i.getStringExtra("coordenadas");//obtenemos las coordenadas envidas del servicioGeolocalización
				String[] tokens = datos.split(";");//separamos por tocken
				tvCoordenadas.append("latitud: " + tokens[0]+ " longitud: " + tokens[1] + " distancia: "+ tokens[2]);
				tvCoordenadas.append("\n");//agregamos salto de linea
	
				JSONObject cadena = new JSONObject(); // Creamos un objeto de tipo JSON
				try {
					SharedPreferences prefs = getSharedPreferences("MisPreferenciasChofer",Context.MODE_PRIVATE);
					String uuid = prefs.getString("uuid", null);
					// Le asignamos los datos que necesitemos
					cadena.put("lat", tokens[0]); //latitud
					cadena.put("lng", tokens[1]);//longitud
					cadena.put("uuid", uuid);

				} catch (JSONException e) {
					e.printStackTrace();
				}

				//generamos la conexión con el servidor y mandamos las coordenads
				socket.emit("locmsg", cadena);
		}
	};

	@Override
	protected void onPause() {
		unregisterReceiver(onBroadcast);
		super.onPause();
	}

	@Override
	protected void onResume() {
		registerReceiver(onBroadcast, new IntentFilter("key"));
		super.onResume();
	}
	
	
	
	/**
	 * Muestra diálogo en dado caso que el GPS esté apagado
	 * 
	 * @param titulo Título del diálogo
	 * @param message Mensaje del diálogo
	 */
	public void showDialogGPS(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Taximetro_choferActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
		builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				startActivity(settingsIntent);
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
	}
	
	
}
