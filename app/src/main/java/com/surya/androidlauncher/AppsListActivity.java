// Is file me apps jab open honge uski list show uski, uski coding hui hai

package com.surya.androidlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppsListActivity extends Activity {

    RecyclerView recyclerView;
    private AppListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        initRecyclerView();
        loadApps();
        addClickListener();

        // Used for Drag and Drop feature in the app
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private PackageManager manager;
    private List<AppDetail> apps;


//    This method uses the package manager to query the device for installed apps,
//    retrieves information about each app, and updates the RecyclerView's data with the list of apps
    private void loadApps() {
        manager = getPackageManager();
        List<AppDetail> newApps = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            AppDetail app = new AppDetail();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(manager);
            newApps.add(app);
        }

        // Update the 'apps' field with the new list
        apps = newApps;

        // Update the data in the adapter
        adapter.updateData(apps);
    }



    // This method initializes the RecyclerView by setting its layout manager and adapter
    private void initRecyclerView() {
        recyclerView = findViewById(R.id.apps_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppListAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }


//    This method sets a click listener for the RecyclerView items,
//    allowing the user to open an app when clicked.
    private void addClickListener() {
        adapter.setOnItemClickListener(new AppListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(position).name.toString());
                AppsListActivity.this.startActivity(i);
            }
        });
    }

    // This is an inner class responsible for managing the RecyclerView adapter.
    private static class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

        private List<AppDetail> apps;
        private OnItemClickListener itemClickListener;

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        public AppListAdapter(List<AppDetail> apps) {
            this.apps = apps;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
        }

        public void updateData(List<AppDetail> apps) {
            this.apps = apps;
            notifyDataSetChanged();
        }



        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT; // Set width as needed
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT; // Set height as needed
            holder.itemView.setLayoutParams(layoutParams);
            holder.bind(apps.get(position));
        }

        @Override
        public int getItemCount() {
            return apps.size();
        }


        // This inner class represents a single item view in the RecyclerView.
        // It holds references to the UI elements (ImageView, TextView) within each item.

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ImageView appIcon;
            private TextView appLabel;
            private TextView appName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                appIcon = itemView.findViewById(R.id.item_app_icon);
                appLabel = itemView.findViewById(R.id.item_app_label);
                appName = itemView.findViewById(R.id.item_app_name);
                itemView.setOnClickListener(this);
            }

            public void bind(AppDetail appDetail) {
                appIcon.setImageDrawable(appDetail.icon);
                appLabel.setText(appDetail.label);
                appName.setText(appDetail.name);
            }

            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(view, getAdapterPosition());
                }
            }
        }
    }
//    Syntax start of Drag and Drop

    // This variable defines a simple callback for handling item touch events in the RecyclerView.
    // It allows for moving items up, down, to the start, or to the end of the list.
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {

           int fromPosition = viewHolder.getAdapterPosition();
           int toPosition = target.getAdapterPosition();

            Collections.swap(apps,fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);


            return false;
        }
// Syntax end of Drag and Drop

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

// Already declared it 0 on OnMove method
        }
    };

}

