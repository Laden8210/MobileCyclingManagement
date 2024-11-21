package com.example.mobilecyclingmanagement.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.adapter.ImageAdapter;
import com.example.mobilecyclingmanagement.adapter.ImageAdapterUpload;
import com.example.mobilecyclingmanagement.adapter.NewFeedAdapter;
import com.example.mobilecyclingmanagement.api.ApiAddress;
import com.example.mobilecyclingmanagement.api.ApiService;
import com.example.mobilecyclingmanagement.api.RetrofitClient;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.callback.ImageUploadCallback;
import com.example.mobilecyclingmanagement.model.ApiResponse;
import com.example.mobilecyclingmanagement.model.Post;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private RecyclerView recyclerView;

    private Button btnUploadImage;
    private RecyclerView recyclerViewImages;
    private ImageAdapterUpload imageAdapter;
    private ArrayList<Uri> imageUris;
    private Button btnPostNow;

    private TextInputEditText etPost;
    private FirebaseAuth mAuth;
    private FirestoreRepositoryImpl<Post> postRepository;
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            imageUris.add(imageUri);
                        }
                    } else if (data.getData() != null) {
                        Uri imageUri = data.getData();
                        imageUris.add(imageUri);
                    }
                    imageAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No images selected", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        etPost = view.findViewById(R.id.etPost);

        btnPostNow = view.findViewById(R.id.btnPostNow);
        btnPostNow.setOnClickListener(this::postNowAction);
        mapView = view.findViewById(R.id.mapView);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        mAuth = FirebaseAuth.getInstance();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initializeMap();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        recyclerViewImages = view.findViewById(R.id.recyclerViewImages);

        imageUris = new ArrayList<>();
        imageAdapter = new ImageAdapterUpload(getActivity(), imageUris);
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewImages.setAdapter(imageAdapter);

        btnUploadImage.setOnClickListener(v -> openImagePicker());
        displayRecentPost();
        return view;
    }

    private void postNowAction(View view) {
        String postSTR = etPost.getText().toString().trim();

        if (postSTR.isEmpty()) {
            Toast.makeText(getContext(), "Post content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Post post = new Post();
        post.setPost(postSTR);

        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        post.setTimestamp(new com.google.firebase.Timestamp(currentTimeInSeconds, 0));
        post.setUserUID(mAuth.getCurrentUser().getUid());

        List<String> imageUrls = new ArrayList<>();
        if (imageUris.isEmpty()) {

            createPost(post, imageUrls);
            return;
        }

        for (Uri uri : imageUris) {
            uploadImageToServer(uri, new ImageUploadCallback() {
                @Override
                public void onSuccess(String filePath) {
                    Log.d("UploadImage", "Uploaded image URL: " + filePath);
                    imageUrls.add(filePath);
                    if (imageUrls.size() == imageUris.size()) {
                        Log.d("UploadImage", "All images uploaded. Creating post.");
                        createPost(post, imageUrls);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getContext(), "Failed to upload image: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void createPost(Post post, List<String> imageUrls) {
        post.setImageUrl(imageUrls);
        postRepository.create(post, new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                Toast.makeText(getContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                etPost.setText("");
                imageUris.clear();
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {

                Toast.makeText(getContext(), "Failed to create post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }


    private void uploadImageToServer(Uri imageUri, ImageUploadCallback callback) {
        String serverUrl = ApiAddress.BASE_URL + "index.php"; // Replace with your server URL
        String boundary = "*****" + System.currentTimeMillis() + "*****";
        String LINE_FEED = "\r\n";

        String filePath = getRealPathFromUri(imageUri);
        if (filePath == null) {
            callback.onFailure("Failed to resolve file path from Uri.");
            return;
        }

        File imageFile = new File(filePath);
        if (!imageFile.exists()) {
            callback.onFailure("File not found at resolved path: " + filePath);
            return;
        }

        new Thread(() -> {
            try {

                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream outputStream = connection.getOutputStream();
                DataOutputStream writer = new DataOutputStream(outputStream);


                writer.writeBytes("--" + boundary + LINE_FEED);
                writer.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"" + imageFile.getName() + "\"" + LINE_FEED);
                writer.writeBytes("Content-Type: " + "image/jpeg" + LINE_FEED);
                writer.writeBytes(LINE_FEED);

                FileInputStream fileInputStream = new FileInputStream(imageFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    writer.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
                writer.writeBytes(LINE_FEED);


                writer.writeBytes("--" + boundary + "--" + LINE_FEED);
                writer.flush();
                writer.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    java.util.Scanner scanner = new java.util.Scanner(connection.getInputStream()).useDelimiter("\\A");
                    String response = scanner.hasNext() ? scanner.next() : "";
                    scanner.close();


                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.getString("status").equals("success")) {
                        String serverFilePath = responseJson.getString("file_path");
                        getActivity().runOnUiThread(() -> callback.onSuccess(serverFilePath));
                    } else {
                        String errorMessage = responseJson.getString("message");
                        getActivity().runOnUiThread(() -> callback.onFailure(errorMessage));
                    }
                } else {
                    String errorMessage = "Server responded with code " + responseCode;
                    getActivity().runOnUiThread(() -> callback.onFailure(errorMessage));
                }
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> callback.onFailure("Error: " + e.getMessage()));
            }
        }).start();
    }



    private void displayRecentPost() {
        postRepository = new FirestoreRepositoryImpl<>("posts", Post.class);
        postRepository.readAll(new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                ArrayList<Post> posts = (ArrayList<Post>) result;
                recyclerView.setAdapter(new NewFeedAdapter(getContext(), posts));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Images"));
    }

    private void initializeMap() {
        mapView.getMapboxMap().setCamera(
                new CameraOptions.Builder().zoom(14.0).build()
        );

        mapView.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS, style -> {
            getLastKnownLocation(location -> {
                if (location != null) {
                    updateMapWithLocation(location);
                } else {
                    Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void getLastKnownLocation(OnLocationReceivedListener listener) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            listener.onLocationReceived(null);
            return;
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                listener.onLocationReceived(task.getResult());
            } else {
                listener.onLocationReceived(null);
            }
        });
    }

    private void updateMapWithLocation(Location location) {
        com.mapbox.geojson.Point currentLocation = com.mapbox.geojson.Point.fromLngLat(location.getLongitude(), location.getLatitude());

        mapView.getMapboxMap().setCamera(new CameraOptions.Builder()
                .center(currentLocation)
                .zoom(15.0)
                .build()
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap();
            } else {
                Toast.makeText(getContext(), "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    interface OnLocationReceivedListener {
        void onLocationReceived(Location location);
    }
}
