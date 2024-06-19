package com.parasolka.fishingapp5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class Send_Post extends AppCompatActivity {
    private static final String TAG = "Send_Post";
    private DatabaseReference acc;
    private RecyclerView recycle;
    private List<forfriends> prolist3;
    private String userId;
    private FirebaseUser vapar;
    private Button b1;
    private MyAdapter3 myadapter;
    private DatabaseReference refrence;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_post);

        recycle = findViewById(R.id.sendfriendlrecycler);
        acc = FirebaseDatabase.getInstance().getReference().child("users");
        vapar = FirebaseAuth.getInstance().getCurrentUser();
        if (vapar == null) {
            Log.e(TAG, "FirebaseUser is null. User not authenticated.");
            return;
        }
        userId = vapar.getUid();

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.blue));

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycle.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setReverseLayout(true);
        gridLayoutManager.setStackFromEnd(true);

        prolist3 = new ArrayList<>();
        myadapter = new MyAdapter3(Send_Post.this, prolist3);
        recycle.setAdapter(myadapter);

        refrence = FirebaseDatabase.getInstance().getReference(userId + "friends");

        eventListener = refrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                prolist3.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    forfriends dete3 = itemSnapshot.getValue(forfriends.class);
                    if (dete3 != null) {
                        prolist3.add(dete3);
                    } else {
                        Log.e(TAG, "Null data found in snapshot: " + itemSnapshot.toString());
                    }
                }
                myadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (refrence != null && eventListener != null) {
            refrence.removeEventListener(eventListener);
        }
    }
}
