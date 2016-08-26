package com.example.init.imageuploaddemo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// Project created by Ferdousur Rahman Shajib
// www.androstock.com

public class MainActivity extends AppCompatActivity {
    ImageView imageview;
    String imagepath;
    File sourceFile;
    int totalSize = 0;
    String FILE_UPLOAD_URL = "http://192.168.0.186/upload.php";
    LinearLayout uploader_area;
    LinearLayout progress_area;
    public DonutProgress donut_progress;
    private List<DataBean> dataBeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
/*
        uploader_area = (LinearLayout) findViewById(R.id.uploader_area);
        progress_area = (LinearLayout) findViewById(R.id.progress_area);
        Button select_button = (Button) findViewById(R.id.button_selectpic);
        Button upload_button = (Button) findViewById(R.id.button_upload);
        donut_progress = (DonutProgress) findViewById(R.id.donut_progress);
        imageview = (ImageView) findViewById(R.id.imageview);




        upload_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (imagepath != null) {
                    new UploadFileToServer().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select a file to upload.", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        Button select_button = (Button) findViewById(R.id.button_selectpic);
        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent,1);
            }
        });
        initializeData();
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
         adapter = new RVAdapter(dataBeen,this);

        rv.setAdapter(adapter);
        // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.

    }
    RVAdapter adapter;
    private void initializeData(){
        dataBeen = new ArrayList<>();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    // Get the url from data
                    Uri selectedImageuri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImageuri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    Log.e("path",imgDecodableString);
                    imagepath=imgDecodableString;
                    DataBean db=new DataBean();
                    db.setImageurl(imagepath);
                    db.setViewtype(1);
                    dataBeen.add(db);
                    adapter.notifyItemChanged(dataBeen.size()-1);

                }
                }
            }

        }


    public String getPathFromURI(Uri contentUri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(contentUri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private class UploadFileToServer extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            // setting progress bar to zero
            donut_progress.setProgress(0);
            uploader_area.setVisibility(View.GONE); // Making the uploader area screen invisible
            progress_area.setVisibility(View.VISIBLE); // Showing the stylish material progressbar
            sourceFile = new File(imagepath);
            totalSize = (int) sourceFile.length();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... progress)
        {
            Log.d("PROG", progress[0]);
            donut_progress.setProgress(Integer.parseInt(progress[0])); //Updating progress
        }

        @Override
        protected String doInBackground(String... args)
        {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = null;
            String fileName = sourceFile.getName();

            try
            {

                connection = (HttpURLConnection) new URL(FILE_UPLOAD_URL).openConnection();

                connection.setRequestMethod("POST");
                String boundary = "---------------------------boundary";
                String tail = "\r\n--" + boundary + "--\r\n";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setDoOutput(true);

                String metadataPart = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                        + "" + "\r\n";

                String fileHeader1 = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + fileName + "\"\r\n"
                        + "Content-Type: application/octet-stream\r\n"
                        + "Content-Transfer-Encoding: binary\r\n";

                long fileLength = sourceFile.length() + tail.length();
                String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
                String stringData = metadataPart + fileHeader;

                long requestLength = stringData.length() + fileLength;
                connection.setRequestProperty("Content-length", "" + requestLength);
                connection.setFixedLengthStreamingMode((int) requestLength);
                connection.connect();

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(stringData);
                out.flush();

                int progress = 0;
                int bytesRead = 0;
                byte buf[] = new byte[1024];
                BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(sourceFile));
                while ((bytesRead = bufInput.read(buf)) != -1) {
                    // write output
                    out.write(buf, 0, bytesRead);
                    out.flush();
                    progress += bytesRead; // Here progress is total uploaded bytes

                    publishProgress("" + (int) ((progress * 100) / totalSize)); // sending progress percent to publishProgress
                }

                // Write closing boundary and close stream
                out.writeBytes(tail);
                out.flush();
                out.close();

                // Get server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
return  builder.toString();
            } catch (Exception e) {
                Log.e("hi", "Exception: "+Log.getStackTraceString(e));
            }
             finally {
                if (connection != null) connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Response", "Response from server: " + result);
            super.onPostExecute(result);
        }

    }


}