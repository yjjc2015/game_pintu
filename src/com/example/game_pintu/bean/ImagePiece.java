package com.example.game_pintu.bean;

import android.graphics.Bitmap;

/**
 * @author Administrator
 * ͼƬ��Ƭ
 */
public class ImagePiece {
	private int index;
	private Bitmap bitmap;
	public ImagePiece() {
	}
	public ImagePiece(int index, Bitmap bitmap) {
		this.index = index;
		this.bitmap = bitmap;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
