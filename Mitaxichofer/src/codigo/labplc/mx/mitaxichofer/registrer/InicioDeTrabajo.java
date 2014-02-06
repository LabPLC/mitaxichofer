package codigo.labplc.mx.mitaxichofer.registrer;

import java.io.File;
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
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import codigo.labplc.mx.mitaxichofer.R;
import codigo.labplc.mx.mitaxichofer.registrer.beans.AutoBean;
import codigo.labplc.mx.mitaxichofer.tracking.Taximetro_choferActivity;

public class InicioDeTrabajo extends Activity {
	EditText inicio_de_trabajo_et_placa;
	private AlertDialog customDialog= null;	//Creamos el dialogo generico
	AutoBean autoBean = null;
	private LinearLayout inicio_de_trabajo_ll_carro;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inicio_de_trabajo);
		
		inicio_de_trabajo_ll_carro= (LinearLayout)findViewById(R.id.inicio_de_trabajo_ll_carro);
//imagen del chofer		
		ImageView	inicio_de_trabajo_iv_foto =(ImageView)findViewById(R.id.inicio_de_trabajo_iv_foto);

		SharedPreferences prefs = getSharedPreferences("MisPreferenciasChofer",Context.MODE_PRIVATE);
		String foto = prefs.getString("foto", null);
		File imgFile = new  File(foto);
		if(imgFile.exists()){
			
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    Matrix mat = new Matrix();
	        mat.postRotate(-90);
	        Bitmap bMapRotate = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), mat, true);
		    inicio_de_trabajo_iv_foto.setImageBitmap(bMapRotate);

		}
//nombre del chofer
		TextView inicio_de_trabajo_tv_nombre =(TextView)findViewById(R.id.inicio_de_trabajo_tv_nombre);
		String nombre = prefs.getString("nombre", null);
		nombre = nombre.replace('+', ' ');
		String apePat = prefs.getString("apePat", null);
		String apeMat = prefs.getString("apeMat", null);
		inicio_de_trabajo_tv_nombre.append(nombre +" "+apePat + " "+ apeMat);
		
//licencia chofer
		TextView inicio_de_trabajo_tv_licencia = (TextView)findViewById(R.id.inicio_de_trabajo_tv_licencia);
		String licencia = prefs.getString("licencia", null);
		inicio_de_trabajo_tv_licencia.append(licencia);
		
//telefono chofer
		TextView inicio_de_trabajo_tv_telefono = (TextView)findViewById(R.id.inicio_de_trabajo_tv_telefono);
		String telefono = prefs.getString("tel", null);
		inicio_de_trabajo_tv_telefono.append(telefono);
		
		inicio_de_trabajo_et_placa = (EditText)findViewById(R.id.inicio_de_trabajo_et_placa);
		inicio_de_trabajo_et_placa.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				 String placaIn = inicio_de_trabajo_et_placa.getText().toString();

		            if (placaIn.length() ==6) {
		            	//buscando datos del carro en el servidor
		            	try {
		            		//cerramos el teclado
		            		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		            		imm.hideSoftInputFromWindow(inicio_de_trabajo_et_placa.getWindowToken(), 0);
		            		mostrarDAtosConcesion(InicioDeTrabajo.this,placaIn);
		            		
						} catch (JSONException e) {
							Toast.makeText(getBaseContext(), "Taxi no valido", Toast.LENGTH_LONG).show();
		          	    	  inicio_de_trabajo_et_placa.setText("");
						}
		            }
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		

		
	}

	/**
	 * Dialogo para que el usuario reporte una anomalia en el taxi o el chofer
	 *
	 * @param Activity (actividad que llama al di‡logo)
	 * @return Dialog (regresa el dialogo creado)
	 * @throws JSONException 
	 **/
	public boolean mostrarDAtosConcesion(final Activity activity,String placa) throws JSONException
	{
		autoBean = new AutoBean();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		final  View view = activity.getLayoutInflater().inflate(R.layout.dialogo_datos_correctos, null);
	    builder.setView(view);
	    builder.setCancelable(false);
	   
	    String Sjson=  doHttpConnection("http://mikesaurio.dev.datos.labplc.mx/movilidad/taxis/"+placa+".json");
	    String marca="",submarca="",anio="";
	    
	      JSONObject json= (JSONObject) new JSONTokener(Sjson).nextValue();
	      JSONObject json2 = json.getJSONObject("Taxi");
	      JSONObject jsonResponse = new JSONObject(json2.toString());
	      JSONArray cast = jsonResponse.getJSONArray("concesion");
	      for (int i=0; i<cast.length(); i++) {
	          	JSONObject oneObject = cast.getJSONObject(i);
				 try {
					 marca = (String) oneObject.getString("marca");
					 autoBean.setMarca(marca);
					 submarca = (String)  oneObject.getString("submarca");
					 autoBean.setSubmarca(submarca);
					 anio = (String)  oneObject.getString("anio");
					 autoBean.setAnio(anio);
				 } catch (JSONException e) { return false;}
	      }
			    TextView dialogo_datos_correctos_tv_marca =(TextView) view.findViewById(R.id.dialogo_datos_correctos_tv_marca);
			    dialogo_datos_correctos_tv_marca.append(marca);
			   
			    TextView dialogo_datos_correctos_tv_submarca =(TextView) view.findViewById(R.id.dialogo_datos_correctos_tv_submarca);
			    dialogo_datos_correctos_tv_submarca.append(submarca);
			    
			    TextView dialogo_datos_correctos_tv_ano =(TextView) view.findViewById(R.id.dialogo_datos_correctos_tv_ano);
			    dialogo_datos_correctos_tv_ano.append(anio);
			   
		       //escucha del boton aceptar
		       Button dialogo_datos_correctos_btnAceptar = (Button) view.findViewById(R.id.dialogo_datos_correctos_btnAceptar);
		       dialogo_datos_correctos_btnAceptar.setOnClickListener(new OnClickListener() {
		            
		           @Override
		           public void onClick(View v)
		           {
		        	   
		        	RadioGroup radioSonPlacas = (RadioGroup) view.findViewById(R.id.dialogo_datos_correctos_radioSonPlacas);//radioGroup  placas correctas
		           	int selectedPlaca = radioSonPlacas.getCheckedRadioButtonId();//obtenemos el id del radio seleccionado
		           	RadioButton radioSonPlaca = (RadioButton) view.findViewById(selectedPlaca);//hacemos la instancia de este
		           	
		           	Toast.makeText(activity.getBaseContext(),radioSonPlaca.getText()+"", Toast.LENGTH_SHORT).show();
		           	autoBean.setTipo(radioSonPlaca.getText()+"");
		               customDialog.dismiss();  //cerramos el di‡logo
		               
		               inicio_de_trabajo_ll_carro.removeAllViews();
		               llenarAutomovil();
		           }
		       });
		       
		       //escucha del boton cancelar
		       Button dialogo_datos_correctos_btnCancelar = (Button) view.findViewById(R.id.dialogo_datos_correctos_btnCancelar);
		       dialogo_datos_correctos_btnCancelar.setOnClickListener(new OnClickListener() {
		            
		           @Override
		           public void onClick(View view)
		           {
		               customDialog.dismiss();  //cerramos el di‡logo  
		           }
		       });
		       
		    (customDialog=builder.create()).show();// return customDialog;//regresamos el di‡logo
		    return true;
}
	
	
	public void llenarAutomovil(){
		
		View view = getLayoutInflater().inflate(R.layout.row_coche_inicio_de_trabajo, null);
		LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		view.setLayoutParams(lp);
		TextView row_coche_inicio_de_secion_tv_marca =(TextView)view.findViewById(R.id.row_coche_inicio_de_secion_tv_marca);
		row_coche_inicio_de_secion_tv_marca.append(autoBean.getMarca());
		
		TextView row_coche_inicio_de_secion_tv_submarca =(TextView)view.findViewById(R.id.row_coche_inicio_de_secion_tv_submarca);
		row_coche_inicio_de_secion_tv_submarca.append(autoBean.getSubmarca());
		
		TextView row_coche_inicio_de_secion_tv_ano =(TextView)view.findViewById(R.id.row_coche_inicio_de_secion_tv_ano);
		row_coche_inicio_de_secion_tv_ano.append(autoBean.getAnio());
		
		TextView row_coche_inicio_de_secion_tv_tipo =(TextView)view.findViewById(R.id.row_coche_inicio_de_secion_tv_tipo);
		row_coche_inicio_de_secion_tv_tipo.append(autoBean.getTipo());
		
		ImageView row_coche_inicio_de_secion_iv_discapacitado =(ImageView) view.findViewById(R.id.row_coche_inicio_de_secion_iv_discapacitado);
		row_coche_inicio_de_secion_iv_discapacitado.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		ImageView row_coche_inicio_de_secion_iv_mascota =(ImageView) view.findViewById(R.id.row_coche_inicio_de_secion_iv_mascota);
		row_coche_inicio_de_secion_iv_mascota.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		ImageView row_coche_inicio_de_secion_iv_bici =(ImageView) view.findViewById(R.id.row_coche_inicio_de_secion_iv_bici);
		row_coche_inicio_de_secion_iv_bici.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		Button row_coche_inicio_de_secion_btn_iniciar_dia =(Button) view.findViewById(R.id.row_coche_inicio_de_secion_btn_iniciar_dia);
		row_coche_inicio_de_secion_btn_iniciar_dia.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(InicioDeTrabajo.this,Taximetro_choferActivity.class));
				InicioDeTrabajo.this.finish();
				
			}
		});
		
		inicio_de_trabajo_ll_carro.addView(view);
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
