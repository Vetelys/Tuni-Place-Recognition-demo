package com.example.tuniclassify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SearchEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


//Client - server code from here: https://heartbeat.comet.ml/uploading-images-from-android-to-a-python-based-flask-server-691e4092a95e
public class ServerActivity extends AppCompatActivity {
    //TODO: establish connection to server
    Button serverButton;
    private static final String SERVER_URL = "http://192.168.1.2:5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        serverButton = findViewById(R.id.serverButton);

        serverButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                connectServer(view);
            }
        });
    }
    void connectServer(View v){
        String postBodyText = "Hello server!";
        MediaType mediatype = MediaType.parse("text/plain; charset=utf-8");
        RequestBody postBody = RequestBody.create(mediatype, postBodyText);
        postRequest(SERVER_URL, postBody);
    }

    void postRequest(String Url, RequestBody postBody){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Url)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e){
                call.cancel();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ServerActivity.this, "Could not connect to server!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ServerActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}