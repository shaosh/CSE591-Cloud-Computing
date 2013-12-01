package com.cc.fileSys.client.UI;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cc.fileSys.client.R;
import com.cc.fileSys.client.entity.DisplayFile;
import com.cc.fileSys.client.function.Command;
import com.cc.fileSys.client.function.DeviceClient;
import com.cc.fileSys.client.function.DeviceServer;
import com.cc.fileSys.client.parameter.Variable;

public class DownloadActivity extends Activity {
	public String filename;
	public String owner;
	Button downloadButton = null;

	public static ProgressDialog pd;
	String[] fileInfo;
	MyCustomAdapter fileAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		new ShowClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		new MyServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		downloadButton = (Button) findViewById(R.id.downloadButton);

		downloadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pd = new ProgressDialog(DownloadActivity.this);
				pd.setCanceledOnTouchOutside(false);
				pd.setMessage("Downloading...Please wait...");
				if (!pd.isShowing()) {
					pd.show();
				}

				new MyClient().execute();

			}
		});
	}

	private class MyCustomAdapter extends ArrayAdapter<DisplayFile> {

		private ArrayList<DisplayFile> fileList;

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<DisplayFile> countryList) {
			super(context, textViewResourceId, countryList);
			this.fileList = new ArrayList<DisplayFile>();
			this.fileList.addAll(countryList);
		}

		private class ViewHolder {
			TextView code;
			CheckBox name;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.file_info, null);

				holder = new ViewHolder();
				holder.code = (TextView) convertView.findViewById(R.id.code);
				holder.name = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				convertView.setTag(holder);

				holder.name.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						DisplayFile file = (DisplayFile) cb.getTag();
						Toast.makeText(
								getApplicationContext(),
								"Clicked on Checkbox: " + cb.getText() + " is "
										+ cb.isChecked(), Toast.LENGTH_LONG)
								.show();
						filename = file.getName();
						owner = file.getCode();
						file.setSelected(cb.isChecked());
					}
				});
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}

			DisplayFile country = fileList.get(position);
			holder.code.setText(" (" + country.getCode() + ")");
			holder.name.setText(country.getName());
			holder.name.setChecked(country.isSelected());
			holder.name.setTag(country);

			return convertView;

		}

	}

	class MyServer extends AsyncTask<Void, Void, Boolean> {

		private Exception exception;

		protected Boolean doInBackground(Void... x) {
			try {
				Log.d("lalala", "Starting server...\n");
				DeviceServer server = new DeviceServer();
				server.init();
				System.out.println("Server Started");
			}
			catch (Exception e) {
				this.exception = e;
				e.getMessage();
			}
			return true;
		}

		protected void onPostExecute() {
			// TODO: check this.exception
			pd.dismiss();
			pd.cancel();
			Toast.makeText(getApplicationContext(),
					"Download file successfully.", Toast.LENGTH_LONG).show();
		}
	}

	class MyClient extends AsyncTask<Void, Void, Boolean> {
		boolean results = false;

		protected Boolean doInBackground(Void... x) {
			try {
				Log.d("lalala", "Client is working...\n");
				DeviceClient.downloadFile(filename, owner, Variable.Latitude,
						Variable.Longitude, Variable.vmIP);
				System.out.println("Client is downloading...");
				results = true;
			}
			catch (Exception e) {
				e.getMessage();
			}

			return true;
		}

		protected void onPostExecute(Boolean x) {
			// TODO: check this.exception
			if (results == true) {
				pd.dismiss();
				pd.cancel();
				Toast.makeText(getApplicationContext(),
						"Download file successfully.", Toast.LENGTH_LONG).show();
			}
		}
	}

	class ShowClient extends AsyncTask<Void, Void, Boolean> {
		boolean result = false;

		protected Boolean doInBackground(Void... x) {
			try {
				Log.d("lalala", "Client is working...\n");
				fileInfo = DeviceClient.queryFiles(Variable.username);
				System.out.println("get the list...");
				result = true;
			}
			catch (Exception e) {
				e.getMessage();
			}
			return true;
		}

		protected void onPostExecute(Boolean x) {
			// TODO: check this.exception

			if (result == true) {
				ArrayList<DisplayFile> fileList = new ArrayList<DisplayFile>();
				for (int i = 0; i < fileInfo.length; i++) {
					String fileName = fileInfo[i]
							.split(Command.INTERNAL_DELETMITER)[0];
					String owner = fileInfo[i]
							.split(Command.INTERNAL_DELETMITER)[1];
					// Array list of files
					DisplayFile file = new DisplayFile(owner, fileName, false);
					fileList.add(file);
				}

				// create an ArrayAdaptar from the String Array
				fileAdapter = new MyCustomAdapter(DownloadActivity.this,
						R.layout.file_info, fileList);
				ListView fileListView = (ListView) findViewById(R.id.ownedFileList);
				// Assign adapter to ListView
				fileListView.setAdapter(fileAdapter);
			}
		}
	}

}
