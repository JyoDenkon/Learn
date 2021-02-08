package cn.rrf.complexgame;

import android.opengl.GLES20;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.rrf.complexgame.dimension_2.Renderer;

public class GLSVRenderer implements GLSurfaceView.Renderer {

	float previousX;
	float previousY;
	private Renderer mRender_0;
	private Renderer mCursor;
	private boolean mCursorShouldRender;
	private float[] projection;
	private float[] view;
	private float[] projectionInv;
	private float[] viewInv;
	private int mWidth;
	private int mHeight;
	private float mLineWidthOuter;
	private float mLineWidthInner;
	private float mOutlineSizeInner;
	private float mBlankWidth;
	private float[] mPositionList;
	private List<Renderer> mRendererList;
	private Queue<Renderer> mRendererQueue;
	private int selected = -1;
	private float px;
	private float py;
	private boolean initialized = false;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		projection = new float[16];
		view = new float[16];
		projectionInv = new float[16];
		viewInv = new float[16];

		mPositionList = new float[27];

		mRendererList = new ArrayList<>();
		mRender_0 = new Renderer();
		mCursor = new Renderer();
		mCursorShouldRender = false;

		//Global Settings
		mLineWidthOuter = 0.02f;
		mLineWidthInner = 0.02f;//This will be scaled
		mBlankWidth = 0.02f;
		mOutlineSizeInner = (1.0f - 2 * mLineWidthOuter - 4 * mBlankWidth) / 3;

		genPositionList(mOutlineSizeInner + mBlankWidth);

		for (int i = 0; i < 8; i++) {
			mRendererList.add(new Renderer());
		}
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

		mRender_0.setProjectionMatrix(projection).setViewMatrix(view);
		for (Renderer renderer : mRendererList) {
			renderer.setProjectionMatrix(projection).setViewMatrix(view);
		}
	}

	private void genPositionList(float f) {
		mPositionList[0] = f;
		mPositionList[1] = f;
		mPositionList[2] = 0.0f;

		mPositionList[3] = 0.0f;
		mPositionList[4] = f;
		mPositionList[5] = 0.0f;

		float[] rotate = new float[16];
		Matrix.setRotateM(rotate, 0, 90, 0, 0, 1);
		for (int i = 0; i < 3; i++) {
			Matrix.multiplyMV(mPositionList, 6 * i + 6, rotate, 0, mPositionList, 6 * i);
			Matrix.multiplyMV(mPositionList, 6 * i + 9, rotate, 0, mPositionList, 6 * i + 3);
		}

		mPositionList[24] = 0.0f;
		mPositionList[25] = 0.0f;
		mPositionList[26] = 0.0f;
	}

	private void permute(List<Float> floats, int k) {
		for (int i = k; i < floats.size(); i++) {
			Collections.swap(floats, i, k);
			permute(floats, k + 1);
			Collections.swap(floats, k, i);
		}
		if (k == floats.size() - 1) {
			return;
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES32.glClearColor(Settings.GL_CLEAR_COLOR[0], Settings.GL_CLEAR_COLOR[1],
		                    Settings.GL_CLEAR_COLOR[2], Settings.GL_CLEAR_COLOR[3]);
		GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);

		mRender_0.draw();

		for (Renderer renderer : mRendererList) {
			renderer.draw();
		}
		if (mCursorShouldRender) mCursor.draw();
	}

	public void onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();

		Vec worldPos = convertScreenToWorld(x, y);
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				renderCursor(worldPos, true);

				for (Renderer renderer : mRendererList) {
					if (renderer.testIn(worldPos.getX(), worldPos.getY())) {
						selected = renderer.getIdentifier();
						renderer.setColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
						break;
					}
				}

				if (selected != -1) {
					px = mRendererList.get(selected).getPositionVector().getX();
					py = mRendererList.get(selected).getPositionVector().getY();
				}

				previousX = worldPos.getX();
				previousY = worldPos.getY();
				break;

			case MotionEvent.ACTION_UP:
				renderCursor(worldPos, false);

				if (selected != -1) {
					mRendererList.get(selected).setColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
				}
				selected = -1;

				break;

			case MotionEvent.ACTION_MOVE:
				renderCursor(worldPos, true);

				if (selected != -1) {
					float dx = worldPos.getX() - previousX;
					float dy = worldPos.getY() - previousY;
					previousX = worldPos.getX();
					previousY = worldPos.getY();

					px = mRendererList.get(selected).getPositionVector().getX();
					py = mRendererList.get(selected).getPositionVector().getY();
					Vec positionAfter = new Vec(mRendererList.get(selected).getPositionVector()
					                                         .getX() + dx, mRendererList.get(
							selected).getPositionVector().getY() + dy, 0);

					Vec vec = regulateCoordinates(positionAfter);
					mRendererList.get(selected).setPositionVector(vec);
				}
		}
	}

	public Vec convertScreenToWorld(float x, float y) {
		y -= 325;
		x = 2.0f * x / mWidth - 1.0f;
		y = 1.0f - 2.0f * y / mHeight;
		float[] f = {x, y, -1.0f, 1.0f};
		Matrix.multiplyMV(f, 0, projectionInv, 0, f, 0);
		Matrix.multiplyMV(f, 0, viewInv, 0, f, 0);
		f[0] /= f[3];
		f[1] /= f[3];
		f[2] /= f[3];
		return new Vec(f[0], f[1], f[2]);
	}

	private Vec regulateCoordinates(Vec vec) {
		float x = vec.getX();
		float y = vec.getY();
		float innerCircleRadius = mOutlineSizeInner + mBlankWidth;
		float outerCircleRadius = (float) (innerCircleRadius * Math.sqrt(2));

		List<Float> values = new ArrayList<>();
		values.add(0.0f);
		values.add(innerCircleRadius);
		values.add(-innerCircleRadius);

		if (x >= innerCircleRadius || x <= -innerCircleRadius)
			x = x >= 0 ? innerCircleRadius : -innerCircleRadius;
		if (y >= innerCircleRadius || y <= -innerCircleRadius)
			y = y >= 0 ? innerCircleRadius : -innerCircleRadius;

		if (!((values.contains(
				x)) && (y <= innerCircleRadius && y >= -innerCircleRadius)) || ((values.contains(
				y)) && (x <= innerCircleRadius && x >= -innerCircleRadius))) {
			List<Float> list = new ArrayList<>();
			list.add(Math.abs(x - 0));
			list.add(Math.abs(x - innerCircleRadius));
			list.add(Math.abs(x + innerCircleRadius));
			list.add(Math.abs(y - 0));
			list.add(Math.abs(y - innerCircleRadius));
			list.add(Math.abs(y + innerCircleRadius));

			float r = Collections.min(list);
			int index = list.indexOf(r);
			if (index != -1) {
				if (index < 3) x = values.get(index);
				else y = values.get(index % 3);
			}
		}

		if (testCollision(x, y, innerCircleRadius, outerCircleRadius)) {
			return new Vec(px, py, 0);
		}

		return new Vec(x, y, 0);
	}

	private int round(float f) {
		return (int) (f * 1000);
	}

	private void renderCursor(Vec pos, boolean should) {
		mCursor.setPositionVector(pos);
		mCursorShouldRender = should;
	}

	private boolean testCollision(float x, float y, float innerCircleRadius,
	                              float outerCircleRadius) {
		List<Float> distances = new ArrayList<>();
		for (int i = 0; i < mRendererList.size(); i++) {
			if (mRendererList.get(i).getIdentifier() != selected) {
				float dist_x = x - mRendererList.get(i).getPositionVector().getX();
				float dist_y = y - mRendererList.get(i).getPositionVector().getY();
				float distance = (float) Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2));
				if (distance < innerCircleRadius - 0.001f) {
					Log.i("Render ", "Collision " + i);
					return true;
				}
				distances.add(distance);
			} else distances.add(1.0f);
		}
		float min = Collections.min(distances);
		int index = distances.indexOf(min);
		if (min > outerCircleRadius) return false;

		float[] vertices = mRendererList.get(index).getGeometry().getVertices();
		Renderer renderer = mRendererList.get(index);
		float sx = renderer.getScaleVector().getX();
		float sy = renderer.getScaleVector().getY();
		float px = renderer.getPositionVector().getX();
		float py = renderer.getPositionVector().getY();
		for (int i = 0; i < vertices.length / 3; i++) {
			if (!mRendererList.get(selected).testIn(vertices[3 * i] * sx + px,
			                                        vertices[3 * i + 1] * sy + py)) {
				continue;
			}
			Log.i("Render", "Fucked" + index);
			return true;
		}
		return false;
	}
}
