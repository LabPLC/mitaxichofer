package codigo.labplc.mx.mitaxichofer.registrer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import codigo.labplc.mx.mitaxichofer.R;
import codigo.labplc.mx.mitaxichofer.registrer.beans.ChoferBean;
import codigo.labplc.mx.mitaxichofer.registrer.dialogos.DatePickerDailog;
import codigo.labplc.mx.mitaxichofer.registrer.dialogos.Dialogues;
import codigo.labplc.mx.mitaxichofer.registrer.dialogos.IdiomasDialogActivity;
import codigo.labplc.mx.mitaxichofer.registrer.networks.NetworkUtils;
import codigo.labplc.mx.mitaxichofer.registrer.utils.EditTextValidator;
import codigo.labplc.mx.mitaxichofer.registrer.utils.RegularExpressions;
import codigo.labplc.mx.mitaxichofer.tracking.Taximetro_choferActivity;

public class RegistroChoferActivity extends Activity {

	private Button mitaxiregistermanually_btn_antiguedad;
	private Button mitaxiregistermanually_btn_vigencia;
	private Button mitaxiregistermanually_btn_foto;
	private Button mitaxiregistermanually_btn_ok;
	private Button mitaxiregistermanually_btn_idiomas;
	private EditText mitaxiregistermanually_et_infousername;
	private EditText mitaxiregistermanually_et_infouserappat;
	private EditText mitaxiregistermanually_et_infouserapmat;
	private EditText mitaxiregistermanually_et_infousertel;
	private EditText mitaxiregistermanually_et_infouserlicencia;
	private String txtAntiguedad=null;
	private String txtVigencia=null;
	private String txtIdiomas=null;
	static final int DATE_DIALOG_ID = 0;
	private  Calendar dateandtime;
    private String foto;
    ChoferBean chofer;
    private boolean[] listHasErrorEditText = {
			false, false, false, false,false
	};
    


    public static int count =0;
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mitaxi_register_chofer_manually);
     

		
		
		dateandtime = Calendar.getInstance();//instancia del pais e idioma al calendario
		foto = Environment.getExternalStorageDirectory() + "/imagen"+ getCode()+".jpg";//creamos la ruta de la foto con codigo œnico
		
		mitaxiregistermanually_et_infousername = (EditText) findViewById(R.id.mitaxiregistermanually_et_infousername);
		mitaxiregistermanually_et_infouserappat = (EditText) findViewById(R.id.mitaxiregistermanually_et_infouserappat);
		mitaxiregistermanually_et_infouserapmat = (EditText) findViewById(R.id.mitaxiregistermanually_et_infouserapmat);
		mitaxiregistermanually_et_infousertel = (EditText) findViewById(R.id.mitaxiregistermanually_et_infousertel);
		mitaxiregistermanually_et_infouserlicencia = (EditText)findViewById(R.id.mitaxiregistermanually_et_infouserlicencia);
		
		
		mitaxiregistermanually_et_infousername.setTag(RegularExpressions.KEY_IS_STRING);
		mitaxiregistermanually_et_infouserappat.setTag(RegularExpressions.KEY_IS_STRING);
		mitaxiregistermanually_et_infouserapmat.setTag(RegularExpressions.KEY_IS_STRING);
		mitaxiregistermanually_et_infousertel.setTag(RegularExpressions.KEY_IS_NUMBER);
		mitaxiregistermanually_et_infouserlicencia.setTag(RegularExpressions.KEY_IS_NUMBER);

		
		mitaxiregistermanually_et_infousername.addTextChangedListener(new EditTextValidator().new CurrencyTextWatcher(
				getBaseContext(), mitaxiregistermanually_et_infousername, listHasErrorEditText, 0));
		mitaxiregistermanually_et_infouserappat.addTextChangedListener(new EditTextValidator().new CurrencyTextWatcher(
				getBaseContext(), mitaxiregistermanually_et_infouserappat, listHasErrorEditText, 1));
		mitaxiregistermanually_et_infouserapmat.addTextChangedListener(new EditTextValidator().new CurrencyTextWatcher(
				getBaseContext(), mitaxiregistermanually_et_infouserapmat, listHasErrorEditText, 2));
		mitaxiregistermanually_et_infousertel.addTextChangedListener(new EditTextValidator().new CurrencyTextWatcher(
				getBaseContext(), mitaxiregistermanually_et_infousertel, listHasErrorEditText, 3));
		mitaxiregistermanually_et_infouserlicencia.addTextChangedListener(new EditTextValidator().new CurrencyTextWatcher(
				getBaseContext(), mitaxiregistermanually_et_infouserlicencia, listHasErrorEditText, 4));
		
		
		//creamos la instancia y escucha del boton que genera la antiguedad del chofer
		mitaxiregistermanually_btn_antiguedad =(Button) findViewById(R.id.mitaxiregistermanually_btn_antiguedad);
		mitaxiregistermanually_btn_antiguedad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				DatePickerDailog dp = new DatePickerDailog(RegistroChoferActivity.this,
						dateandtime, new DatePickerDailog.DatePickerListner() {

							@Override
							public void OnDoneButton(Dialog datedialog, Calendar c) {
								datedialog.dismiss();
								dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
								dateandtime.set(Calendar.MONTH,c.get(Calendar.MONTH));
								dateandtime.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH));
								
								((Button)v).setText(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));
								txtAntiguedad = ((Button)v).getText().toString();
							}

							@Override
							public void OnCancelButton(Dialog datedialog) {
								// TODO Auto-generated method stub
								datedialog.dismiss();
							}
						});
				dp.show();
				
			}
		});
	
		//creamos la instancia y escucha del boton que genera la vigencia del chofer
		mitaxiregistermanually_btn_vigencia= (Button) findViewById(R.id.mitaxiregistermanually_btn_vigencia); 
		mitaxiregistermanually_btn_vigencia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				DatePickerDailog dp = new DatePickerDailog(RegistroChoferActivity.this,
						dateandtime, new DatePickerDailog.DatePickerListner() {

							@Override
							public void OnDoneButton(Dialog datedialog, Calendar c) {
								datedialog.dismiss();
								dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
								dateandtime.set(Calendar.MONTH,c.get(Calendar.MONTH));
								dateandtime.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH));
								
								((Button)v).setText(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));
								txtVigencia = ((Button)v).getText().toString();
							}

							@Override
							public void OnCancelButton(Dialog datedialog) {
								// TODO Auto-generated method stub
								datedialog.dismiss();
							}
						});
				dp.show();
				
			}
		});
		
		//creamos la instancia y escucha del boton que genera la foto del chofer
		mitaxiregistermanually_btn_foto = (Button) findViewById(R.id.mitaxiregistermanually_btn_foto);
		mitaxiregistermanually_btn_foto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri output = Uri.fromFile(new File(foto));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                startActivityForResult(intent, 1); // 1 para la camara, 2 para la galeria
				
			}
		});
		
		
		mitaxiregistermanually_btn_ok = (Button) findViewById(R.id.mitaxiregistermanually_btn_ok);
		mitaxiregistermanually_btn_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isAnyEditTextEmpty()) {
					if (!hasErrorEditText()) {
						if(!isValidateButtons()){
						if (NetworkUtils.isNetworkConnectionOk(getBaseContext())) {
							try {
								saveUserInfo();
							} catch (JSONException e) {
								Dialogues.Toast(getApplicationContext(),"error fatal :(",Toast.LENGTH_LONG);
							}
						} else {
							Dialogues.Toast(getApplicationContext(),"no tienes internet",Toast.LENGTH_LONG);
						}
						}else{
							Dialogues.Toast(getApplicationContext(),getString(R.string.edittext_emtpy)+1,Toast.LENGTH_LONG);
						}
					} else {
						Dialogues.Toast(getApplicationContext(),getString(R.string.edittext_wrong_info),Toast.LENGTH_LONG);
					}
				} else {
					Dialogues.Toast(getApplicationContext(),
							getString(R.string.edittext_emtpy), Toast.LENGTH_LONG);
				}

			}
		});
		
		
		mitaxiregistermanually_btn_idiomas = (Button) findViewById(R.id.mitaxiregistermanually_btn_idiomas);
		mitaxiregistermanually_btn_idiomas.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegistroChoferActivity.this, IdiomasDialogActivity.class);
	                startActivityForResult(intent, 2); // 1 para la camara, 2 para la galeria
			}
			
		});
		
	}
	
	public void saveUserInfo() throws JSONException {

		chofer = new ChoferBean();
		chofer.setNombre(mitaxiregistermanually_et_infousername.getText().toString().replaceAll(" ", "+"));
		chofer.setAppat(mitaxiregistermanually_et_infouserappat.getText().toString());
		chofer.setApmat(mitaxiregistermanually_et_infouserapmat.getText().toString());
		chofer.setTelefono(mitaxiregistermanually_et_infousertel.getText().toString());
		chofer.setLicencia(mitaxiregistermanually_et_infouserlicencia.getText().toString());
		chofer.setAntiguedad(txtAntiguedad);
		chofer.setVigencia(txtVigencia);
		chofer.setIdioma(txtIdiomas);
		chofer.setFoto(foto);
		
		

		
		UploaderFoto nuevaTarea = new UploaderFoto();
        nuevaTarea.execute(foto);
	}
	
	
	/**
	  * Metodo privado que genera un codigo unico segun la hora y fecha del sistema
	  * @return photoCode 
	  * */
	  @SuppressLint("SimpleDateFormat")
	  private String getCode()
	  {
	   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
	   String date = dateFormat.format(new Date() );
	   String photoCode = "pic_" + date;  
	   return photoCode;
	  }
	  @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (requestCode == 1) { 
		  File file = new File(foto);
	        if (file.exists()) {
	        	mitaxiregistermanually_btn_foto.setText("OK");
	        }
	        else
	        	mitaxiregistermanually_btn_foto.setText("falla");
		  }
		  if (requestCode == 2) {
			  if(resultCode==RESULT_OK){
				  txtIdiomas=  data.getStringExtra("result");
//				  Toast.makeText(getApplicationContext(), txtIdiomas+"",	Toast.LENGTH_LONG).show();
				  mitaxiregistermanually_btn_idiomas.setText("OK");
			  }
		  }
	    }
	  
	  
	  class UploaderFoto extends AsyncTask<String, Void, Void>{
			 
	        ProgressDialog pDialog;
	        String miFoto = "";
			private String resultado;
	        public static final int HTTP_TIMEOUT = 30 * 1000;
	 
	        @SuppressWarnings("deprecation")
			@Override
			protected Void doInBackground(String... params) {
				 miFoto = (String) params[0];
		            	 try
		            	 
		                 {	 
		            		 
		            		 System.setProperty("http.keepAlive", "false");	
		            		 HttpClient httpclient = new DefaultHttpClient();
		            		 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		            		 StrictMode.setThreadPolicy(policy);
		                 	final HttpParams par = httpclient.getParams();
		         			HttpConnectionParams.setConnectionTimeout(par, HTTP_TIMEOUT);
		         			HttpConnectionParams.setSoTimeout(par, HTTP_TIMEOUT);
		         			ConnManagerParams.setTimeout(par, HTTP_TIMEOUT);
		         			//HttpPost httppost = new HttpPost("http://lionteamsoft.comli.com/subir.php");
		         			HttpPost httppost = new HttpPost("http://datos.labplc.mx/~mikesaurio/taxi.php?act=chofer&type=add");		
		     				MultipartEntity entity = new MultipartEntity();
		     				
		     				entity.addPart("nombre",  new StringBody(chofer.getNombre()+""));
		     				entity.addPart("apePat",  new StringBody(chofer.getAppat()+""));
		     				entity.addPart("apeMat",  new StringBody(chofer.getApmat()+""));
		     				entity.addPart("tel",  new StringBody(chofer.getTelefono()+""));
		     				entity.addPart("licencia",  new StringBody(chofer.getLicencia()+""));
		     				entity.addPart("vigencia",  new StringBody(chofer.getVigencia()+""));
		     				entity.addPart("antiguedad",  new StringBody(chofer.getAntiguedad()+""));
		     				entity.addPart("idioma",  new StringBody(chofer.getIdioma()+""));
		     				
		     				
		     				File file = new File(miFoto);
		     				entity.addPart("foto", new FileBody(file));
		     				
		     				
		     				System.setProperty("http.keepAlive", "false");				  				  														 									 
		     				httppost.setEntity(entity);
		     				HttpResponse response = httpclient.execute(httppost);
		     				BufferedReader	in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		     				StringBuffer sb = new StringBuffer("");
		     				String linea = "";
		     				String NL = System.getProperty("line.separator");
		     					
		     				while((linea = in.readLine())!=null){
		     					sb.append(linea + NL);			
		     				}
		     				in.close();
		     				resultado = sb.toString();
		     				httpclient = null;
		     				response = null;	
		     			Log.d("RESULTADO", resultado);	

		     			if (resultado != null) {
		    				//Dialogues.Toast(getApplicationContext(), ws_result, Toast.LENGTH_LONG);
		    				
		    				//revisamos json
		    					String errorJson;
		    					String successJson;
		    					String pk_chofer;
		    					 
		    					 JSONObject json= (JSONObject) new JSONTokener(resultado).nextValue();
		    				      JSONObject json2 = json.getJSONObject("message");
		    					 try {
		    						 errorJson = (String) json2.get("error");
		    					 } catch (JSONException e) { errorJson = null; }
		    					 try {
		    						 successJson = (String) json2.get("success");
		    						 pk_chofer = (String) json2.get("pk_appchofer");
		    					 } catch (JSONException e) { successJson = null;pk_chofer = null;}
		    					 
		    				//	Dialogues.Toast(getApplicationContext(), "query1: "+ errorJson+"", Toast.LENGTH_LONG);
		    				// Dialogues.Toast(getApplicationContext(), "query2: "+ successJson+".."+ pk_pasajero + "", Toast.LENGTH_LONG);
		    				    
		    						if(pk_chofer != null){
		    							
		    							chofer.setUUID(pk_chofer);//agregamos el UUID del usuario
		    						
		    							savePreferences(chofer); //guardamos todo en preferencias
		    							
		    							Intent intent = new Intent(RegistroChoferActivity.this, InicioDeTrabajo.class);
		    							startActivity(intent);
		    							RegistroChoferActivity.this.finish();
		    		
		    						}else if(errorJson != null){
		    							//aqui cachamos el tipo de error
		    						}	 
		    							} else {
		    				Dialogues.Toast(getApplicationContext(), getString(R.string.transaction_wrong), Toast.LENGTH_LONG);
		    			}
		 
		     			 
		     				
		                 }
		                 catch(Exception e)
		                 {
		                	 e.printStackTrace();
		                 } 
		                 return null;
			}
	 
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(RegistroChoferActivity.this);
	            pDialog.setMessage("Subiendo la informaci—n, espere." );
	            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            pDialog.setCancelable(true);
	            pDialog.show();
	        }
	        
	        protected void onPostExecute(Void result) {
	            super.onPostExecute(result);
	            pDialog.dismiss();
	          //  RegistroChoferActivity.this.finish();
	        }

			
	    }
	  
	  
	  /**
		 * Verifica si alguno de los campos {@link EditText} con la informaci—n del
		 * usuario est‡ vacio.
		 * 
		 * @return (boolean) <b>true</b> si esta vacio <b>false</b> si no esta vacio
		 */
		public boolean isAnyEditTextEmpty() {
			boolean empty = false;

			if (EditTextValidator.isEditTextEmpty(mitaxiregistermanually_et_infousername))
				empty = true;
			if (EditTextValidator.isEditTextEmpty(mitaxiregistermanually_et_infouserappat))
				empty = true;
			if (EditTextValidator.isEditTextEmpty(mitaxiregistermanually_et_infouserapmat))
				empty = true;
			if (EditTextValidator.isEditTextEmpty(mitaxiregistermanually_et_infousertel))
				empty = true;
			if (EditTextValidator.isEditTextEmpty(mitaxiregistermanually_et_infouserlicencia))
				empty = true;

			return empty;
		}

		public boolean hasErrorEditText() {
			for(boolean hasError : listHasErrorEditText)
				if(hasError)
					return true;
			return false;
		}
		
		  
		  /**
			 * Verifica si alguno de los campos {@link EditText} con la informaci—n del
			 * usuario est‡ vacio.
			 * 
			 * @return (boolean) <b>true</b> si esta vacio <b>false</b> si no esta vacio
			 */
			public boolean isValidateButtons() {
				boolean empty = false;

				if (txtAntiguedad==null)
					empty = true;
				if (txtVigencia==null)
					empty = true;
				if (txtIdiomas==null)
					empty = true;
				
				Dialogues.Toast(getApplicationContext(),txtAntiguedad+" . "+txtVigencia+" . "+txtIdiomas+"",Toast.LENGTH_LONG);
				return empty;
			}
			/**
			 * Guarda las preferencias del usuario en el SO
			 * 
			 * @param (user) bean que contiene los datos del usuario
			 * @return void
			 */
			public void savePreferences(ChoferBean chofer) {
				SharedPreferences prefs = getSharedPreferences("MisPreferenciasChofer", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("uuid",chofer.getUUID());
				editor.putString("nombre",chofer.getNombre());
				editor.putString("apePat",chofer.getAppat());
				editor.putString("apeMat",chofer.getApmat());
				editor.putString("tel",chofer.getTelefono());
				editor.putString("licencia",chofer.getLicencia());
				editor.putString("foto",chofer.getFoto());
				editor.commit();
				
			}
			
}
