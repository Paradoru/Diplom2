package com.parasolka.fishingapp5;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter3 extends RecyclerView.Adapter<MyAdapter3.productviewholder3> {
    private Context pcontext3;
    private List<forfriends> prolist3;
    private String propic, name2, title, id2, url, key;
    private boolean dataLoaded = false;  // добавляем флаг dataLoaded

    public MyAdapter3(Context pcontext3, List<forfriends> prolist3) {
        this.pcontext3 = pcontext3;
        this.prolist3 = prolist3;
        loadFirebaseData();  // Переносим вызов метода loadFirebaseData() в конструктор
    }

    @NonNull
    @Override
    public productviewholder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_friends_layout, parent, false);
        return new productviewholder3(mView2);
    }

    @Override
    public void onBindViewHolder(@NonNull productviewholder3 holder, int position) {
        Glide.with(pcontext3).load(prolist3.get(position).getTyaimg()).into(holder.c1);
        holder.t1.setText(prolist3.get(position).getTyaame());
        holder.t2.setText(prolist3.get(position).getTyaid());

        holder.b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager manager = (ConnectivityManager) pcontext3.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    String sanitizedPath = sanitizePath(holder.t2.getText().toString());
                    Log.d("SanitizedPath", "Sanitized Path: " + sanitizedPath);

                    // Логирование перед отправкой данных
                    Log.d("SendData", "Data to send: " + "propic: " + propic + ", name2: " + name2 +
                            ", title: " + title + ", id2: " + id2 + ", url: " + url + ", key: " + key);

                    try {
                        DatabaseReference messageRef = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("message_" + sanitizedPath)
                                .child(key);

                        messageRef.child("id2").setValue(id2);
                        messageRef.child("key").setValue(key);
                        messageRef.child("name2").setValue(name2);
                        messageRef.child("propic").setValue(propic);
                        messageRef.child("title").setValue(title);
                        messageRef.child("url").setValue(url);

                        Toast.makeText(v.getContext(), "Sent successfully!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("SendData", "Failed to send data: " + e.getMessage());
                        Toast.makeText(v.getContext(), "Пост был отправлен", Toast.LENGTH_SHORT).show();
                    }

                    Intent i = new Intent(pcontext3, videoimageplayer.class);
                    pcontext3.startActivity(i);
                } else {
                    Toast.makeText(v.getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return prolist3.size();
    }

    private String sanitizePath(String path) {
        return path.replaceAll("[.$#\\[\\]/]", "_");
    }

    private void loadFirebaseData() {
        DatabaseReference mDataBaseRef = FirebaseDatabase.getInstance().getReference();
        String userUid = FirebaseAuth.getInstance().getUid();

        if (userUid == null) {
            Log.d("FirebaseData", "User UID is null. Are you authenticated?");
            return;
        }

        mDataBaseRef.child(userUid + "/temporary/1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    upload xx = snapshot.getValue(upload.class);
                    if (xx != null) {
                        propic = xx.getPropic();
                        name2 = xx.getName2();
                        title = xx.getTitle();
                        id2 = xx.getId2();
                        url = xx.getUrl();
                        key = xx.getKey();
                        dataLoaded = true; // Установка флага загрузки данных

                        // Логирование загруженных данных
                        Log.d("FirebaseData", "Data Loaded: " + "propic: " + propic + ", name2: " + name2 +
                                ", title: " + title + ", id2: " + id2 + ", url: " + url + ", key: " + key);
                    } else {
                        Log.d("FirebaseData", "upload object is null");
                    }
                } else {
                    Log.d("FirebaseData", "snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(pcontext3, "Failed to load data from Firebase.", Toast.LENGTH_SHORT).show();
                Log.d("FirebaseData", "Failed to load data from Firebase: " + error.getMessage());
            }
        });
    }

    static class productviewholder3 extends RecyclerView.ViewHolder {
        CircleImageView c1;
        TextView t1, t2;
        Button b1;

        public productviewholder3(View itemView) {
            super(itemView);
            c1 = itemView.findViewById(R.id.sfriendimage);
            t1 = itemView.findViewById(R.id.sfriendname);
            t2 = itemView.findViewById(R.id.sfriendid);
            b1 = itemView.findViewById(R.id.sfriendshare);
        }
    }
}
