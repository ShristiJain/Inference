package com.example.inference;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class MenuActivity extends AppCompatActivity {

    private  View myView;
    TextView playerNameText, link;
    Button create,join,copy,share,enter;
    ImageButton settings,sound;
    int soundTrack;

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth firebaseAuth;
    String userId,playerName;
    Dialog dialog;
    HashMap<String,Object> data =new HashMap<>();
    SharedPreferences sharedPreferences, sharedPreferences1;

   // EditText textView;
    DatabaseReference databaseReference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        firebaseAuth=FirebaseAuth.getInstance();
        userId=firebaseAuth.getCurrentUser().getUid();

        sharedPreferences=getSharedPreferences("myPref",MODE_PRIVATE);
        sharedPreferences1=getSharedPreferences("myPref",MODE_PRIVATE);

        Intent intent=getIntent();
        //String playerName=intent.getStringExtra("PlayerName");
        if(sharedPreferences.contains("Name")) {
            playerName = sharedPreferences.getString("Name", "Null");
        }
        soundTrack=intent.getIntExtra("SoundTrack",1);
        playerNameText=findViewById(R.id.player_name_text);
        playerNameText.setText(playerName);

        dialog= new Dialog(MenuActivity.this);
        dialog.setContentView(R.layout.dialog_create_game);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_buttom));

        copy=dialog.findViewById(R.id.copy_dialog_button);
        share=dialog.findViewById(R.id.share_dialog_button);
        enter=dialog.findViewById(R.id.enter_game_button);
        link=dialog.findViewById(R.id.game_link_text);

        create=findViewById(R.id.create_button);
        join=findViewById(R.id.join_button);
        settings=findViewById(R.id.settings_image_button);
        sound=findViewById(R.id.sound_image_button);

       // HashMap<String,String> data =new HashMap<>();

       /* databaseReference = firebaseDatabase.getReference("Words");
        textView = findViewById(R.id.editText);
        String str = textView.getText().toString();
        StringTokenizer st= new StringTokenizer(str);
        int ln= st.countTokens();
        int i,c=0;
        String strr="";
        HashMap<String,String> wd=new HashMap<>();
        for(i=0;i<ln;i++)
        {
            String B = st.nextToken();
            if(B.indexOf('.')==-1)
                strr=strr+B+" ";
            else {
                wd.put(c + "", strr.trim());
                strr = "";
                c++;
            }
        }
        databaseReference.setValue(wd);*/

        create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                final Uri dynamicLinkUri=createLink();


                data.put("Status","Created");
                db.collection("Game").document(userId).set(data);

                copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Game link",dynamicLinkUri.toString() );
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(MenuActivity.this, "Saved to Clipboard", Toast.LENGTH_SHORT).show();
                    }
                });

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = dynamicLinkUri.toString();
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Link :");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, s);
                        startActivity(Intent.createChooser(sharingIntent, "Share text via"));
                        finish();
                    }
                });

                enter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.put("Status","Ongoing");
                        data.put("PlayersNumber",1);
                        //data.put("Chance",playerName);
                        data.put("Players", Arrays.asList(playerName));
                        firebaseDatabase.getReference().child("Game").child(userId).child("Basic").setValue(data);
                        HashMap<String,String> hashMap =new HashMap<>();
                        hashMap.put("Name",playerName);
                        hashMap.put("Status","On");
                        firebaseDatabase.getReference().child("Game").child(userId).child("Chance").setValue(hashMap);
                        db.collection("Game").document(userId).set(data);
                        Intent intent=new Intent(MenuActivity.this,PlayerActivity.class);
                        intent.putExtra("Name",playerName);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.show();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soundTrack==1)
                {
                    soundTrack=0;
                    sound.setBackgroundResource(R.drawable.ic_soundoff);
                }
                else
                {
                    soundTrack=1;
                    sound.setBackgroundResource(R.drawable.ic_soundon);
                }
            }
        });


        myView = getWindow().getDecorView();
        myView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
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

    public Uri createLink()
    {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.inference.com?game_id="+userId))
                .setDomainUriPrefix("https://inferencegame.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                //.setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        link.setText(dynamicLinkUri.toString());
        link.setLinksClickable(true);
        Log.d("MenuActivity.this", "createLink: "+dynamicLinkUri);
        return dynamicLinkUri;
    }
}
