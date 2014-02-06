package codigo.labplc.mx.mitaxichofer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import codigo.labplc.mx.mitaxichofer.R;
import codigo.labplc.mx.mitaxichofer.registrer.InicioDeTrabajo;
import codigo.labplc.mx.mitaxichofer.registrer.RegistroChoferActivity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		//solicitamos las preferencias del usuario
		SharedPreferences prefs = getSharedPreferences("MisPreferenciasChofer",Context.MODE_PRIVATE);
		String uuid = prefs.getString("uuid", null);
		Toast.makeText(getApplicationContext(), "uuid: "+uuid , Toast.LENGTH_LONG).show();
		//si aun no tiene datos guardados en preferencias
		if(uuid == null){
			// se llama a la actividad de registro del taximetro

			//Intent intent = new Intent(MainActivity.this,InicioDeTrabajo.class);
			Intent intent = new Intent(MainActivity.this,RegistroChoferActivity.class);
			startActivity(intent);
			this.finish();
		}else{
			//se llama directo a la busqueda de taxista
			Intent intent = new Intent(MainActivity.this,InicioDeTrabajo.class);
			startActivity(intent);
			Toast.makeText(getApplicationContext(), "uuid: "+uuid , Toast.LENGTH_LONG).show();
			this.finish();
		}
		
	}


}
