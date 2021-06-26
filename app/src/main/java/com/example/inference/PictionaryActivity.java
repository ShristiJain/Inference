package com.example.inference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class PictionaryActivity extends AppCompatActivity {

    public Path path;
    public Paint brush=new Paint();
    private View myView;
    PaintClass paintClass;
    int penColor;
    int pensize,erasersize;
    private int k;
    // DocumentReference documentReference;
     //FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private String playerName,id,word;
    private Button undob , penb , eraserb , pensizeb ,colorb,word1,word2,word3,checkAns;
    DatabaseReference databaseReference, databaseReferenceWord;
    FirebaseDatabase firebaseDatabase;
    Dialog dialogWord, dialogWin,dialogWait,dialogShowAllo;
    LinkedList<String> words;
    int ran, flag, chosenWordFlag=0, chosenln, cInference = 60,cWord=20;
    boolean firstTime = true;
    String string, chosen, chosenString,player ,color,playerAl;
    TextView wordText,congoText,displayAns,counterWordText, counterInferenceText,playerNameText,colorNameText,playerNameText1,allocationText;
    EditText answerText;
    Spinner playerSpinner, colorSpinner;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictionary);

        Intent intent= getIntent();
        id=intent.getStringExtra("Id");
       // undob=findViewById(R.id.button_undo);
        penb=findViewById(R.id.button_pen);
        eraserb=findViewById(R.id.button_eraser);
        pensizeb=findViewById(R.id.button_pensize);
        colorb=findViewById(R.id.button_color);
       // undob.setEnabled(false);
        penb.setEnabled(false);
        eraserb.setEnabled(false);
        pensizeb.setEnabled(false);
        colorb.setEnabled(false);
        paintClass= findViewById(R.id.paintClass);
        paintClass.getId(id);
        paintClass.setClickable(false);
        pensize=erasersize=8;
        penColor= Color.BLACK;
        wordText = findViewById(R.id.word_display_text);
        answerText = findViewById(R.id.ans_edit_text);

        dialogWord = new Dialog(PictionaryActivity.this);
        dialogWord.setContentView(R.layout.dialog_word_choose);
        dialogWord.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_buttom));
        dialogWord.setCancelable(false);
        dialogWord.setCanceledOnTouchOutside(false);

        dialogWin = new Dialog(PictionaryActivity.this);
        dialogWin.setContentView(R.layout.dialog_make_inference);
        dialogWin.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_buttom));
        dialogWin.setCancelable(false);
        dialogWin.setCanceledOnTouchOutside(false);

        dialogWait = new Dialog(PictionaryActivity.this);
        dialogWait.setContentView(R.layout.dialog_wait_till_inference);
        dialogWait.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_buttom));
        dialogWait.setCancelable(false);
        dialogWait.setCanceledOnTouchOutside(false);

        dialogShowAllo = new Dialog(PictionaryActivity.this);
        dialogShowAllo.setContentView(R.layout.dialog_show_allocation);
        dialogShowAllo.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_buttom));
        dialogShowAllo.setCancelable(false);
        dialogShowAllo.setCanceledOnTouchOutside(false);

        word1 = dialogWord.findViewById(R.id.button_word1);
        word2 = dialogWord.findViewById(R.id.button_word2);
        word3 = dialogWord.findViewById(R.id.button_word3);
        counterWordText=dialogWord.findViewById(R.id.counter_word_text);

        congoText = dialogWin.findViewById(R.id.congo_text);
        playerSpinner = dialogWin.findViewById(R.id.playername_spinner);
        colorSpinner = dialogWin.findViewById(R.id.colorname_spinner);
        checkAns = dialogWin.findViewById(R.id.check_ans_button);
        displayAns=dialogWin.findViewById(R.id.ans_display_text);
        counterInferenceText = dialogWin.findViewById(R.id.counter_inference_text);

        playerNameText = dialogWait.findViewById(R.id.playername_text);
        playerNameText1 = dialogWait.findViewById(R.id.playername1_text);
        colorNameText = dialogWait.findViewById(R.id.colorname_text);

        allocationText = dialogShowAllo.findViewById(R.id.show_allocation_text);

        // firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        sharedPreferences = getSharedPreferences("myPref",MODE_PRIVATE);
        playerName= sharedPreferences.getString("Name","null");
        firebaseAuth=FirebaseAuth.getInstance();
        words = new LinkedList<>();
        // documentReference= firebaseFirestore.collection("Game").document(id);
        databaseReference = firebaseDatabase.getReference("Game").child(id);
        databaseReferenceWord = firebaseDatabase.getReference("Words");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot value) {
                if(value!= null && value.exists())
                {
                    if(firstTime)
                    {
                         playerAl= value.child("Allocation").child(playerName).getValue()+"";
                         dialogShowAllocation(playerName,playerAl,value.child("Chance").child("Name").getValue()+"");
                    }
                    else if((value.child("Chance").child("Name").getValue()+"").equals(playerName))
                    {
                        answerText.setFocusable(false);
                        if((value.child("Chance").child("Status").getValue()+"").equalsIgnoreCase("On")) {
                            chosen = chooseWordDialogBox();
                        }
                        k=1;
                        paintClass.setK(k);
                        paintClass.setClickable(true);
                        // undob.setEnabled(true);
                        penb.setEnabled(true);
                        eraserb.setEnabled(true);
                        pensizeb.setEnabled(true);
                        colorb.setEnabled(true);
                    }
                    else
                    {
                        answerText.setFocusable(true);
                        k=0;
                        paintClass.setK(k);
                        paintClass.setClickable(false);


                        if((value.child("Chance").child("Status").getValue()+"").equalsIgnoreCase("On"))
                        {
                            Toast.makeText(PictionaryActivity.this, (value.child("Chance").child("Name").getValue()+"")+" is choosing a word", Toast.LENGTH_LONG).show();
                        }

                        if(value.hasChild("Chosen Word") && !((value.child("Chosen Word").getValue()+"").equalsIgnoreCase("No Value")))
                        {
                            chosen = value.child("Chosen Word").getValue()+"";
                            setWordToWordText();
                            Log.d("Check", "onDataChange: "+chosen);
                        }

                        answerText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                Log.d("Dekho", "onEditorAction: "+actionId+" "+EditorInfo.IME_ACTION_SEND);
                                if( (actionId == EditorInfo.IME_ACTION_SEND))
                                {
                                    String ans = answerText.getText().toString();
                                    Log.d("Check", "onEditorAction: "+ans);
                                    if(ans.equalsIgnoreCase(chosen))
                                    {
                                        databaseReference.child("Chance").child("Win").setValue(playerName);
                                    }
                                }
                                return false;
                            }
                        });

                        if(value.hasChild("Data"))
                        {
                            String str=  value.child("Data").child("Type").getValue()+"";
                            float x = Float.parseFloat(value.child("Data").child("X").getValue()+"");
                            float y = Float.parseFloat( value.child("Data").child("Y").getValue()+"");
                            if(str.equalsIgnoreCase("DOWN"))
                            {
                               // paintClass.down(xx,yy);
                            }
                            else if(str.equalsIgnoreCase("MOVE"))
                            {
                                float xx = Float.parseFloat(value.child("Data").child("Xx").getValue()+"");
                                float yy = Float.parseFloat(value.child("Data").child("Yy").getValue()+"");
                                if(flag == 1) {
                                    paintClass.down(xx, yy);
                                    flag=0;
                                }
                                paintClass.move(x,y);
                            }
                            else if(str.equalsIgnoreCase("UP"))
                            {
                                paintClass.up(x,y);
                                flag =1;
                            }
                        }
                        if(value.hasChild("Change"))
                        {
                            String str1 =  value.child("Change").child("Name").getValue()+"";
                            switch (str1)
                            {
                                case "PenSizeModify": paintClass.disableEraser();
                                    //showDialogBox(false);
                                    paintClass.setSizePen((Integer.parseInt(value.child("Change").child("Size").getValue()+"")));
                                   // databaseReference.child("Change").setValue("No Value");
                                    break;
                                case "EraserSizeModify":  paintClass.enableEraser();
                                    //showDialogBox(true);
                                    paintClass.setSizeEraser((Integer.parseInt(value.child("Change").child("Size").getValue()+"")));
                                   // databaseReference.child("Change").setValue("No Value");
                                    break;
                                case "Pen": paintClass.disableEraser();
                                    paintClass.setPenColor(Color.BLACK);
                                   // databaseReference.child("Change").setValue("No Value");
                                    break;
                                case "ColorModify": paintClass.setPenColor((Integer.parseInt( value.child("Change").child("Color").getValue()+"")));
                                  //  databaseReference.child("Change").setValue("No Value");
                                    break;
                               // case "DoUndo": paintClass.returnLastAction();
                                   // databaseReference.child("Change").setValue("No Value");
                                   // break;
                                default: Log.d("PictionaryActivity","Error");
                            }
                        }
                    }
                    if((value.child("Chance").hasChild("Win")) && !((value.child("Chance").child("Win").getValue()+"").equalsIgnoreCase("No Value")))
                    {
                        ArrayList<String> arr= new ArrayList<>();
                        arr= (ArrayList<String>) value.child("Basic").child("Players").getValue();
                        String currentPlayer= value.child("Chance").child("Name").getValue()+"";
                        int pos=0;
                        String putPlayer;
                        for (int i=0;i<8;i++)
                        {
                           if(arr.get(i).equalsIgnoreCase(currentPlayer))
                           {
                              pos=i;
                              break;
                           }
                        }
                        putPlayer=arr.get(pos+1);
                        HashMap<String,String> h=new HashMap<>();
                        CountDownTimer countDownTimer2 =new CountDownTimer(60000,1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                counterInferenceText.setText(cInference+"");
                                cInference--;
                            }

                            @Override
                            public void onFinish() {
                                dialogWord.dismiss();
                                dialogWait.dismiss();
                                dialogWin.dismiss();
                                h.put("Name",putPlayer);
                                h.put("Status","On");
                                databaseReference.child("Chance").setValue(h);
                            }
                        }.start();

                        if((value.child("Chance").child("Win").getValue()+"").equalsIgnoreCase(playerName))
                        {
                            dialogMakeInference(true,arr);
                            checkAns.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if((value.child("Allocation").child(player).getValue()+"").equalsIgnoreCase(color))
                                    {
                                        displayAns.setText("TRUE");
                                    }
                                    else
                                    {
                                        displayAns.setText("FALSE");
                                    }
                                }
                            });
                        }
                        else if((value.child("Chance").child("Name").getValue()+"").equalsIgnoreCase(playerName))
                        {
                            dialogMakeInference(false,arr);
                            checkAns.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if((value.child("Allocation").child(player).getValue()+"").equalsIgnoreCase(color))
                                    {
                                        displayAns.setText("TRUE");
                                    }
                                    else
                                    {
                                        displayAns.setText("FALSE");
                                    }
                                }
                            });
                        }
                        else
                        {
                            dialogWaitTillInferenceIsMade(currentPlayer,value.child("Chance").child("Win").getValue()+"");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PictionaryActivity", "onCancelled: "+error);
            }
        });


      /*  documentReference.addSnapshotListener(PictionaryActivity.this , new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Log.w("PictionaryActivity",error);
                }
                if(value!=null && value.exists())
                {
                    if(value.get("Chance").equals(playerName))
                    {
                        paintClass.setClickable(true);
                        undob.setEnabled(true);
                        penb.setEnabled(true);
                        eraserb.setEnabled(true);
                        pensizeb.setEnabled(true);
                        colorb.setEnabled(true);
                    }
                    else
                    {
                        if(value.contains("X") && value.contains("Y") && value.contains("Type"))
                        {
                            String str= (String) value.get("Type");
                            float x = Float.parseFloat(value.get("X")+"");
                            float y = Float.parseFloat( value.get("Y")+"");
                            if(str.equalsIgnoreCase("DOWN"))
                            {
                                paintClass.down(x,y);
                            }
                            else if(str.equalsIgnoreCase("MOVE"))
                            {
                                paintClass.move(x,y);
                            }
                            else if(str.equalsIgnoreCase("UP"))
                            {
                                paintClass.up(x,y);
                            }
                        }
                        if(value.contains("Change"))
                        {
                            String str1 = (String) value.get("Change");
                            switch (str1)
                            {
                                case "PenSizeModify": paintClass.disableEraser();
                                    //showDialogBox(false);
                                    paintClass.setSizePen((Integer.parseInt(value.get("Size")+"")));
                                    break;
                                case "EraserSizeModify":  paintClass.enableEraser();
                                    //showDialogBox(true);
                                    paintClass.setSizeEraser((Integer.parseInt(value.get("Size")+"")));
                                    break;
                                case "Pen": paintClass.disableEraser();
                                    paintClass.setPenColor(Color.BLACK);
                                    break;
                                case "ColorModify": paintClass.setPenColor((Integer.parseInt( value.get("Color")+"")));
                                        break;
                                case "DoUndo": paintClass.returnLastAction();
                                        break;
                                default: Log.d("PictionaryActivity","Error");

                            }
                        }
                    }
                }
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

    private void dialogShowAllocation(String playerName, String playerAl,String currPlayer) {
        CountDownTimer cdt = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                dialogShowAllo.dismiss();
                firstTime=false;
                if(playerName.equalsIgnoreCase(currPlayer))
                {
                    databaseReference.child("Set").setValue("Start");
                }
            }
        }.start();
        allocationText.setText("Hey "+playerName+"\n"+"You are Allocated the title of "+playerAl+" for this game.\nSo gear up and start finding the Titles of your friends before anybody else.");
        dialogShowAllo.show();
    }

    private void dialogWaitTillInferenceIsMade(String pname,String cname) {
        playerNameText1.setText(cname);
        playerNameText.setText(cname);
        colorNameText.setText(pname);
        dialogWait.show();
    }

    private void dialogMakeInference(boolean check, ArrayList<String> arr) {
        if(check)
        {
            congoText.setText("Congratulations!! You got the answer right.");
        }
        else
        {
            congoText.setText("Your drawing was rightly guessed!!");
        }
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.color_name,android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter1);
        ArrayAdapter adapter2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,arr);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerSpinner.setAdapter(adapter2);
        dialogWin.show();
        playerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                player= arr.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("PictionaryActivity", "onNothingSelected: ");
            }
        });
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> list= Arrays.asList(getResources().getStringArray(R.array.color_name));
                color = list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("PictionaryActivity", "onNothingSelected: ");
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


    public void penSizeModify(View view) {
        paintClass.disableEraser();
        showDialogBox(false);
       // databaseReference.child("Change").setValue("PenSizeModify");
    }



    private void showDialogBox(final boolean isEraser) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_size,null,false);

        TextView heading = view.findViewById(R.id.size_heading);
        SeekBar seekBar= view.findViewById(R.id.size_seekbar);
        final TextView status= view.findViewById(R.id.status_size);
        seekBar.setMax(99);

        if(isEraser)
        {
            heading.setText("Set Eraser Size");
            status.setText("Size : "+erasersize);
        }
        else
        {
            heading.setText("Set Pen Size");
            status.setText("Size : "+pensize);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                HashMap<String,Object> hash = new HashMap<>();
                if (isEraser)
                {
                    erasersize= progress+1;
                    status.setText("Size : "+erasersize);
                   // hash.put("Name","EraserSizeModify");
                   // hash.put("Size",erasersize);
                   // databaseReference.child("Change").setValue(hash);
                    paintClass.setSizeEraser(erasersize);
                }
                else
                {
                    pensize= progress+1;
                    status.setText("Size : "+pensize);
                    hash.put("Name","PenSizeModify");
                    hash.put("Size",pensize);
                    databaseReference.child("Change").setValue(hash);
                    paintClass.setSizePen(pensize);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String,Object> hash = new HashMap<>();
                if (isEraser)
                {
                    hash.put("Name","EraserSizeModify");
                    hash.put("Size",erasersize);
                    databaseReference.child("Change").setValue(hash);
                }
                else
                {
                    hash.put("Name","PenSizeModify");
                    hash.put("Size",pensize);
                    databaseReference.child("Change").setValue(hash);
                }
                dialog.dismiss();
            }
        });
        builder.setView(view);
        builder.show();
    }

    public void eraserSizeModify(View view) {
        paintClass.enableEraser();
        showDialogBox(true);
       // databaseReference.child("Change").setValue("EraserSizeModify");
    }

    public void pen(View view) {
        paintClass.disableEraser();
        paintClass.setPenColor(Color.BLACK);
        databaseReference.child("Change").child("Name").setValue("Pen");
    }


    public void colorModify(View view) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(penColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        HashMap<String , Object> hash =new HashMap<>();
                        penColor=selectedColor;
                        paintClass.setPenColor(penColor);
                        hash.put("Name","ColorModify");
                        hash.put("Color",penColor);
                       // databaseReference.child("Color").setValue(penColor);
                        databaseReference.child("Change").setValue(hash);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    String chooseWordDialogBox()
    {

        databaseReferenceWord.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0;i<3;i++)
                {
                    if(i==0) {
                        ran = (int) (Math.random() * 100);
                        string = snapshot.child(ran+"").getValue()+"";
                        word1.setText(string);
                    }
                    else if(i==1) {
                        ran = (int) (Math.random() * 100);
                        string = snapshot.child(ran+"").getValue()+"";
                        word2.setText(string);
                    }
                    else {
                        ran = (int) ((Math.random() * 100) +(Math.random() * 100)+(Math.random() * 100)+ (71));
                        string = snapshot.child(ran+"").getValue()+"";
                        word3.setText(string);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PictionaryActivity", "onCancelled: "+error);
            }
        });

        dialogWord.show();
       CountDownTimer countDownTimer= new CountDownTimer(20000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished) {
                counterWordText.setText(cWord+"");
                cWord--;
            }

            @Override
            public void onFinish() {
                dialogWord.dismiss();
                //change chance
            }
        }.start();
        databaseReference.child("Chance").child("Status").setValue("Off");
        word1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word = word1.getText().toString();
                chosenWordFlag=1;
                databaseReference.child("Chosen Word").setValue(word);
                wordText.setText(word);
                countDownTimer.cancel();
               dialogWord.dismiss();


            }
        });
        word2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word = word2.getText().toString();
                chosenWordFlag=1;
                databaseReference.child("Chosen Word").setValue(word);
                wordText.setText(word);
                countDownTimer.cancel();
                dialogWord.dismiss();
            }
        });
        word3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word = word3.getText().toString();
                chosenWordFlag=1;
                databaseReference.child("Chosen Word").setValue(word);
                wordText.setText(word);
                countDownTimer.cancel();
                dialogWord.dismiss();
            }
        });

        return word;
    }

    void setWordToWordText()
    {

        chosenln = chosen.length();
        chosenString="";
        for(int i=0;i<chosenln;i++)
        {
            if(chosen.charAt(i)!=' ')
            {
                chosenString = chosenString+"._. ";
            }
            else
            {
                chosenString = chosenString+"   ";
            }
        }
        wordText.setText(chosenString);
        databaseReference.child("Chosen Word").setValue("No Value");
        chosenWordFlag=0;
    }


    //public void doundo(View view) {
      //  paintClass.returnLastAction();
        //databaseReference.child("Change").child("Name").setValue("DoUndo");
    //}
}

