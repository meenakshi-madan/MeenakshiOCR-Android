package meenakshi.project.meenakshiocr;

import android.graphics.Bitmap;
import android.graphics.Color;
import meenakshi.project.meenakshiocr.ConvolutionMatrix;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class OCRImageProcessing {
	
	//for unsharp mask
	final static int KERNAL_WIDTH = 3;
	final static int KERNAL_HEIGHT = 3;
	final static int DIV_BY_9 = 9;
	//end of for unsharp mask
	 
	
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
	
	public static Bitmap increaseDPI(Bitmap bitmap, int w, int h)
	{
		bitmap = Bitmap.createScaledBitmap(bitmap, 2*w, 2*h, false);
		return bitmap;
	}
	
	public static Bitmap applyGaussianBlur(Bitmap src) {

	    double[][] GaussianBlurConfig = new double[][] { { 1, 2, 1 }, { 2, 4, 2 }, { 1, 2, 1 } };
	    ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
	    convMatrix.applyConfig(GaussianBlurConfig);
	    convMatrix.Factor = 16;
	    convMatrix.Offset = 0;
	    return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}
	
	public static Bitmap unsharpMask(Bitmap bitmap_Source)
	{

		 int[][] kernal_blur = {
		 {1, 1, 1},
		 {1, 1, 1},
		 {1, 1, 1}
		 };
		 
		 //Handler handler;
		 Bitmap afterProcess;
		 //handler = new Handler();
		 afterProcess = processingBitmap(bitmap_Source, kernal_blur);
		 afterProcess = processingBitmap(afterProcess, kernal_blur);
		 afterProcess = processingBitmap(afterProcess, kernal_blur);
		 
		 return afterProcess;
	}
	
	private static Bitmap processingBitmap(Bitmap src, int[][] knl){
		 Bitmap dest = Bitmap.createBitmap(
		 src.getWidth(), src.getHeight(), src.getConfig());
		 
		 int bmWidth = src.getWidth();
		 int bmHeight = src.getHeight();
		 int bmWidth_MINUS_2 = bmWidth - 2;
		 int bmHeight_MINUS_2 = bmHeight - 2;
		 int bmWidth_OFFSET_1 = 1;
		 int bmHeight_OFFSET_1 = 1;
		 
		 for(int i = bmWidth_OFFSET_1; i <= bmWidth_MINUS_2; i++){
			 for(int j = bmHeight_OFFSET_1; j <= bmHeight_MINUS_2; j++){
		 
				 //get the surround 7*7 pixel of current src[i][j] into a matrix subSrc[][]
				 int[][] subSrc = new int[KERNAL_WIDTH][KERNAL_HEIGHT];
				 for(int k = 0; k < KERNAL_WIDTH; k++){
					 for(int l = 0; l < KERNAL_HEIGHT; l++){
						 subSrc[k][l] = src.getPixel(i-bmWidth_OFFSET_1+k, j-bmHeight_OFFSET_1+l);
					 }
				 }

				 //subSum = subSrc[][] * knl[][]
				 long subSumA = 0;
				 long subSumR = 0;
				 long subSumG = 0;
				 long subSumB = 0;

				 for(int k = 0; k < KERNAL_WIDTH; k++){
					 for(int l = 0; l < KERNAL_HEIGHT; l++){
						 subSumA += (long)(Color.alpha(subSrc[k][l])) * (long)(knl[k][l]);
						 subSumR += (long)(Color.red(subSrc[k][l])) * (long)(knl[k][l]);
						 subSumG += (long)(Color.green(subSrc[k][l])) * (long)(knl[k][l]);
						 subSumB += (long)(Color.blue(subSrc[k][l])) * (long)(knl[k][l]);
					 }
				 }

				 subSumA = subSumA/DIV_BY_9;
				 subSumR = subSumR/DIV_BY_9;
				 subSumG = subSumG/DIV_BY_9;
				 subSumB = subSumB/DIV_BY_9;

				 int orgColor = src.getPixel(i, j);
				 int orgA = Color.alpha(orgColor);
				 int orgR = Color.red(orgColor);
				 int orgG = Color.green(orgColor);
				 int orgB = Color.blue(orgColor);

				 subSumA = orgA + (orgA - subSumA);
				 subSumR = orgR + (orgR - subSumR);
				 subSumG = orgG + (orgG - subSumG);
				 subSumB = orgB + (orgB - subSumB);

				 if(subSumA <0){
					 subSumA = 0;
				 }else if(subSumA > 255){
					 subSumA = 255; 
				 }

				 if(subSumR <0){
					 subSumR = 0;
				 }else if(subSumR > 255){
					 subSumR = 255; 
				 }

				 if(subSumG <0){
					 subSumG = 0;
				 }else if(subSumG > 255){
					 subSumG = 255;
				 }

				 if(subSumB <0){
					 subSumB = 0;
				 }else if(subSumB > 255){
					 subSumB = 255;
				 }

				 dest.setPixel(i, j, Color.argb(
						 (int)subSumA, 
						 (int)subSumR, 
						 (int)subSumG, 
						 (int)subSumB));
			 } 
		 }
		 
		 return dest;
	}

}
