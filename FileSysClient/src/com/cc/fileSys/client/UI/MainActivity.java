package com.cc.fileSys.client.UI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cc.fileSys.client.R;
import com.cc.fileSys.client.function.DeviceClient;
import com.cc.fileSys.client.parameter.Variable;

public class MainActivity extends Activity {

	private Button downloadButton;
	private Button shareButton;
	private Button addButton;

	private ListView ownedFileListView;

	private TextView welcome_str;
	
	public static String fileFullPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		downloadButton = (Button) findViewById(R.id.downloadButton);
		shareButton = (Button) findViewById(R.id.shareButton);
		addButton = (Button) findViewById(R.id.addButton);
		ownedFileListView = (ListView) findViewById(R.id.ownedFileList);
		welcome_str = (TextView) findViewById(R.id.welcome);

		welcome_str.setText("Hello " + Variable.username
				+ ", welcome to our File System Client");

		downloadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),
						DownloadActivity.class);
				startActivityForResult(myIntent, 0);
			}
		});

		shareButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),
						UploadActivity.class);
				startActivityForResult(myIntent, 0);
			}
		});

		addButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent myIntent = new Intent(getApplicationContext(),
				// FileActivity.class);
				// startActivityForResult(myIntent, 0);
				File mPath = new File(Environment.getExternalStorageDirectory()
						+ "/worm");
				FileDialog fileDialog = new FileDialog(MainActivity.this, mPath);
				// fileDialog.setFileEndsWith(".txt");
				fileDialog
						.addFileListener(new FileDialog.FileSelectedListener() {
							public void fileSelected(File file) {
								Log.d(getClass().getName(), "selected file "
										+ file.toString());
								final ArrayList<String> list = new ArrayList<String>();
								fileFullPath = file.toString();
								list.add(file.toString());
								final StableArrayAdapter adapter = new StableArrayAdapter(
										MainActivity.this,
										android.R.layout.simple_list_item_1,
										list);
								ownedFileListView.setAdapter(adapter);
							}
						});
				// fileDialog.addDirectoryListener(new
				// FileDialog.DirectorySelectedListener() {
				// public void directorySelected(File directory) {
				// Log.d(getClass().getName(), "selected dir " +
				// directory.toString());
				// }
				// });
				// fileDialog.setSelectDirectoryOption(false);
				fileDialog.showDialog();
			}
		});

		new MyClient().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}
	}

	class MyClient extends AsyncTask<Void, Void, Boolean> {

		protected Boolean doInBackground(Void... x) {
			boolean login_result = false;
			try {
				String ip = getExterIP.getAndroidLocalIP();
				DeviceClient.updateInfo(Variable.username,
						String.valueOf(Variable.Latitude),
						String.valueOf(Variable.Longitude), ip);
			}
			catch (Exception e) {
				e.getMessage();
			}
			return login_result;
		}

		protected void onPostExecute(Boolean login_result) {
			// TODO: check this.exception
			Toast.makeText(getApplicationContext(),
					"Update userinfo successfully.", Toast.LENGTH_LONG).show();
		}
	}
}
