package com.example.jakoubek.converter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    private String filePathForToast = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        try {
            Bundle extras = getIntent().getExtras();
            String videoUrl = extras.getString(Intent.EXTRA_TEXT);
            EditText urlBox = (EditText) findViewById(R.id.urlBox);
            urlBox.setText(videoUrl);
        } catch(NullPointerException e) {
            System.out.println("Link nebyl zadán.");
        }
        //Toast.makeText(MainActivity.this, value1, Toast.LENGTH_LONG).show();
//        System.exit(1);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void linkButtonClicked(View Button) {

//
//        System.out.println(intent.getDataString());

//System.exit(1);
        EditText insertedText = (EditText) findViewById(R.id.urlBox);

        final String videoUrl = insertedText.getText().toString();

        if (this.isValidUrl(videoUrl) && this.isYoutubeLink(videoUrl)) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        //String videoUrl = "https://www.youtube.com/watch?v=dDj7DuHVV9E";
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        final TextView textProgress = (TextView) findViewById(R.id.textProgress);


                        Socket clientSocket;
                        clientSocket = new Socket("app.heydukovo.biz", 4467);

                        System.out.println("client started....");

                        PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream());
                        clientWriter.write(videoUrl + "\n");
                        clientWriter.flush();

                        InputStream is = clientSocket.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);

                        while (true) {
                            File file = null;
                            int status = br.read();
                            //System.out.println("asdf");

                            if (status == 1) {
                                System.out.println("Video downloading started.");
                                progressBar.setProgress(2);
                            } else if (status == 2) {
                                System.out.println("Video downloaded.");
                                progressBar.setProgress(8);
                            } else if (status == 3) {
                                System.out.println("Video converting started.");
                                progressBar.setProgress(10);
                            } else if (status == 4) {
                                System.out.println("Video converted .");
                                progressBar.setProgress(20);
                            } else if (status == 5) {
                                System.out.println("Uploading started.");

                                String fileName = br.readLine();

                                int fileSize = Integer.parseInt(br.readLine());

                                System.out.println("Filesize is " + fileSize + " bytes and name is " + fileName);

                                int bytesRead;
                                int currentTot = 0;
                                byte[] bytearray = new byte[fileSize];

                                File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                                    //filePath = Environment.getExternalStorageDirectory();
//                                    filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                                } else {
//                                    filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//
//                                }
//                                System.out.println(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

                                File fileDir = new File(filePath.getAbsolutePath() + "/mp3");

                                if (!fileDir.exists()) {
                                    System.out.println(fileDir.mkdir());
                                }

                                file = new File(fileDir.getAbsolutePath(), fileName);

                                MainActivity.this.filePathForToast = file.getAbsolutePath();

                                FileOutputStream fos = new FileOutputStream(file);
                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                bytesRead = is.read(bytearray, 0, bytearray.length);
                                currentTot = bytesRead;
                                double progress = 0;

                                do {
                                    bytesRead = is.read(bytearray, currentTot, (bytearray.length - currentTot));
                                    //	if (bytesRead >= 0) {

                                    if (bytesRead >= 0) {
                                        currentTot += bytesRead;
                                        progress = (((double) currentTot / (double) fileSize) * 80) + 20;
                                        //System.out.println(progress);
                                        progressBar.setProgress((int) progress);
                                        System.out.println((int) progress);
                                    }
                                } while (bytesRead > 0);

//                                if(progressBar.getProgress() >= 100) {
//                                    progressBar.he
//                                }

                                bos.write(bytearray, 0, currentTot);
                                bos.flush();
                                bos.close();
//                                System.out.println(file.getAbsolutePath());
//                                System.exit(1);

                                //socket.close();
                            } else if (status == 6) {

//                                final MainActivity activity = new MainActivity();
//                                activity.runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
//                                    }
//                                });


                                runOnUiThread(new Runnable() {
                                    String filePath;

                                    @Override
                                    public void run() {
//                                        Looper.prepare();
                                        Toast.makeText(MainActivity.this, "File downloaded", Toast.LENGTH_LONG).show();

                                        String text = "Downloaded to " + filePath;

                                        textProgress.setText(text);
                                        textProgress.setTextSize(15);

                                        Intent intent = new Intent();
                                        intent.setAction(android.content.Intent.ACTION_VIEW);
                                        File file = new File(MainActivity.this.filePathForToast);
                                        intent.setDataAndType(Uri.fromFile(file), "audio/*");

                                        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);


                                        NotificationCompat.Builder mBuilder =
                                                new NotificationCompat.Builder(MainActivity.this)
                                                        .setSmallIcon(android.R.drawable.ic_media_play)
                                                        .setContentTitle("Play file")
                                                        .setContentText("Downloaded to " + MainActivity.this.filePathForToast)
                                                        .setContentIntent(pIntent);

                                        int mNotificationId = 175626;
                                        NotificationManager mNotifyMgr =
                                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        mNotifyMgr.notify(mNotificationId, mBuilder.build());


//                                        Looper.loop();
                                    }

                                    public Runnable init(String filePath) {
                                        this.filePath = filePath;
                                        return (this);
                                    }
                                }.init(MainActivity.this.filePathForToast));


                                progressBar.setVisibility(View.INVISIBLE);
                                System.out.println("File transfered.");

                                clientWriter.close();

                                break;
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
            //String videoUrl = "https://www.youtube.com/watch?v=xaRNvJLKP1E";

            //System.out.println(videoUrl);

        } else {
            Toast.makeText(this, "Please enter valid Youtube link.", Toast.LENGTH_SHORT).show();
        }


    }

    public void something() {
        Toast.makeText(this, "File downloaded!", Toast.LENGTH_SHORT).show();
    }

    private boolean isYoutubeLink(String url) {
        return url.contains("youtube.com") || url.contains("youtu.be");
    }

    private boolean isValidUrl(String text) {
        try {
            URL url = new URL(text);
            return true;
        } catch (MalformedURLException e) {
            return false;

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jakoubek.converter/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jakoubek.converter/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
