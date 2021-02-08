package cn.rrf.complexgame.dimension_2;

public class Circle implements Geometry {
	private final int precision = 100;
	private final float pi = 3.14159265358979f;
	private final float radius = 0.5f;

	private float[] vertices;
	private short[] indices;

	public Circle() {
		init();
	}


	private void init() {
		vertices = new float[precision * 3];
		indices = new short[(precision - 2) * 3];

		for (int i = 0; i < precision; i++) {
			float theta = 2 * pi * i / precision;
			float x = (float) Math.cos(theta) * radius;
			float y = (float) Math.sin(theta) * radius;
			vertices[3 * i] = x;
			vertices[3 * i + 1] = y;
			vertices[3 * i + 2] = 0;
		}

		for (int i = 0; i < precision - 2; i++) {
			indices[3 * i] = 0;
			indices[3 * i + 1] = (short) (i + 1);
			indices[3 * i + 2] = (short) (i + 2);
		}
	}

	@Override
	public float[] getVertices() {
		return vertices;
	}

	@Override
	public short[] getIndices() {
		return indices;
	}
}
