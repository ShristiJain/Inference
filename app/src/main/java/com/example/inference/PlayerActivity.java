package com.example.inference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PlayerActivity extends AppCompatActivity {

    private View myView;
    SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    private DocumentReference doc, docHost;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,databaseReference1;
    private FirebaseAuth firebaseAuth;
    private String userId,id,playerName;
    private RecyclerView recyclerView;
    private player_list_adapter adapter;
    private ArrayList<String> names;
    private Button startGame;
    int k;
    ListenerRegistration lis1,lis2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        sharedPreferences=getSharedPreferences("myPref",MODE_PRIVATE);
        db=FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView=findViewById(R.id.recycler_view_player);
        names=new ArrayList<>();
        startGame=findViewById(R.id.start_game_button);
       // adapter= new player_list_adapter(this,)
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null) {
            userId = firebaseAuth.getCurrentUser().getUid();
        }
     //   else
      //  {

       // }
        //
        final Intent intent=getIntent();
         playerName = sharedPreferences.getString("Name","Null");
       // player=findViewById(R.id.player1_text);


       // FirebaseDynamicLinks.getInstance()
                //.getDynamicLink(intent)
              //  .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                 //   @Override
                 //   public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                      //  Toast.makeText(PlayerActivity.this, "success", Toast.LENGTH_SHORT).show();
        Uri deepLink = null;
        if (intent.hasExtra("Link")) {
             k = 0;
            Toast.makeText(PlayerActivity.this, "successful", Toast.LENGTH_SHORT).show();
            id = intent.getStringExtra("HostUid");
            names.add(playerName);
            adapter = new player_list_adapter(PlayerActivity.this, names);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(PlayerActivity.this, 3));
            doc = db.collection("Game").document(id);
            databaseReference= firebaseDatabase.getReference("Game").child(id).child("Basic");
            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(PlayerActivity.this, id, Toast.LENGTH_SHORT).show();
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) {
                            if ((long) (document.get("PlayersNumber")) < 8) {
                                //doc.update("PlayersNumber", FieldValue.increment(1));
                                doc.update("Players", FieldValue.arrayUnion(playerName));
                                for(int i =0; i <20; i++)
                                {
                                }
                                ArrayList<String> arr = (ArrayList<String>) (document.get("Players"));
                                int m = arr.size();
                                for (int i = 0; i < 15; i++) {
                                   // Toast.makeText(PlayerActivity.this, "look" + arr.get(i), Toast.LENGTH_SHORT).show();
                                }
                                k=1;
                                listen();
                                doc.update("PlayersNumber", m);
                                //Toast.makeText(PlayerActivity.this, playerName+""+m+" joined", Toast.LENGTH_SHORT).show();
                                //  names= (ArrayList<String>)document.get("Players");
                                //  adapter=new player_list_adapter(PlayerActivity.this,names);
                                //  recyclerView.setAdapter(adapter);
                                //  recyclerView.setLayoutManager(new GridLayoutManager(PlayerActivity.this,3));
                            } else if ((long) (document.get("PlayersNumber")) >= 8) {
                                Toast.makeText(PlayerActivity.this, "Room is full", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(PlayerActivity.this, MenuActivity.class);
                                startActivity(intent1);
                                finish();
                            }
                        }
                        else
                            {
                                Toast.makeText(PlayerActivity.this, "No such game", Toast.LENGTH_SHORT).show();
                            }

                    } else {
                        Toast.makeText(PlayerActivity.this, "bahar task", Toast.LENGTH_SHORT).show();
                        // Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
        else
        {
            docHost= db.collection("Game").document(userId);
            names.add(playerName);
            adapter=new  player_list_adapter(PlayerActivity.this,names );
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(PlayerActivity.this,3));
            databaseReference1= firebaseDatabase.getReference("Game").child(userId);
            lis2= docHost.addSnapshotListener(PlayerActivity.this , new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable final DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error!=null)
                    {
                        Log.w("PlayerActivity",error);
                    }
                    if(value!=null && value.exists())
                    {
                      ArrayList<String>  array= (ArrayList<String>) value.get("Players");
                      List<String> a= Arrays.asList(getResources().getStringArray(R.array.color_name));
                      long number=(long)(value.get("PlayersNumber"));
                      if(number==2)
                      {
                          startGame.setEnabled(true);
                          startGame.setBackgroundResource(R.drawable.custom_buttom);
                          startGame.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                    docHost.update("Status","Started");
                                    Intent intent2=new Intent(PlayerActivity.this,PictionaryActivity.class);
                                    intent2.putExtra("Id",userId);
                                    startActivity(intent2);
                                    finish();
                              }
                          });
                          int pos1;
                          HashMap<String,String> hhash=new HashMap<>();
                          String tem;
                          for(int j=7;j>=0;j--)
                          {
                              pos1=Integer.parseInt(Math.random()*10+"");
                              if(pos1>j)
                              {
                                  pos1=j;
                              }
                              hhash.put(array.get(pos1),a.get(j));
                              array.add(pos1,array.get(j));
                          }
                          databaseReference1.child("Allocation").setValue(hhash);
                      }
                        assert names != null;
                        int n=array.size();
                        String nn=array.get(n-1);
                        int num =names.size();

                        Toast.makeText(PlayerActivity.this, nn+" joined", Toast.LENGTH_SHORT).show();
                        if(n>1)

                        {
                            names.clear();
                            recyclerView.getAdapter().notifyItemRangeRemoved(0,num);
                            for(int i=0;i<n;i++)
                            {
                                names.add(array.get(i));
                                 }
                            (recyclerView.getAdapter()).notifyItemRangeInserted(0,n);
                            //recyclerView.getAdapter().notifyItemInserted(n-1);
                            recyclerView.smoothScrollToPosition(n-1);
                        }

                    }
                }
            });
        }

                        //player.setText(id);
                        //Toast.makeText(PlayerActivity.this, i+"", Toast.LENGTH_SHORT).show();
                    //}
               // })
                //.addOnFailureListener(this, new OnFailureListener() {
                  //  @Override
                    //public void onFailure(@NonNull Exception e) {
                      //  Toast.makeText(PlayerActivity.this, "failure", Toast.LENGTH_SHORT).show();
                       // Log.w(TAG, "getDynamicLink:onFailure", e);
                  //  }
                //});




        myView=getWindow().getDecorView();
        myView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility==0)
                {
                    myView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }
            }
        }
        );

    }

    private void listen() {
       lis1= doc.addSnapshotListener(PlayerActivity.this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("PlayerActivity", error);
                }
                if (value != null && value.exists()) {
                    ArrayList<String> array = (ArrayList<String>) (value.get("Players"));

                    if (((String) (value.get("Status"))).equalsIgnoreCase("Started")) {
                        Intent intent2 = new Intent(PlayerActivity.this, PictionaryActivity.class);
                        intent2.putExtra("Id",id);
                        startActivity(intent2);
                        finish();
                    }
                    assert names != null;
                    assert array != null;
                    int num = 1;
                    num = array.size();
                    doc.update("PlayersNumber", num);
                    databaseReference.child("Players").setValue(array);
                    databaseReference.child("PlayersNumber").setValue(num);
                    // String nn=array.get(num-1);
                    int n = names.size();

                    // Toast.makeText(PlayerActivity.this, nn+" joined", Toast.LENGTH_SHORT).show();
                    if (num > 1) {
                        names.clear();
                        recyclerView.getAdapter().notifyItemRangeRemoved(0, n);
                        for (int i = 0; i < num; i++) {
                            names.add(array.get(i));
                        }
                        (recyclerView.getAdapter()).notifyItemRangeInserted(0, num);
                        recyclerView.smoothScrollToPosition(num - 1);
                    }

                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
        {
            myView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }
}
