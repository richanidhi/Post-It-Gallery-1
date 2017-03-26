package com.akshaybaweja.nasheligallery.activity;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.akshaybaweja.nasheligallery.R;
import com.akshaybaweja.nasheligallery.adapter.GalleryAdapter;
import com.akshaybaweja.nasheligallery.model.Image;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private static final String endpoint = "http://api.androidhive.info/json/glide.json";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    protected boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions()
    {
        String permissions[] = {"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.INTERNET"};
        requestPermissions(permissions,200);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(shouldAskPermission())
        {
            askPermissions();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(view.findViewById(R.id.thumbnail).getAlpha()==0.3f) {
                    TextView textView = (TextView) view.findViewById(R.id.overlayText);
                    textView.setVisibility(TextView.INVISIBLE);
                    view.findViewById(R.id.thumbnail).setAlpha(1.0f);
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", images);
                    bundle.putInt("position", position);

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if(view.findViewById(R.id.thumbnail).getAlpha()!=0.3f){
                    TextView textView = (TextView) view.findViewById(R.id.overlayText);
                    textView.setVisibility(TextView.VISIBLE);

                    try {
                        Image meriImage = images.get(position);
                        ExifInterface exif = new ExifInterface(meriImage.getImage());
                        textView.setText(exif.getAttribute(ExifInterface.));

                    } catch (Exception e) {

                    }
                    view.findViewById(R.id.thumbnail).setAlpha(0.3f);
                }
            }
        }));

        fetchImages();
    }

    private void fetchImages() {

        pDialog.setMessage("Fetching Image Database...");
        pDialog.show();

        images.clear();
        File path = new File("storage/emulated/0/DCIM/Camera/");
        if(path.isDirectory()) {
            File[] n = path.listFiles();
            for (int i = 0; i < n.length; i++) {
                File j = n[i].getAbsoluteFile();
                String jpath = j.getAbsolutePath();
                Image image = new Image();
                image.setName(j.getName());
                image.setImage(j.getAbsolutePath());
                try {
                    ExifInterface exif = new ExifInterface(jpath);
                    image.setTimestamp(exif.getAttribute(ExifInterface.TAG_DATETIME));
                } catch (Exception e) {

                }
                images.add(image);
            }
        }
        mAdapter.notifyDataSetChanged();
        pDialog.hide();
    }
}
