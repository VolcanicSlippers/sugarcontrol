package com.karimtimer.sugarcontrol.History;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.models.Record;

public class HistoryActivity extends AppCompatActivity {
    private static String TAG = "HistoryActivity";

    private RecyclerView mRecordList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseRecyclerAdapter<Record, RecordViewHolder> adapter;
   // public TextView currentMonth;
    private Toolbar toolbar;
    private TextView cardOptions, sugarLevelTxt;
    private ImageView emote;
    private Context mCtx;
    private ImageView deleteIcon;

//TODO: figure out how to click on card then show notes, carbs and other details with the post.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_record_database);


        sugarLevelTxt = (TextView) findViewById(R.id.sugar_level_reading_on_card);
        emote = (ImageView) findViewById(R.id.range_pic_on_card);
        deleteIcon = (ImageView) findViewById(R.id.delete_option_card);


        //Toolbar for saving items
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_done);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();


        mRecordList = (RecyclerView) findViewById(R.id.record_list);
        mRecordList.setLayoutManager(new LinearLayoutManager(this));
        mRecordList.setHasFixedSize(true);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecordList.setLayoutManager(mLayoutManager);
      //  currentMonth = findViewById(R.id.month);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Record").child("SugarLevel").child(mAuth.getUid());
        mDatabase.keepSynced(true);



        DatabaseReference personRef = FirebaseDatabase.getInstance().getReference().child("Record").child("SugarLevel").child(mAuth.getUid());
        Query query = personRef.orderByKey();
        //.orderByChild("month").equalTo(1);
        FirebaseRecyclerOptions<Record> options =
                new FirebaseRecyclerOptions.Builder<Record>()
                        .setQuery(query, Record.class)
                        .build();




        adapter = new FirebaseRecyclerAdapter<Record, HistoryActivity.RecordViewHolder>(options) {
            @NonNull
            @Override
            public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.record_list, parent, false);

                return new RecordViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull RecordViewHolder holder, int position, @NonNull Record model) {
                final String post_key = getRef(position).getKey().toString();
                    holder.setSugarLevel(model.getSugarLevel()); //set the sugar level reading etc;
                    holder.setTime(model.getTime());
                    holder.setDate(model.getDate());
                    double sugarLevel = Double.parseDouble(model.getSugarLevel());
            }
        };

        mRecordList.setAdapter(adapter);

//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                //delete item
//                Toast.makeText(HistoryActivity.this, "on Swiped ", Toast.LENGTH_SHORT).show();
//                //Remove swiped item from list and notify the RecyclerView
//                final int position = viewHolder.getAdapterPosition();
//                adapter.notifyItemRemoved(position);
//            }
//
//        });
////
//       ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
//
//           @Override
//           public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//               Toast.makeText(HistoryActivity.this, "on Move", Toast.LENGTH_SHORT).show();
//               return false;
//           }
//
//           @Override
//           public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//
//           }
//       };

    }




    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(HistoryActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.card_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_option_card:
                Toast.makeText(this, "osfdosdjf", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    //we need a viewholder to set up the Recycler  view.
    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        View mView;




        public RecordViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG,"jump");
                    //Show the webViewPost Here
                }
            });


        }

        public void setDate(String date) {

            TextView post_date = mView.findViewById(R.id.date_reading_on_card);
            post_date.setText(date);
        }

//        public void setCarbs(String carbs) {
//            TextView post_carbs = mView.findViewById(R.id.carb_reading_on_card);
//            post_carbs.setText(carbs);
//        }

        public void setSugarLevel(String sugarLevel) {
            TextView post_sugar_level = mView.findViewById(R.id.sugar_level_reading_on_card);
            ImageView image = mView.findViewById(R.id.range_pic_on_card);
            double sugarLevels = Double.parseDouble(sugarLevel);
            if (sugarLevels > 10) {
                post_sugar_level.setText(sugarLevel);
                image.setImageResource(R.drawable.ic_priority_high_black_24dp);
                image.setColorFilter(Color.RED);


            }else if(sugarLevels < 4){
                post_sugar_level.setText(sugarLevel);
                image.setImageResource(R.drawable.ic_priority_high_black_24dp);
                image.setColorFilter(Color.BLUE);

            }
            else {
                post_sugar_level.setText(sugarLevel);
                image.setVisibility(View.GONE);
            }
        }

        public void setTime(String time) {
            TextView post_time = mView.findViewById(R.id.time_on_card);
            post_time.setText(time);
        }

//        public void setInsulinUnits(String insulinUnits) {
//            TextView post_insulin_units = mView.findViewById(R.id.insulin_units_on_card);
//            post_insulin_units.setText(insulinUnits);
//        }

//        public void setNotes(String notes) {
//            TextView post_notes = mView.findViewById(R.id.notes_on_card);
//            post_notes.setText(notes);
//        }
    }

}
