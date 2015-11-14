package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

// Testing GitHub integration

public class MainActivity extends Activity {
    ProgressBar progressBar;
    TextView textViewPracticeTime;
    TextView textViewPlanTime;
    Progress myProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewPlanTime = (TextView) findViewById(R.id.textViewPlanTime);
        textViewPracticeTime = (TextView) findViewById(R.id.textViewPracticeTime);


        Database.init(getApplicationContext());
        // Database.createDemoDatabase();
        myProgress = new Progress(this);
        myProgress.update();
    }

    public void piecesButtonHandler(View v) {
        Intent intent = new Intent(MainActivity.this, PiecesActivity.class);
        startActivityForResult(intent, 0);
    }

    public void planButtonHandler(View v) {
        Intent intent = new Intent(MainActivity.this, PlanListActivity.class);
        startActivityForResult(intent, 0);
    }

    public void planTabletButtonHandler(View v) {
        Intent intent = new Intent(MainActivity.this, PlanTabletActivity.class);
        startActivityForResult(intent, 0);
    }

    public void practiceButtonHandler(View v) {
        Intent intent = new Intent(MainActivity.this, PracticeListActivity.class);
        startActivityForResult(intent, 0);
    }

    public void practiceTabletButtonHandler(View v) {
        Intent intent = new Intent(MainActivity.this, PracticeTabletActivity.class);
        startActivityForResult(intent, 0);
    }
    public void importButtonHandler(View v) {
       String importFrom = Database.importDatabaseFromFile();
        Toast.makeText(getApplicationContext(),"Imported from " + importFrom, Toast.LENGTH_SHORT).show();
    }

    public void exportButtonHandler(View v) {
        String exportTo = Database.exportDatabaseToFile();
        Toast.makeText(getApplicationContext(),"Exported to " + exportTo, Toast.LENGTH_SHORT).show();

    }
    public void onResume() {
        super.onResume();
        myProgress.update();
    }

    public void settingsButtonHandler(View v) {
    }

    public  void downloadDatabaseFromInternet( String stringUrl) {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            // display error
        }

    }
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // textView.setText(result);
        }
    }
    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("DOWNLOADED", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
