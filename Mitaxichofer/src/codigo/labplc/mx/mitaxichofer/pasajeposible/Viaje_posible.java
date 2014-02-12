package codigo.labplc.mx.mitaxichofer.pasajeposible;

import codigo.labplc.mx.mitaxichofer.R;
import codigo.labplc.mx.mitaxichofer.R.layout;
import codigo.labplc.mx.mitaxichofer.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Viaje_posible extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viaje_posible);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.viaje_posible, menu);
		return true;
	}

}
