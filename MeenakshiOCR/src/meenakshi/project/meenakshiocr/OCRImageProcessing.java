package meenakshi.project.meenakshiocr;

import android.graphics.Bitmap;
import android.graphics.Color;

public class OCRImageProcessing {
	
	public static Bitmap makeGreyScale(Bitmap src){
			// constant factors
			final double GS_RED = 0.299;
			final double GS_GREEN = 0.587;
			final double GS_BLUE = 0.114;

			// create output bitmap
			Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
			// pixel information
			int A, R, G, B;
			int pixel;

			// get image size
			int width = src.getWidth();
			int height = src.getHeight();

			// scan through every single pixel
			for(int x = 0; x < width; ++x) {
				for(int y = 0; y < height; ++y) {
					// get one pixel color
					pixel = src.getPixel(x, y);
					// retrieve color of all channels
					A = Color.alpha(pixel);
					R = Color.red(pixel);
					G = Color.green(pixel);
					B = Color.blue(pixel);
					// take conversion up to one single value
					R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
					// set new pixel color to output bitmap
					bmOut.setPixel(x, y, Color.argb(A, R, G, B));
				}
			}

			// return final image
			return bmOut;
		}
	
	
	public static Bitmap createContrastBW(Bitmap src, double value) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;
		// get contrast value
		double contrast = Math.pow((100 + value) / 100, 2);

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				// apply filter contrast for every channel R, G, B
				R = Color.red(pixel);
				R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(R < 0) { R = 0; }
				else if(R > 255) { R = 255; }

				G = Color.red(pixel);
				G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(G < 0) { G = 0; }
				else if(G > 255) { G = 255; }

				B = Color.red(pixel);
				B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(B < 0) { B = 0; }
				else if(B > 255) { B = 255; }

				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}


}
