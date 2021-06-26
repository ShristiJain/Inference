package com.example.inference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaintClass extends View {

    public LayoutParams layoutParams;
    private Paint brush=new Paint();
    private Path path =new Path();
    private int pensize,erasersize,curentColor;
    private ArrayList<Path> pathArrayList;
    private ArrayList<Integer> colorArrayList;
    private ArrayList<Float> penArrayList;
    private ArrayList<Float> eraserArrayList;
    private Bitmap btnBackground , btnView;
    private  Canvas mcanvas;
    private ArrayList<Bitmap> listAction=new ArrayList<>();
   // private FirebaseFirestore firebaseFirestore;
    //private DocumentReference documentReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    HashMap<String,Object> map=new HashMap<>();
    String hostid;
    private int k=1;
    float Xx, Yy;


    public PaintClass(Context context) {
        super(context);
        brush.setAntiAlias(true);
        brush.setColor(Color.BLACK);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(8f);
        brush.setStrokeCap(Paint.Cap.ROUND);

        pathArrayList=new ArrayList<>();
        colorArrayList = new ArrayList<>();
        curentColor= Color.BLACK;
        pensize =erasersize=8;


        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        firebaseDatabase= FirebaseDatabase.getInstance();
       // documentReference = firebaseFirestore.collection("Game").document(hostid);

    }

    public PaintClass(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        brush.setAntiAlias(true);
        brush.setColor(Color.BLACK);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(8f);
        brush.setStrokeCap(Paint.Cap.ROUND);

        pathArrayList=new ArrayList<>();
        colorArrayList = new ArrayList<>();
        curentColor= Color.BLACK;
        pensize=erasersize=8;


        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        firebaseDatabase= FirebaseDatabase.getInstance();
      //  documentReference = firebaseFirestore.collection("Game").document(hostid);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        btnBackground= Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        btnView= Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        mcanvas= new Canvas(btnView);

        Log.d("PaintClass", "onSizeChanged: called ");

    }

    public String getId(String id)
    {
        hostid =id;
        return hostid;
    }

    private float px(int pensize)
    {
        //return pensize*(getResources().getDisplayMetrics().density);
        return Float.parseFloat(pensize+"");
    }

    public void setSizePen(int s)
    {
        pensize=s;
        brush.setStrokeWidth(px(pensize));
    }

    public void setPenColor(int color)
    {
        curentColor=color;
        brush.setColor(color);
    }

    public void setSizeEraser(int s)
    {
        erasersize=s;
        brush.setStrokeWidth(px(erasersize));
    }

    public void enableEraser()
    {
        brush.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void disableEraser()
    {
        brush.setXfermode(null);
        brush.setShader(null);
        brush.setMaskFilter(null);
    }

   // public void addLastAction(Bitmap bitmap)
   // {
     //   listAction.add(bitmap);
   // }

    /*public void returnLastAction()
    {
        databaseReference = firebaseDatabase.getReference("Game").child(hostid);
        if(listAction.size()>0)
        {
            listAction.remove(listAction.size()-1);
            if(listAction.size()>0)
            {
                btnView= listAction.get(listAction.size()-1);
            }
            else
            {
                btnView= Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
            }

            mcanvas=new Canvas(btnView);
            invalidate();
        }
        databaseReference.child("Change").child("Name").setValue("No Value");
    }*/

    public void setK(int flag)
    {
        k=flag;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x= event.getX();
        float y=event.getY();
        databaseReference = firebaseDatabase.getReference("Game").child(hostid);
        map.put("X",x);
        map.put("Y",y);

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN: path.moveTo(x,y);
            Xx = x;
            Yy = y;
            map.put("Xx",Xx);
            map.put("Yy",Yy);
            map.put("Type","DOWN");
            databaseReference.child("Data").setValue(map);
            map.remove("Type");
            break;
            case MotionEvent.ACTION_MOVE: path.lineTo(x,y);
            mcanvas.drawPath(path,brush);
            map.put("Type","MOVE");
            databaseReference.child("Data").setValue(map);
            map.remove("Type");
            invalidate();
            break;
            case MotionEvent.ACTION_UP: path.reset();
           // addLastAction(getBitmap());
            map.put("Type","UP");
            databaseReference.child("Data").setValue(map);
            map.remove("Type");
            break;
            default: return false;
        }
        return k == 1;
    }

    public Bitmap getBitmap()
    {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        //Bitmap bitmap=Bitmap.createBitmap(this.getDrawingCache());
        Bitmap bitmap=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
        this.setDrawingCacheEnabled(false);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(btnBackground,0,0,null);
        canvas.drawBitmap(btnView,0,0,null);
        Log.d("PaintClass", "onDraw: called ");

       /* Gson gson= new Gson();
        String json1 = gson.toJson(btnBackground);
        String json2= gson.toJson(btnView);

        map.put("Canvas",canvas);
        map.put("BtnBackground",json1);
        map.put("BtnView",json2);

       documentReference = firebaseFirestore.collection("Game").document(hostid);
       documentReference.update(map);*/
       // documentReference.update("Canvas",canvas);
       // documentReference.update("BtnBackground",btnBackground);
       // documentReference.update("BtnView",json2);

    }

    public void down(float x, float y)
    {
        path.moveTo(x,y);
    }

    public void move(float x, float y)
    {
        path.lineTo(x,y);
        mcanvas.drawPath(path,brush);
        invalidate();
    }

    public void up(float x, float y)
    {
        databaseReference = firebaseDatabase.getReference("Game").child(hostid);
        path.reset();
        //addLastAction(getBitmap());
        databaseReference.child("Data").child("Type").setValue("No Value");

    }

   /* private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }*/

}

