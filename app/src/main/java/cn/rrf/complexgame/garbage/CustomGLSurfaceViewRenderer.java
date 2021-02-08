package cn.rrf.complexgame.garbage;

import android.opengl.GLES20;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.view.MotionEvent;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.rrf.complexgame.Vec;
import cn.rrf.complexgame.dimension_2.RectangleFrame;
import cn.rrf.complexgame.dimension_2.Renderer;

public class CustomGLSurfaceViewRenderer implements GLSurfaceView.Renderer {
	Square square22;
	float t;
	Renderer renderer;
	float px;
	float py;
	Square2 square2;
	private Triangle triangle;
	private Square square;
	private Square square1;
	private ArrayList<Square> squareList;
	private ArrayList<Vec> mVecArrayList;
	private float[] projectionInv;
	private float[] viewInv;
	private float[] projection;
	private float[] view;
	private int mWidth;
	private int mHeight;
	private int currentSquare;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		projection = new float[16];
		view = new float[16];
		projectionInv = new float[16];
		viewInv = new float[16];
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		mWidth = width;
		mHeight = height;

		float ratio = (float) width / height;

		Matrix.frustumM(projection, 0, -ratio, ratio, -1, 1, 3, 7);
		Matrix.setLookAtM(view, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);

		Matrix.invertM(projectionInv, 0, projection, 0);
		Matrix.invertM(viewInv, 0, view, 0);

		Vec translateVec = new Vec(0, 0, 0);
		Vec scaleVec = new Vec(1, 1, 1);

		scaleVec.setValue(1, 1, 1);
		translateVec.setValue(0, 0, 0);
		square = new Square(0, projection, view, translateVec, scaleVec);
		square2 = new Square2(-11, projection, view, new Vec(0, 0, 0), new Vec(0.8f, 0.8f, 0.8f));

		float widthAll = 1.0f;
		float widthAllInner = widthAll * 0.95f;
		float interval = 0.01f;
		float widthSmall = (widthAllInner - 4 * interval) / 3;

		scaleVec.setValue(widthSmall, widthSmall, 1);
		translateVec.setValue(0, 0, 0);
		squareList = new ArrayList<>();

		t = (widthSmall + interval);
		mVecArrayList = new ArrayList<>();
		mVecArrayList.add(new Vec(t, t, 0));
		mVecArrayList.add(new Vec(0, t, 0));
		mVecArrayList.add(new Vec(-t, t, 0));
		mVecArrayList.add(new Vec(-t, 0, 0));
		mVecArrayList.add(new Vec(-t, -t, 0));
		mVecArrayList.add(new Vec(0, -t, 0));
		mVecArrayList.add(new Vec(t, -t, 0));
		mVecArrayList.add(new Vec(t, 0, 0));
		mVecArrayList.add(new Vec(0, 0, 0));
		for (int i = 0; i < 9; i++) {
			if (i == 6) continue;
			Square square = new Square(i, projection, view, mVecArrayList.get(i), scaleVec);
			squareList.add(square);
		}

		renderer = new Renderer(16, new RectangleFrame(0.02f), projection, view, new Vec(0, 0, 0),
		                        new Vec(1, 1, 1));
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	public void onTouchEvent(int action, float x, float y) {
		y -= 325;
		if (action == MotionEvent.ACTION_UP) {
			squareList.forEach(square -> {
				square.setColors(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
			});
			square22 = null;
			currentSquare = -1;
		}
		if (action == MotionEvent.ACTION_MOVE) {
			x = 2.0f * x / mWidth - 1.0f;
			y = 1.0f - 2.0f * y / mHeight;

			float[] coordinate = new float[4];
			coordinate[0] = x;
			coordinate[1] = y;
			coordinate[2] = -1;
			coordinate[3] = 1;

			Matrix.multiplyMV(coordinate, 0, projectionInv, 0, coordinate, 0);
			Matrix.multiplyMV(coordinate, 0, viewInv, 0, coordinate, 0);

			square22 = new Square(-1, projection, view, new Vec(coordinate[0] / coordinate[3],
			                                                    coordinate[1] / coordinate[3], 0),
			                      new Vec(0.05f, 0.05f, 1));

			if (currentSquare != -1) {
				if (x > t) x = t;
				if (x < -t) x = -t;
				if (y > t) y = t;
				if (y < -t) y = -t;
				List<Double> distances = new ArrayList<>();
				for (int i = 0; i < 8; i++) {
					if (squareList.get(i).getIdentifier() != currentSquare) {
						double distance = Math.sqrt(Math.pow(squareList.get(currentSquare)
						                                               .getTranslateVec()
						                                               .getX() - squareList.get(i)
						                                                                   .getTranslateVec()
						                                                                   .getX(),
						                                     2) +
								                            Math.pow(squareList.get(currentSquare)
								                                               .getTranslateVec()
								                                               .getY() - squareList
										                            .get(i).getTranslateVec()
										                            .getY(), 2));
						if (distance < t) {
							squareList.get(currentSquare).setTranslateVec(new Vec(px, py, 0));
							return;
						}
						distances.add(distance);
					}
				}
				px = x;
				py = y;

				squareList.get(currentSquare).setTranslateVec(new Vec(x, y, 0));
			}
		}
		if (action == MotionEvent.ACTION_DOWN) {

			x = 2.0f * x / mWidth - 1.0f;
			y = 1.0f - 2.0f * y / mHeight;
			px = x;
			py = y;
			float[] coordinate = new float[4];
			coordinate[0] = x;
			coordinate[1] = y;
			coordinate[2] = -1;
			coordinate[3] = 1;

			Matrix.multiplyMV(coordinate, 0, projectionInv, 0, coordinate, 0);
			Matrix.multiplyMV(coordinate, 0, viewInv, 0, coordinate, 0);

			square22 = new Square(-1, projection, view, new Vec(coordinate[0] / coordinate[3],
			                                                    coordinate[1] / coordinate[3], 0),
			                      new Vec(0.05f, 0.05f, 1));
			squareList.forEach(square -> {
				if (square.testIn(coordinate[0] / coordinate[3], coordinate[1] / coordinate[3])) {
					square.setColors(new float[]{1.0f, 0.1f, 0.1f, 1.0f});
					currentSquare = square.getIdentifier();
				}
			});
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES32.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
		GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);

		if (false) {
			square.draw();
			squareList.forEach(square -> {
				if (square.getIdentifier() != 6) square.draw();
			});
			if (square22 != null) square22.draw();
		}
		//square2.draw();
		renderer.draw();
	}
}
