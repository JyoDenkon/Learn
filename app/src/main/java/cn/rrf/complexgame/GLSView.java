package cn.rrf.complexgame;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.view.MotionEvent;

import androidx.annotation.RequiresApi;

public class GLSView extends GLSurfaceView {
	private final GLSVRenderer mGLSVRenderer;

	public GLSView(Context context) {
		super(context);
		mGLSVRenderer = new GLSVRenderer();
		setEGLContextClientVersion(2);
		setRenderer(mGLSVRenderer);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		queueEvent(() -> {
			mGLSVRenderer.onTouchEvent(event);
		});
		return true;
	}
}
