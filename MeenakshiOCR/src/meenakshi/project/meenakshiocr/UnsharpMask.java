package meenakshi.project.meenakshiocr;

import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class UnsharpMask {
 
	final static int KERNAL_WIDTH = 3;
	final static int KERNAL_HEIGHT = 3;

	int[][] kernal_blur = {
			{1, 1, 1},
			{1, 1, 1},
			{1, 1, 1}
	};

	final static int DIV_BY_9 = 9;

	ImageView imageAfter; // ,imageSource
	Bitmap bitmap_Source;
	ProgressBar progressBar;

	private Handler handler;
	Bitmap afterProcess;
	String TAG = "UnsharpMask";
	
	 String _path;

	public UnsharpMask(Activity act, Bitmap bitmap, String path) {
		Log.v(TAG, "Begin constructor");
		//imageSource = (ImageView)act.findViewById(R.id.imageSource);
		imageAfter = (ImageView)act.findViewById(R.id.iv_photo);
		progressBar = (ProgressBar)act.findViewById(R.id.progressBar1);
		//progressBar = new ProgressBar(act, null, android.R.attr.progressBarStyleSmall);
		//progressBar.setIndeterminate(true);
		//progressBar.set
		//progressBar.setVisibility(View.VISIBLE);

		bitmap_Source = bitmap; //BitmapFactory.decodeResource(getResources(), R.drawable.testpicture);
		_path = path;

		handler = new Handler();
		StratBackgroundProcess();
	}

	private void StratBackgroundProcess(){

		Runnable runnable = new Runnable(){

			@Override
			public void run() {
				Log.v(TAG, "In runnable thread, before processing");
				int w = bitmap_Source.getWidth(), h = bitmap_Source.getHeight();
				if(w<300 || h<300)
					afterProcess = OCRImageProcessing.increaseDPI(bitmap_Source,w,h);
				//afterProcess = OCRImageProcessing.createContrastBW(afterProcess, 50);
				afterProcess = OCRImageProcessing.makeGreyScale(afterProcess);
				
				afterProcess = OCRImageProcessing.applyGaussianBlur(afterProcess);
				afterProcess = OCRImageProcessing.applyGaussianBlur(afterProcess);
				//afterProcess = OCRImageProcessing.applyGaussianBlur(afterProcess);

				afterProcess = processingBitmap(afterProcess, kernal_blur);
				afterProcess = processingBitmap(afterProcess, kernal_blur);
				afterProcess = processingBitmap(afterProcess, kernal_blur);
				
				afterProcess = OCRImageProcessing.applyGaussianBlur(afterProcess);
				
				//afterProcess = OCRImageProcessing.createContrastBW(afterProcess, 80);
				
				Log.v(TAG, "In runnable thread, after processing");
				
				handler.post(new Runnable(){

					@Override
					public void run() {
						
						//imageAfter.setImageBitmap(afterProcess);
						Log.v(TAG, "In inner runnable thread, after updating image view");
						
						try{	
							FileOutputStream out = new FileOutputStream(_path);
					        afterProcess.compress(Bitmap.CompressFormat.JPEG, 100, out);
						}catch(Exception e)
						{
							Log.v(TAG, e.toString());
						}
						
						String text = MainActivity.performOCR();
						progressBar.setVisibility(View.GONE);
					}

				});
			}
		};
		new Thread(runnable).start();
	}

	private Bitmap processingBitmap(Bitmap src, int[][] knl){
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
	
	
	private Bitmap processingBitmapBW(Bitmap src, int[][] knl){
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
						//subSumR += (long)(Color.red(subSrc[k][l])) * (long)(knl[k][l]);
						//subSumG += (long)(Color.green(subSrc[k][l])) * (long)(knl[k][l]);
						//subSumB += (long)(Color.blue(subSrc[k][l])) * (long)(knl[k][l]);
					}
				}

				subSumA = subSumA/DIV_BY_9;
				//subSumR = subSumR/DIV_BY_9;
				//subSumG = subSumG/DIV_BY_9;
				//subSumB = subSumB/DIV_BY_9;

				int orgColor = src.getPixel(i, j);
				int orgA = Color.alpha(orgColor);
				//int orgR = Color.red(orgColor);
				//int orgG = Color.green(orgColor);
				//int orgB = Color.blue(orgColor);

				subSumA = orgA + (orgA - subSumA);
				//subSumR = orgR + (orgR - subSumR);
				//subSumG = orgG + (orgG - subSumG);
				//subSumB = orgB + (orgB - subSumB);

				if(subSumA <0){
					subSumA = 0;
				}else if(subSumA > 255){
					subSumA = 255; 
				}

				/*(if(subSumR <0){
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
				}*/

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