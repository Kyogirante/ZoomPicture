package com.kyo.zoom.gif;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by KyoWang on 2016/12/30 .
 */
public class GifView extends View {

    private final int DEFAULT_DURATION = 1000;

    private Movie mMovie;
    private float mMovieWidth;
    private float mMovieHeight;

    private float mMovieTime;
    private long mPlayStartTime;
    private float mAnimationTime;

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GifView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mMovie != null) {
            setMeasuredDimension((int)mMovieWidth, (int)mMovieHeight);
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mMovie != null) {
            drawGif(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    public void setGif(int resId) {
        mMovie = Movie.decodeStream(getResources().openRawResource(resId));
        initMovie(mMovie);
    }

    public void setGif(String path) {
        mMovie = Movie.decodeFile(path);
        initMovie(mMovie);
    }

    private void init() {
        // Android API 11之后要关闭硬件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void initMovie(Movie movie) {
        if(movie == null) {
            return;
        }
        mMovieWidth = movie.width();
        mMovieHeight = movie.height();
        mMovieTime = movie.duration() == 0 ? DEFAULT_DURATION : movie.duration();
        requestLayout();
    }

    private void drawGif(Canvas canvas) {
        long now = System.currentTimeMillis();
        if(mPlayStartTime == 0) {
            mPlayStartTime = now;
        }
        mAnimationTime = (now - mPlayStartTime) % mMovieTime;
        mMovie.setTime((int)mAnimationTime);
        mMovie.draw(canvas, 0, 0);
        invalidate();
    }
}
