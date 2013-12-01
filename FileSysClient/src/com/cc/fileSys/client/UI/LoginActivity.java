package com.cc.fileSys.client.UI;

import location.MyLocationListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.cc.fileSys.client.R;
import com.cc.fileSys.client.function.DeviceClient;
import com.cc.fileSys.client.parameter.Variable;

public class LoginActivity extends Activity {

	private Button loginButton;
	private Button gpsButton;
	private EditText usernameEdit;
	private EditText pwEdit;
	String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginButton = (Button) findViewById(R.id.loginButton);
		gpsButton = (Button) findViewById(R.id.gpsButton);
		usernameEdit = (EditText) findViewById(R.id.username);
		pwEdit = (EditText) findViewById(R.id.password);
		
		Toast.makeText(getApplicationContext(),
				"IP " + getExterIP.getAndroidLocalIP() , Toast.LENGTH_LONG)
				.show();

		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Variable.Latitude != 0 && Variable.Longitude != 0) {
					new MyClient().execute();
				}
				else {
					Toast.makeText(
							getApplicationContext(),
							"No GPS info found, please press get location button. It may take 1~5 minutes to get location.",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		gpsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				LocationManager mlocManager = null;
				LocationListener mlocListener;
				mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				mlocListener = new MyLocationListener();
				mlocManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

				if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					if (MyLocationListener.latitude > 0) {
						Toast.makeText(
								getApplicationContext(),
								"Latitude:- " + MyLocationListener.latitude
										+ '\n' + "Longitude:- "
										+ MyLocationListener.longitude + '\n',
								Toast.LENGTH_LONG).show();
						Variable.Latitude = MyLocationListener.latitude;
						Variable.Longitude = MyLocationListener.longitude;
					}
					else {
						Toast.makeText(getApplicationContext(),
								"GPS in progress, please wait.",
								Toast.LENGTH_LONG).show();
					}
				}
				else {
					Toast.makeText(getApplicationContext(),
							"GPS is not turned on...", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	class MyClient extends AsyncTask<Void, Void, Boolean> {

		protected Boolean doInBackground(Void... x) {
			boolean login_result = false;
			try {
				username = usernameEdit.getText().toString();
				String pw = pwEdit.getText().toString();
				Log.d("lalala", "Client is working...\n");
				// testing
				login_result = DeviceClient.login(username, pw);
				System.out.println("User is logining...");
			}
			catch (Exception e) {
				e.getMessage();
			}
			return login_result;
		}

		protected void onPostExecute(Boolean login_result) {
			if (login_result == true) {
				Variable.username = username;
				Intent myIntent = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivityForResult(myIntent, 0);
			}
			else {
				Toast.makeText(getApplicationContext(),
						"Username or password is wrong.", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

}
