package com.example.nicolas.mylauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //the recycler view that will display the app as a list
    RecyclerView recyclerView;

    /**
     * called when your android activity starts
     * put your code inside
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set activity_main.xml as the main view for this activity
        setContentView(R.layout.activity_main);

        //initialize recyclerview with the one in the xml
        recyclerView = (RecyclerView) findViewById(R.id.list);

        //set the recycler view to act like a horizontal linear layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set the recycler view adapter
        setupAdapter();
    }

    /**
     * query all the applications on the phone
     * set the adapter for the recycler view
     */
    public void setupAdapter(){
        /**
         * get the list of all the apps installed on the phone
         */
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> applications = pm.queryIntentActivities(startupIntent, 0);

        //set the adapter for the recycler view and pass it all the applications
        recyclerView.setAdapter(new RecyclerViewAdapter(applications));
    }

    /**
     * template for the row item, used by the recycler view
     */
    private class ApplicationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //the textview that will contain the app name
        private TextView appName;
        private ImageView iconImg;
        //information related to the app
        private ResolveInfo mResolveInfo;

        public ApplicationHolder(View itemView) {
            super(itemView);
            //bind the TextView to the xml
            appName = (TextView) itemView.findViewById(R.id.appName);
            //bind the xml to the imageView
            iconImg = (ImageView) itemView.findViewById(R.id.icon);
            //set the click listener for the text view
            appName.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            //save the information of this app
            mResolveInfo = resolveInfo;
            //get the name of the app as text
            PackageManager pm = MainActivity.this.getPackageManager();
            String appNameTxt = mResolveInfo.loadLabel(pm).toString();

            try {
                //get the icon from the activityinfo
                Drawable icon = getPackageManager().getApplicationIcon(mResolveInfo.activityInfo.applicationInfo.packageName);
                //set the icon
                iconImg.setImageDrawable(icon);
            } catch (Exception e){e.printStackTrace();}

            appName.setText(appNameTxt);
        }

        @Override
        public void onClick(View v) {
            //get the information of the app you clicked on
            ActivityInfo activityInfo = mResolveInfo.activityInfo;

            Intent i = new Intent(Intent.ACTION_MAIN)
                    .setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //open the app
            startActivity(i);

        }
    }

    /**
     * recyclerview adapter, used by recyclerview to bind between the data and the ui
     */
    private class RecyclerViewAdapter extends RecyclerView.Adapter{

        List<ResolveInfo> applications;

        public RecyclerViewAdapter(List<ResolveInfo> applications){
            //save the applications
            this.applications=applications;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //bind and create the row item
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.row_item, parent, false);
            return new ApplicationHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //return the application at that position
            ApplicationHolder view = (ApplicationHolder) holder;
            view.bindActivity(applications.get(position));
        }

        @Override
        public int getItemCount() {
            //return the number of applications you have
            return applications.size();
        }
    }
}
