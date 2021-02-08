package cn.rrf.complexgame;

public class Vec {
	public float x;
	public float y;
	public float z;
	public float w;

	public Vec(float x, float y, float z) {
		this(x, y, z, 1);
	}

	public Vec(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = z;
	}

	public Vec() {
		this(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	public void setValue(float x, float y, float z) {
		setValue(x, y, z, this.w);
	}

	public void setValue(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
}
