package com.karimtimer.sugarcontrol.Record;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.models.Record;

public class PreviousRecords extends AppCompatActivity {
    private RecyclerView mRecordList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseRecyclerAdapter<Record, PreviousRecords.RecordViewHolder> adapter;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_records);

      //  String mGroupId = mDatabase.push().getKey();
//        Intent intent= getIntent(); // gets the previously created intent
//       final String value = intent.getStringExtra("userKey");
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("records");
//        mDatabase.keepSynced(true);

        mRecordList = (RecyclerView) findViewById(R.id.record_list);
        mRecordList.setLayoutManager(new LinearLayoutManager(this));
        mRecordList.setHasFixedSize(true);

//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Journal");
//        mDatabase.keepSynced(true);

        DatabaseReference personRef = FirebaseDatabase.getInstance().getReference().child("records");
        Query query = personRef.orderByKey();

        FirebaseRecyclerOptions<Record> options =
                new FirebaseRecyclerOptions.Builder<Record>()
                        .setQuery(query, Record.class)
                        .build();


        adapter =  new FirebaseRecyclerAdapter<Record, PreviousRecords.RecordViewHolder>(options) {
            @NonNull
            @Override
            public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.record_list, parent, false);

                return new PreviousRecords.RecordViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull PreviousRecords.RecordViewHolder holder, int position, @NonNull Record model) {

                mCurrentUser = mAuth.getCurrentUser();

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference yourRef = rootRef.child("records").child(mCurrentUser.getUid());
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            Record category = dataSnapshot.getValue(Record.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                yourRef.addListenerForSingleValueEvent(eventListener);




                final String post_key = getRef(position).getKey().toString();
                holder.setNotes(model.getNotes());
            //    holder.setTime(model.getTime());
             //   holder.setDate(model.getDate());

            }
        };
        mRecordList.setAdapter(adapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
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
        }
//
//        public void setTime(String time) {
//            TextView post_time = mView.findViewById(R.id.in_time);
//            post_time.setText(time);
//        }
//
//        public void setDate(String date) {
//            TextView post_date = mView.findViewById(R.id.in_date);
//            post_date.setText(date);
//        }

        public void setNotes(String notes){
        //    TextView postSugarLevel = mView.findViewById(R.id.show_notes);
        }
    }

}