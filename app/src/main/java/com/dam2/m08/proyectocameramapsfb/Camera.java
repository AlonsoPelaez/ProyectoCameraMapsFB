package com.dam2.m08.proyectocameramapsfb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Camera extends AppCompatActivity {

    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final StorageReference storageReferenceGlobal = storage.getReference().child("Usuarios");
    private static final String TAG = "AndroidCameraApi";
    private ImageView btnTomaFoto;
    private ImageView btn_cambiaCamara;
    private ImageView imageViewGaleria;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    private String cameraFrontId;
    private String cameraBackId;
    private boolean cameraIsBack;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    private HandlerThread mbackgroundThread;
    private Bitmap resizedBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");

        textureView = findViewById(R.id.textureView);
        if (textureView != null) {
            textureView.setSurfaceTextureListener(texturelistener);
        }
        btnTomaFoto = findViewById(R.id.btn_takePicture);
        btn_cambiaCamara = findViewById(R.id.btn_switchCamera);
        imageViewGaleria = findViewById(R.id.ivfoto);

        defineCamaras();

        if (btnTomaFoto != null) {
            btnTomaFoto.setOnClickListener(v -> tomaFoto());
        }
        if (btn_cambiaCamara != null) {
            btn_cambiaCamara.setOnClickListener(v -> cambiaCameraId());
        }
    }

    private void defineCamaras() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String idCamera : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(idCamera);
                int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    cameraFrontId = idCamera;
                } else if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraBackId = idCamera;
                    cameraId = idCamera;
                    cameraIsBack = true;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    TextureView.SurfaceTextureListener texturelistener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    protected void startBackgroundThreads() {
        mbackgroundThread = new HandlerThread("Camera Background");
        mbackgroundThread.start();
        mBackgroundHandler = new Handler(mbackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mbackgroundThread.quitSafely();
        try {
            mbackgroundThread.join();
            mbackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void tomaFoto() {

        if (cameraDevice == null) {
            return;
        }
        if (!isExtenalStorageAvailableForRW() || isExternalStorageReadOnly()) {
            btnTomaFoto.setEnabled(false);
        }
        if (isStoragePermissionGranted()) {

            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

            try {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
                Size[] jpegSizes = null;
                if (characteristics != null) {
                    jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                }
                int width = 640;
                int height = 480;
                if (jpegSizes != null && jpegSizes.length > 0) {
                    width = jpegSizes[0].getWidth();
                    height = jpegSizes[0].getHeight();
                }
                final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
                List<Surface> outPutSurface = new ArrayList<>(2);
                outPutSurface.add(reader.getSurface());
                outPutSurface.add(new Surface(textureView.getSurfaceTexture()));

                final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(reader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                //orientation

                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, rotation);


                ImageReader.OnImageAvailableListener readerListener = reader1 -> {
                    try (Image image = reader1.acquireLatestImage()) {

                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);

                        SharedPreferences prefer = getSharedPreferences(getString(R.string.prefer_file), Context.MODE_PRIVATE);
                        String email = prefer.getString("email", null);

                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String filename = "IMG_" + timeStamp;

                        StorageReference storageReference = storageReferenceGlobal.child(email).child("misImagenes").child(filename);
                        UploadTask uploadTask = storageReference.putBytes(bytes);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    HashMap<String, Object> map = new HashMap();
                                    map.put("uri_foto", uri);
                                    db.collection("Usuarios").document(email).
                                            collection("misImagenes").document(filename).set(map);

                                    Log.d(TAG, "onSuccess: "+ map);
                                });

                            }


                        }).addOnFailureListener(e -> {

                        });

                            if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                                locationPermissionGranted = true;
                            } else {
                                ActivityCompat.requestPermissions(this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            }
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    HashMap<String, Object> map = new HashMap();
                                    map.put("longitud", location.getLongitude());
                                    map.put("latitud", location.getLatitude());
                                    db.collection("Usuarios").document(email).
                                            collection("misImagenes").document(filename).set(map);
                                    Log.d(TAG, "onSuccess: "+ map);
                                }
                            });


                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        int width1 = 90;
                        int height1 = 60;
                        resizedBitmap = Bitmap.createScaledBitmap(bitmap, width1, height1, false);

                        updateImageView(rotaImage(resizedBitmap));

                    }
                };



                reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);



                final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        createCameraPreview();

                    }
                };

                cameraDevice.createCaptureSession(outPutSurface, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);

                        }catch (CameraAccessException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                    }
                }, mBackgroundHandler);


            }catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
    public Bitmap rotaImage(Bitmap bitmap){

        Matrix matrix = new Matrix();
        if (cameraIsBack){
            matrix.postRotate(90);
        }else {
            matrix.postRotate(270);
        }

        Bitmap bitmapRota= Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
        return bitmapRota;
    }

    public void updateImageView(Bitmap bitmap){
        runOnUiThread(() -> imageViewGaleria.setImageBitmap(bitmap));
    }

    private static boolean isExternalStorageReadOnly(){
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)){
            return true;
        }
        return false;
    }
    private boolean isExtenalStorageAvailableForRW(){
        String extStorageState=Environment.getExternalStorageState();
        if (extStorageState.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }
    private boolean isStoragePermissionGranted(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                return true;
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                return false;
            }
        }else {
            return true;
        }
    }

    protected void createCameraPreview(){

        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert  texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {

                    if (cameraDevice == null){
                        return;
                    }
                    cameraCaptureSession = session;
                    updatePreview();

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(getApplicationContext(), "configuracion cambiada",Toast.LENGTH_SHORT).show();
                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera(){

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

            StreamConfigurationMap map= characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert  map!=null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(Camera.this,new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId,stateCallback,null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    public void closeCamera(){
        if (cameraCaptureSession != null){
            cameraCaptureSession.close();
            cameraCaptureSession =null;
        }
        if (cameraDevice != null){
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void cambiaCameraId() {

        if (cameraIsBack){
            cameraId = cameraFrontId;
            cameraIsBack=false;
        }else {
            cameraId = cameraBackId;
            cameraIsBack = true;
        }
        closeCamera();
        openCamera();
    }

    protected void updatePreview(){
        if (null == cameraDevice){
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION){
            if (grantResults[0]==PackageManager.PERMISSION_DENIED){
                Toast.makeText(getApplicationContext(),"lo siento no puede utilizar la app sin permisos",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {

        startBackgroundThreads();
        if (textureView.isAvailable()){
            openCamera();
        }else{
            textureView.setSurfaceTextureListener(texturelistener);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

}