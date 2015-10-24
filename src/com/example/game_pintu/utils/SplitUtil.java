package com.example.game_pintu.utils;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.example.game_pintu.bean.ImagePiece;

public class SplitUtil {
	public static List<ImagePiece> splitImage (Bitmap bitmap, int piece) {
		List<ImagePiece> result = new ArrayList<ImagePiece>();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int perWidth = width / piece;
		int perHeight = height / piece;
		for (int i = 0; i < piece; i++) {
			for (int j = 0; j < piece; j++) {
				ImagePiece imagePiece = new ImagePiece();
				imagePiece.setIndex(i * piece + j);
				int x = j * perWidth;
				int y = i * perHeight;
				Bitmap bp = Bitmap.createBitmap(bitmap, x, y, perWidth, perHeight);
				imagePiece.setBitmap(bp);
				result.add(imagePiece);
			}
		}
		return result;
	}
}
