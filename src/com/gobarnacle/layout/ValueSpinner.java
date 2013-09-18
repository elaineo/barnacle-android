package com.gobarnacle.layout;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gobarnacle.R;


public class ValueSpinner extends LinearLayout {
    public static final int NUMBER= 0;
    public static final int TEXT = 1;
     
    private int style=0;
    private TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private int min=1;
    private int max=100;
     
    private ArrayList<String> valueString=new ArrayList<String>();
    private int value=0;
    private TextView textLabel;
    private String textLabelVal;
     
    Button incButton;
    Button decButton;
    TextView valueView;
 
    public ValueSpinner(Context context) {
        super(context);     
        init();
    }
 
    public ValueSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ValueSpinner,0,0);
        try {
        	textLabelVal = a.getString(R.styleable.ValueSpinner_labelText);
        } finally {
        	init();
        }
    }
 
    public ValueSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init() {       
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int buttSize = 50;
        Log.d("dims", width+","+height);
        if (height>900)
        	buttSize = 80;
        	
    	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        valueView = new TextView(getContext());
        textLabel = new TextView(getContext());
        incButton = (Button) inflater.inflate(R.layout.template_buttonright,null);
        textLabel.setText(textLabelVal);
 
        decButton = (Button) inflater.inflate(R.layout.template_buttonleft,null);
        
        
        LinearLayout leftDiaper = new LinearLayout(getContext());
        LinearLayout rightDiaper = new LinearLayout(getContext());
        LinearLayout.LayoutParams crap = new LinearLayout.LayoutParams(buttSize, buttSize);
        leftDiaper.addView(decButton,crap);
        rightDiaper.addView(incButton,crap);
        
        valueView.setText(getValueString());
        valueView.setTextSize(15);
        valueView.setGravity(Gravity.CENTER);
        textLabel.setGravity(Gravity.CENTER);
        textLabel.setTextSize(15);
        paint.setTextSize(valueView.getTextSize());        
        
        this.addView(leftDiaper, new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));        
        this.addView(valueView, new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.addView(rightDiaper, new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.addView(textLabel, new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        
        reCalculateWidthAndCaption();
         
        decButton.setOnClickListener(new View.OnClickListener() {                   	
			@Override
			public void onClick(View arg0) {
                touchSet(0);
			}
        });
/**        decButton.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				touchFF(0);
				return false;
			}        	
        });**/
        decButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchSet(0);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.arg1 = 0;
                    msg.arg2 = 250; // this btn's repeat time in ms
                    v.setTag(v); // mark btn as pressed 
                    repeatHandler.sendMessageDelayed(msg, msg.arg2);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setTag(null); // mark btn as not pressed
                    break;
                }
                return true; // return true to prevent calling btn onClick handler
            }
        });
        
        
        
        incButton.setOnClickListener(new View.OnClickListener() {           
			@Override
			public void onClick(View arg0) {
                touchSet(1);
			}
        });
        incButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchSet(0);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.arg1 = 1; //1 for incButton
                    msg.arg2 = 250; // this btn's repeat time in ms
                    v.setTag(v); // mark btn as pressed 
                    repeatHandler.sendMessageDelayed(msg, msg.arg2);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setTag(null); // mark btn as not pressed
                    break;
                }
                return true; // return true to prevent calling btn onClick handler
            }
        });
    }
    
    public final Handler repeatHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) { 
            case 1: 
            	Button btn;
            	if(msg.arg1==0) 
            		btn = decButton;
        		else
        			btn = incButton;
                if (btn.getTag() != null) { // button is still pressed
                    touchSet(msg.arg1); // perform Click or different long press action
                    Message msg1 = new Message(); // schedule next btn pressed check
                    msg1.copyFrom(msg);
                    repeatHandler.sendMessageDelayed(msg1, msg1.arg2);
                }
                break;
            } 
        }
    };
    
    
    protected void touchSet(int i) {
        int p = i ^ getOrientation();
        switch(p) {
        case 0:
            value--;
            break;
        case 1:
            value++;
            break;
        }
        value = value < min ? min : value > max ? max : value;
        valueView.setText(getValueString());        
        invalidate();
    }
    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        reCalculateWidthAndCaption();
    }
    private String getValueString() {
        String ret = "";
        switch(style) {
        case NUMBER:
            ret = String.valueOf(value);
            break;
        case TEXT:
            try {
            ret = (String) (valueString.get(value) == null ? "" : valueString.get(value));
            } catch (Exception e) {
                ret = "";
            }
             
            break;
        }
        return ret;
    }
    public int getStyle() {
        return style;
    }
    private void reCalculateWidthAndCaption() {
        MarginLayoutParams lp = (MarginLayoutParams) valueView.getLayoutParams();
        lp.width = (int) StaticLayout.getDesiredWidth(" WWW ", paint) ;
        if (valueString.size()>0) {
            String s="";
            for(int i=0; i < valueString.size(); i++) {
                s = s.length() > valueString.get(i).length() ? s : valueString.get(i);
            }
            int w = (int) StaticLayout.getDesiredWidth(s, paint);
            lp.width = w > lp.width ? w : lp.width;      
        }
        /*
        switch(getOrientation()) {
        case LinearLayout.HORIZONTAL:
            lp.width += 10;
            lp.leftMargin = 5;
            lp.rightMargin= 5;
            break;
        case LinearLayout.VERTICAL:
            lp.height = 60;
            lp.topMargin= 5;
            lp.bottomMargin= 5;
            break;
        }*/
 
        valueView.setText(getValueString());
    }
    public void setStyle(int style) {
        this.style = style;
        reCalculateWidthAndCaption();
    }
 
    public int getMin() {
        return min;
    }
 
    public void setMinValue(int min) {
        this.min = min;
        value = value < min ? min : value;
    }
 
    public int getMax() {
        return max;
    }
 
    public void setMaxValue(int max) {
        this.max = max;
        value = value > max ? max : value;
    }
 
    public int getValue() {
        return value;
    }
 
    public void setValue(int value) {
        this.value = value;
        valueView.setText(getValueString());
    }
    public void setValueString(String[] valueString) {
        if (valueString==null)
            return;
        if (valueString.length==0)
            return;
        this.valueString.clear(); 
        for (int i=0; i< valueString.length; i++)
            this.valueString.add(valueString[i]);
     
        this.min =0;
        this.max = this.valueString.size()-1;
        this.value=0;
        this.style=TEXT;
        reCalculateWidthAndCaption();       
    }
    public void setValueString(ArrayList<String> valueString) {
        if (valueString==null)
            return;
        if (valueString.size()==0)
            return;
        this.valueString=valueString;
     
        this.min =0;
        this.max = this.valueString.size()-1;
        this.value=0;
        this.style=TEXT;
        reCalculateWidthAndCaption();
    }
    public void setNextBackgroundResource(int background) {
        incButton.setBackgroundResource(background);
        MarginLayoutParams lp = (MarginLayoutParams) incButton.getLayoutParams();
        lp.width=50;
        lp.height=50;
    }
    public void setPrevBackgroundResource(int background) {
        decButton.setBackgroundResource(background);
        MarginLayoutParams lp = (MarginLayoutParams) decButton.getLayoutParams();
        lp.width=50;
        lp.height=50;
    }
 
}