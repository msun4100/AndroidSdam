package kr.me.sdam.seekbars;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar_Reverse extends SeekBar {

    public VerticalSeekBar_Reverse(Context context) {
        super(context);
    }

    public VerticalSeekBar_Reverse(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar_Reverse(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(90);
        c.translate(0, -getWidth());

        super.onDraw(c);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
            	int i=0;
            	i=getMax() - (int) (getMax() * event.getY() / getHeight());
                setProgress(200-i); //setMax(200)으로 하면
//                setProgress(100-i); //디폴트시
//                Log.i("Progress",getProgress()+"");
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
    
}