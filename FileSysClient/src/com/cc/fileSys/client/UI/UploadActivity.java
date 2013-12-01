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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cc.fileSys.client.R;
import com.cc.fileSys.client.entity.DisplayFile;
import com.cc.fileSys.client.entity.Users;
import com.cc.fileSys.client.function.DeviceClient;
import com.cc.fileSys.client.function.DeviceServer;
import com.cc.fileSys.client.parameter.Variable;

public class UploadActivity extends Activity {
	getExterIP ip = new getExterIP();

	MyCustomAdapter fileAdapter = null;
	MyCustomAdapter2 userAdapter = null;

	Button uploadButton = null;
	Button userSelected = null;

	public static ProgressDialog pd;

	private static String username;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		uploadButton = (Button) findViewById(R.id.shareButton);

		uploadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				pd = new ProgressDialog(UploadActivity.this);
				pd.setCanceledOnTouchOutside(false);
				pd.setMessage("Uploading...Please wait...");
				if (!pd.isShowing()) {
					pd.show();
				}
				new MyClient().execute();

			}
		});

		new ShowClient().execute();

	}

	private void displayListView(String[] users) {

		// Array list of files

		// file = new DisplayFile("QYL", "eminem.mp3", true);
		// fileList.add(file);
		// file = new DisplayFile("QYL", "war3.dmg", true);
		// fileList.add(file);

		// TODO Need query
		// Array list of users
		ArrayList<Users> userList = new ArrayList<Users>();
		for (int i = 0; i < users.length; i++) {
			Users user = new Users("ASU", users[i], false);
			userList.add(user);
		}

		ArrayList<DisplayFile> fileList = new ArrayList<DisplayFile>();
		DisplayFile file = new DisplayFile(Variable.username,
				MainActivity.fileFullPath, true);
		// DisplayFile file = new DisplayFile("QYL", "dota.exe", true);
		fileList.add(file);
		// create an ArrayAdaptar from the String Array
		fileAdapter = new MyCustomAdapter(this, R.layout.file_info, fileList);
		ListView fileListView = (ListView) findViewById(R.id.listView1);
		// Assign adapter to ListView
		fileListView.setAdapter(fileAdapter);

		userAdapter = new MyCustomAdapter2(this, R.layout.user_info, userList);
		ListView userListView = (ListView) findViewById(R.id.listView2);
		// Assign adapter to ListView
		userListView.setAdapter(userAdapter);

		fileListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				DisplayFile file = (DisplayFile) parent
						.getItemAtPosition(position);
				// Toast.makeText(getApplicationContext(),
				// "Clicked on Row: " + file.getName(), Toast.LENGTH_LONG)
				// .show();
			}
		});

		userListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				// Users users = (Users) parent.getItemAtPosition(position);
				// username = users.getName();
				// Toast.makeText(getApplicationContext(),
				// "Clicked on Row: " + file.getName(), Toast.LENGTH_LONG)
				// .show();
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
						DisplayFile country = (DisplayFile) cb.getTag();
						Toast.makeText(
								getApplicationContext(),
								"Clicked on Checkbox: " + cb.getText() + " is "
										+ cb.isChecked(), Toast.LENGTH_LONG)
								.show();

						country.setSelected(cb.isChecked());
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

	private class MyCustomAdapter2 extends ArrayAdapter<Users> {

		private ArrayList<Users> userList;

		public MyCustomAdapter2(Context context, int textViewResourceId,
				ArrayList<Users> userList) {
			super(context, textViewResourceId, userList);
			this.userList = new ArrayList<Users>();
			this.userList.addAll(userList);
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
						Users users = (Users) cb.getTag();
						users.setSelected(cb.isChecked());
						username = users.getName();
					}
				});
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}

			Users users = userList.get(position);
			holder.code.setText(" (" + users.getCode() + ")");
			holder.name.setText(users.getName());
			holder.name.setChecked(users.isSelected());
			holder.name.setTag(users);

			return convertView;

		}

	}

	// private void checkButtonClick() {
	//
	// Button myButton = (Button) findViewById(R.id.findSelected);
	// myButton.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	//
	// StringBuffer responseText = new StringBuffer();
	// responseText.append("The following were selected...\n");
	//
	// ArrayList<DisplayFile> countryList = fileAdapter.fileList;
	// for (int i = 0; i < countryList.size(); i++) {
	// DisplayFile country = countryList.get(i);
	// if (country.isSelected()) {
	// responseText.append("\n" + country.getName());
	// }
	// }
	//
	// Toast.makeText(getApplicationContext(), responseText,
	// Toast.LENGTH_LONG).show();
	//
	// // DeviceClient.uploadFile("/Users/yjiang66/Desktop/launch.jpg",
	// // "sshao", "192.168.56.1");
	//
	// }
	// });
	//
	// }

	private class ExternalIP extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// your http network call here.
			String ipadd = getExterIP.getCurrentExternalIP();
			return ipadd;
		}

		@Override
		protected void onPostExecute(String result) {
			// update your ui here
			Toast.makeText(getApplicationContext(),
					getExterIP.getCurrentExternalIP(), Toast.LENGTH_LONG)
					.show();
		}

		@Override
		protected void onPreExecute() {
			// do any code before exec
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// If you want to update a progress bar ..do it here
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

		protected void onPostExecute(Boolean result) {
			// TODO: check this.exception
			if (result == true) {
				pd.dismiss();
				pd.cancel();
				Toast.makeText(getApplicationContext(),
						"Share file successfully.", Toast.LENGTH_LONG).show();
			}
		}
	}

	class MyClient extends AsyncTask<Void, Void, Boolean> {
		boolean result = false;

		protected Boolean doInBackground(Void... x) {
			try {
				Log.d("lalala", "Client is working...\n");
				DeviceClient.uploadFile(MainActivity.fileFullPath,
						Variable.username, username, Variable.vmIP);
				System.out.println("Client is uploading...");
				result = true;
			}
			catch (Exception e) {
				e.getMessage();
			}
			return result;
		}

		protected void onPostExecute(Boolean x) {
			// TODO: check this.exception
			if (result == true) {
				pd.dismiss();
				pd.cancel();
				Toast.makeText(getApplicationContext(),
						"Upload file successfully.", Toast.LENGTH_LONG).show();
			}
		}
	}

	class ShowClient extends AsyncTask<Void, Void, String[]> {

		protected String[] doInBackground(Void... x) {
			String[] users = null;
			try {
				users = DeviceClient.queryUsers(Variable.username);

			}
			catch (Exception e) {
				e.getMessage();
			}
			return users;
		}

		protected void onPostExecute(String[] users) {
			displayListView(users);
			// TODO: check this.exception
		}
	}
}
