package cn.rrf.complexgame.garbage;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomSurfaceView extends GLSurfaceView {
    private SurfaceRenderer mSurfaceRenderer;

    private void init() {
        mSurfaceRenderer = new SurfaceRenderer();
        setEGLContextClientVersion(2);
        setRenderer(mSurfaceRenderer);
    }

    public CustomSurfaceView(Context context) {
        super(context);
        init();
    }

    public CustomSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        queueEvent(new Runnable() {
            @Override
            public void run() {

            }
        });
        return true;
    }
}
