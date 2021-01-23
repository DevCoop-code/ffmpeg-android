package com.hankyo.jeong.avideoeditor;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private View mainLayout;
//    private ImageView videoThumbNail;

    private static final String LOG = "AVideoEditor";

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private ContentResolver contentResolver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.main_layout);

        // Initialize the properties
        initProperties();

        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // Permission을 이미 가지고 있는지를 체크
        if (readExternalStoragePermission == PackageManager.PERMISSION_GRANTED &&
                writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
            // Already has Permission
            // START
            scanningStart();

        } else {
            // 사용자가 Permission 거부를 한 전적이 있는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                // 요청을 진행하기 전에 사용자에게 퍼미션이 필요한 이유를 설명 해 줌
                Snackbar.make(mainLayout, "이 앱을 실행하려면 외부 저장소에 대한 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        // 사용자에게 퍼미션 요청을 시도 요청 결과는 onRequestPermissionResult에서 수신됨
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                // 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 시도함, 요청 결과는 onRequestPermissionResultt에서 수신됨
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        // Get Video Data from ContentProvider
        try {
            contentResolver = getContentResolver();

            Cursor cursor = getVideo();
            while (cursor.moveToNext()) {
                // 각 칼럼의 열 인덱스 취득
                int idColNum = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID);
                int titleColNum = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.TITLE);
                int dateTakenColNum = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_TAKEN);
                int durationColNum = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION);
                int sizeColNum = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE);

                // 인덱스를 바탕으로 데이터를 Cursor로부터 취득
                long id = cursor.getLong(idColNum);
                String title = cursor.getString(titleColNum);
                long dateTaken = cursor.getLong(dateTakenColNum);
                int duration = cursor.getInt(durationColNum);
                int size = cursor.getInt(sizeColNum);
                Uri videoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                Log.d(LOG, "title : " + title + ", image URI : " + videoUri);

                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
//                videoThumbNail.setImageBitmap(bitmap);
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 여부를 체크
            for (int result: grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 모든 퍼미션을 허용
                // START
                scanningStart();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명 후 앱을 종료
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Snackbar.make(mainLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Snackbar.LENGTH_INDEFINITE).setAction("확인.", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                } else {
                    Snackbar.make(mainLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인.", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    /*
     * [Function]
     */
    private Cursor getVideo() {
        Uri queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        // 가져올 Column
        String[] projection = {
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.DATE_TAKEN,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.SIZE
        };

        // Sorting
        String sortOrder = MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC";

        // Get Data
//        queryUri = queryUri.buildUpon().appendQueryParameter("limit", "1").build();

        return contentResolver.query(queryUri, projection, null, null, sortOrder);
    }

    private void initProperties() {
//        videoThumbNail = findViewById(R.id.videoThumbNail);
    }

    /*
     * [FFMpeg Functions]
     */
    public void scanningStart() {
        String filepath;
        try {
            filepath = new File(Environment.getExternalStorageDirectory(), "/sample-video.mp4").getCanonicalPath();
            new NDK().scanning(filepath);
        } catch (IOException e){
            Log.e("FFmpegForAndroid", "", e);
        }
    }
}
