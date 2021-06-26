package com.example.inference;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
     private View myView;
     SeekBar volume;
     AudioManager audioManager;
     EditText playerName, playerEmail, playerPassword;
     Button loginButton;
     MediaPlayer bgMusic;
     final int soundTrack=1;
     FirebaseAuth firebaseAuth;
     String name,userId,id;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        playerName=findViewById(R.id.player_name_edit);
        playerEmail=findViewById(R.id.player_email_edit);
        loginButton=findViewById(R.id.login_button);
        playerPassword=findViewById(R.id.player_password_edit);
        firebaseAuth= FirebaseAuth.getInstance();
        sharedPreferences=getSharedPreferences("myPref",MODE_PRIVATE);


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                            @Override
                            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                                // Get deep link from result (may be null if no link is found)
                                Uri deepLink = null;
                                if (pendingDynamicLinkData != null) {
                                    deepLink = pendingDynamicLinkData.getLink();

                                    String dynamicLink = deepLink.toString();
                                    if(firebaseAuth.getCurrentUser() != null)
                                    {
                                        Intent intent=new Intent(LoginActivity.this,PlayerActivity.class);
                                       intent.putExtra("Link","Yes");
                                        int i= dynamicLink.lastIndexOf('=');
                                        id=dynamicLink.substring(i+1);
                                       intent.putExtra("HostUid",id);
                                        startActivity(intent);
                                        finish();
                                    }


                                    loginButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            name=playerName.getText().toString();
                                            String email=playerEmail.getText().toString();
                                            String password=playerPassword.getText().toString();

                                            if(TextUtils.isEmpty(name))
                                            {
                                                playerName.setError("Player Name is Required");
                                                return;
                                            }
                                            if(TextUtils.isEmpty(email))
                                            {
                                                playerEmail.setError("Email is required");
                                                return;
                                            }
                                            if(TextUtils.isEmpty(password))
                                            {
                                                playerPassword.setError("Password is required");
                                                return;
                                            }
                                            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(LoginActivity.this,"Welcome  "+name,Toast.LENGTH_SHORT).show();
                                                        userId= firebaseAuth.getCurrentUser().getUid();
                                                        sharedPreferences.edit().putString("Name",name).apply();

                                                        Intent intent=new Intent(LoginActivity.this,PlayerActivity.class);
                                                        // intent.putExtra("PlayerName",name);
                                                        // intent.putExtra("SoundTrack",soundTrack);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(LoginActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    });

                                } else {
                                    // Log.d(TAG, "get failed with ", task.getException());
                                    if(firebaseAuth.getCurrentUser() != null)
                                    {
                                        Intent intent=new Intent(LoginActivity.this,MenuActivity.class);
                                        intent.putExtra("PlayerName",name);
                                        intent.putExtra("SoundTrack",soundTrack);
                                        startActivity(intent);
                                        finish();
                                    }


                                    loginButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            name=playerName.getText().toString();
                                            String email=playerEmail.getText().toString();
                                            String password=playerPassword.getText().toString();

                                            if(TextUtils.isEmpty(name))
                                            {
                                                playerName.setError("Player Name is Required");
                                                return;
                                            }
                                            if(TextUtils.isEmpty(email))
                                            {
                                                playerEmail.setError("Email is required");
                                                return;
                                            }
                                            if(TextUtils.isEmpty(password))
                                            {
                                                playerPassword.setError("Password is required");
                                                return;
                                            }
                                            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(LoginActivity.this,"Welcome  "+name,Toast.LENGTH_SHORT).show();
                                                        userId= firebaseAuth.getCurrentUser().getUid();
                                                        sharedPreferences.edit().putString("Name",name).apply();

                                                        Intent intent=new Intent(LoginActivity.this,MenuActivity.class);
                                                        // intent.putExtra("PlayerName",name);
                                                        // intent.putExtra("SoundTrack",soundTrack);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(LoginActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    });
                                }
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });






       /* if(firebaseAuth.getCurrentUser() != null)
        {
            Intent intent=new Intent(LoginActivity.this,MenuActivity.class);
            intent.putExtra("PlayerName",name);
            intent.putExtra("SoundTrack",soundTrack);
            startActivity(intent);
            finish();
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=playerName.getText().toString();
                String email=playerEmail.getText().toString();
                String password=playerPassword.getText().toString();

                if(TextUtils.isEmpty(name))
                {
                    playerName.setError("Player Name is Required");
                    return;
                }
                if(TextUtils.isEmpty(email))
                {
                    playerEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    playerPassword.setError("Password is required");
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this,"Welcome  "+name,Toast.LENGTH_SHORT).show();
                            userId= firebaseAuth.getCurrentUser().getUid();
                            sharedPreferences.edit().putString("Name",name).apply();

                            Intent intent=new Intent(LoginActivity.this,MenuActivity.class);
                           // intent.putExtra("PlayerName",name);
                            // intent.putExtra("SoundTrack",soundTrack);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });*/

        /*bgMusic=MediaPlayer.create(LoginActivity.this, R.raw.bgmusic);

        button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               bgMusic.start();
            }
        });

        volume=findViewById(R.id.seekBarVolume);
        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        // get the maximum volume
        assert audioManager != null;
        int maxVolume= audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // get the current volume
        int currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //create relation between seekbar and volume
        volume.setMax(maxVolume);
        volume.setProgress(currentVolume);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/


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
