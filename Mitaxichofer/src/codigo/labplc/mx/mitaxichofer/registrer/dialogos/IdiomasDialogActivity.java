package codigo.labplc.mx.mitaxichofer.registrer.dialogos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import codigo.labplc.mx.mitaxichofer.R;

public class IdiomasDialogActivity extends Activity {
	private ListView myListView;
	private Button getResult;
	private ArrayList<String> idiomasLetrasList = new ArrayList<String>();
	private ArrayList<Integer> idiomasImagenesList = new ArrayList<Integer>();

	MyArrayAdapter myArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_idiomas_dialog);
		initDayOfWeekList();
		myListView = (ListView) findViewById(R.id.idiomas_dialogo_activity_list_idiomas);

		myArrayAdapter = new MyArrayAdapter(this,
				R.layout.activity_idiomas_row, android.R.id.text1,
				idiomasLetrasList);

		myListView.setAdapter(myArrayAdapter);
		myListView.setOnItemClickListener(myOnItemClickListener);

		getResult = (Button) findViewById(R.id.idiomas_dialogo_activity_btn_result);
		getResult.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String result = "";
				List<String> resultList = myArrayAdapter.getCheckedItems();
				for (int i = 0; i < resultList.size(); i++) {
					result += String.valueOf(resultList.get(i));
					if(i!=resultList.size()-1){
						result+=",";
					}
				}
				myArrayAdapter.getCheckedItemPositions().toString();
				//Toast.makeText(getApplicationContext(), result,	Toast.LENGTH_LONG).show();
				Intent intent = getIntent();
	            intent.putExtra("result", result);
	            setResult(RESULT_OK, intent);
	            finish();

			}
		});

	}

	private void initDayOfWeekList() {
		idiomasLetrasList.add("Es");
		idiomasLetrasList.add("En");
		idiomasImagenesList.add(R.drawable.ic_launcher);
		idiomasImagenesList.add(R.drawable.ic_launcher);
	}

	OnItemClickListener myOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			myArrayAdapter.toggleChecked(position);

		}
	};

	private class MyArrayAdapter extends ArrayAdapter<String> {

		private HashMap<Integer, Boolean> myChecked = new HashMap<Integer, Boolean>();

		public MyArrayAdapter(Context context, int resource,int textViewResourceId, List<String> objects) {
			super(context, resource, textViewResourceId, objects);

			for (int i = 0; i < objects.size(); i++) {
				myChecked.put(i, false);
			}
		}

		public void toggleChecked(int position) {
			if (myChecked.get(position)) {
				myChecked.put(position, false);
			} else {
				myChecked.put(position, true);
			}

			notifyDataSetChanged();
		}

		public List<Integer> getCheckedItemPositions() {
			List<Integer> checkedItemPositions = new ArrayList<Integer>();

			for (int i = 0; i < myChecked.size(); i++) {
				if (myChecked.get(i)) {
					(checkedItemPositions).add(i);
				}
			}

			return checkedItemPositions;
		}

		public List<String> getCheckedItems() {
			List<String> checkedItems = new ArrayList<String>();

			for (int i = 0; i < myChecked.size(); i++) {
				if (myChecked.get(i)) {
					(checkedItems).add(idiomasLetrasList.get(i));
				}
			}

			return checkedItems;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.activity_idiomas_row, parent,
						false);
			}
			ImageView imageView =(ImageView) row.findViewById(R.id.idiomas_dialogo_row_img_idioma);
			imageView.setImageDrawable(getResources().getDrawable(idiomasImagenesList.get(position)));
			
			CheckedTextView checkedTextView = (CheckedTextView) row	.findViewById(R.id.idiomas_dialogo_row_tv_idioma);
			checkedTextView.setText(idiomasLetrasList.get(position));
			
			
			Boolean checked = myChecked.get(position);
			if (checked != null) {
				checkedTextView.setChecked(checked);
			}

			return row;
		}

	}
}
