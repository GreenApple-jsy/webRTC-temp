package com.example.videotest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.webrtc.MediaStreamTrack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class uploadVideo extends AppCompatActivity {

    Uri video_URI;
    int VIDEOFILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
    }

    // 동영상 앨범 열기
    public void getVideoFromGallery(View v) {
        //갤러리에 저장된 동영상 호출
        video_URI = Uri.parse("content://media/external/images/media");
        Intent intent = new Intent(Intent.ACTION_VIEW, video_URI);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent,VIDEOFILE_REQUEST);
        System.out.println(video_URI);
    }

    // 서버에 전송
    public void sendVideoContentToServer(View v) {
        System.out.println("들어옴0");
        class sendDataToHttp extends AsyncTask<Void, Void, String> {
            String serverUrl = "http://1.234.38.211/uploadVideo.php";
            OkHttpClient client = new OkHttpClient();
            Context context;

            public sendDataToHttp(Context context) {
                this.context = context;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                System.out.println("들어옴1");
                ContentResolver contentResolver = context.getContentResolver();
                final String contentType = contentResolver.getType(video_URI);
                final AssetFileDescriptor fd;
                try {
                    fd = contentResolver.openAssetFileDescriptor(video_URI, "r");

                    if (fd == null) {
                        throw new FileNotFoundException("could not open file descriptor");
                    }


                    RequestBody videoFile = new RequestBody() {
                        @Override
                        public long contentLength() {
                            return fd.getDeclaredLength();
                        }

                        @Override
                        public MediaType contentType() {
                            return MediaType.parse(contentType);
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            try (InputStream is = fd.createInputStream()) {
                                sink.writeAll(Okio.buffer(Okio.source(is)));
                            }
                        }
                    };

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("questionId","1") //문제 id 들어가면 됨
                            .addFormDataPart("file", "fname",videoFile)
                            .build();

                    Request request = new Request.Builder()
                            .url(serverUrl)
                            .post(requestBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            try {
                                fd.close();
                            } catch (IOException ex) {
                                e.addSuppressed(ex);
                            }
                            System.out.println("실패" + e);
                            //Log.d("실패", "failed", e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            System.out.println("결과" + result);
                            //Log.d("결과",""+result);
                            fd.close();
                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println("끝");
            }

        }

        sendDataToHttp sendData = new sendDataToHttp(this);
        sendData.execute();
    }



}
