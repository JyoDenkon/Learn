package cn.rrf.complexgame.dimension_2;

import android.opengl.Matrix;

public class RectangleFrame implements Geometry {

	private final float widthUp;
	private final float widthDown;
	private final float widthLeft;
	private final float widthRight;

	private float[] vertices;
	private short[] indices;

	public RectangleFrame(float width) {
		this(width, width, width, width);
	}

	public RectangleFrame(float widthUp, float widthDown, float widthLeft, float widthRight) {
		this.widthUp = widthUp;
		this.widthDown = widthDown;
		this.widthLeft = widthLeft;
		this.widthRight = widthRight;
		init();
	}

	public float getWidthUp() {
		return widthUp;
	}

	public float getWidthDown() {
		return widthDown;
	}

	public float getWidthLeft() {
		return widthLeft;
	}

	public float getWidthRight() {
		return widthRight;
	}

	public float[] getVertices() {
		return vertices;
	}

	public short[] getIndices() {
		return indices;
	}

	private void init() {
		//vertices initialization
		vertices = new float[24];
		vertices[0] = 0.5f;
		vertices[1] = 0.5f;
		vertices[2] = 0.0f;
		vertices[3] = 1.0f;

		float[] rotate = new float[16];
		Matrix.setRotateM(rotate, 0, 90, 0, 0, 1);
		for (int i = 0; i < 3; i++) {
			Matrix.multiplyMV(vertices, 3 * i + 3, rotate, 0, vertices, 3 * i);
		}

		for (int i = 0; i < 4; i++) {
			float t;
			vertices[3 * i + 12] = (t = vertices[3 * i]) > 0 ? t - widthRight : t + widthLeft;
			vertices[3 * i + 13] = (t = vertices[3 * i + 1]) > 0 ? t - widthUp : t + widthDown;
			vertices[3 * i + 14] = vertices[3 * i + 2];
		}

		//indices initialization
		indices = new short[24];
		for (int i = 0; i < 4; i++) {
			indices[6 * i] = (short) i;
			indices[6 * i + 1] = (short) ((i + 1) % 4);
			indices[6 * i + 2] = (short) ((i + 5) % 4 + 4);
			indices[6 * i + 3] = (short) i;
			indices[6 * i + 4] = (short) (i + 4);
			indices[6 * i + 5] = (short) ((i + 5) % 4 + 4);
		}
	}
}
