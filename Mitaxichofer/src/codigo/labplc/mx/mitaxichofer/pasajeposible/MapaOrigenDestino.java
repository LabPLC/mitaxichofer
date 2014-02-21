package codigo.labplc.mx.mitaxichofer.pasajeposible;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import codigo.labplc.mx.mitaxichofer.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaOrigenDestino extends Activity  {

	private String punto;
	   // Google Map
    private GoogleMap googleMap;
     private String Spunto[];
	private String direccion;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa_origen_destino);

		Bundle bundle = getIntent().getExtras();
		punto = bundle.getString("punto");
		direccion = bundle.getString("direccion");
		
		punto  = punto.replaceAll("[()]", "");
		String spunto[] = punto.split(",");
		Spunto = spunto;
		
		try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
	}

	    private void initilizeMap() {
	        if (googleMap == null) {
	            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	         // latitude and longitude
	    		double latitude = Double.parseDouble(Spunto[0]);
	    		double longitude = Double.parseDouble(Spunto[1]);

	    		 
	    		// create marker
	    		MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(direccion+"");
	    		// ROSE color icon
	    		marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
	    		CameraPosition cameraPosition = new CameraPosition.Builder().target(
	                    new LatLng(Double.parseDouble(Spunto[0]), Double.parseDouble(Spunto[1]))).zoom(16).build();
	    			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); 
	    		
	    		
	    		// adding marker
	    		googleMap.addMarker(marker);
	    		
	            if (googleMap == null) {
	                Toast.makeText(getApplicationContext(),"Sorry! unable to create maps", Toast.LENGTH_SHORT)
	                        .show();
	            }
	        }
	    }
	 
	    @Override
	    protected void onResume() {
	        super.onResume();
	        initilizeMap();
	    }
	 
}
