package meenakshi.project.meenakshiocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;



public class CopyDataToSDAsync extends AsyncTask<Void, Void, Void> {
	
	Activity act;
	boolean allDone = true;
	String TAG = "CopyDataToSDAsync";
	ProgressDialog pg;
	
	
	CopyDataToSDAsync(Activity a)
	{
		act = a;
	}
	
	@Override
    protected void onPostExecute(Void result) {
        //mImageView.setImageBitmap(result);
		Log.v("CopData AsyncTask Mein", "Entered onPostExecute");
		
		//pg.
		
		if ( allDone )
		{
			pg.setMessage("All files have been successfully downloaded!");
			pg.dismiss();
		}
		else
		{
			pg.setMessage("Download unsuccessful! Try re-downloading from Settings or re-installing the application.");
			pg.setCanceledOnTouchOutside(true);
		}
		//pg.setCancelable(true);
		//pg.dismiss();
    }
	
	
	@Override
    protected void onPreExecute() {
        //mImageView.setImageBitmap(result);
		Log.v("CopData AsyncTask Mein", "Entered onPreExecute");
		
		pg = new ProgressDialog(act);
		pg.setTitle("Downloading. . .");
		pg.setMessage("This will take just a moment!");
		//pg.setButton(ProgressDialog.BUTTON_NEUTRAL, text, listener)
		//pg.setButton(ProgressDialog.BUTTON_NEUTRAL, "End", "Closing Dialog");
		pg.show();
    }
	

	@Override
	protected Void doInBackground(Void... arg0) {
		// TODO Auto-generated method stub


		//SPECIFIC TO OCR
		String[] paths = new String[] { Constants.DATA_PATH, Constants.DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					allDone = false;
					return null;
					//Toast.makeText(getApplicationContext(), "Couldn't create required directories. OCR will not work.", Toast.LENGTH_LONG).show();
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		Log.v(TAG, "End of creating directories for loop");

		// act.LANG.traineddata file with the app (in assets folder)
		// This area needs work and optimization
		
		AssetManager assetManager = act.getAssets();
		InputStream in;
		OutputStream out;
		byte[] buf;
		int len;
		try {
			in = assetManager.open("tessdata/" + Constants.LANG + ".traineddata");
			//GZIPInputStream gin = new GZIPInputStream(in);
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + Constants.LANG + ".traineddata");

			// Transfer bytes from in to out
			buf = new byte[1024];
			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + Constants.LANG + " traineddata");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + Constants.LANG + " traineddata " + e.toString());
			allDone=false;
			return null;
		}

		try {


			in = assetManager.open("tessdata/" + "equ" + ".traineddata");
			//GZIPInputStream gin = new GZIPInputStream(in);
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + "equ" + ".traineddata");

			// Transfer bytes from in to out
			buf = new byte[1024];

			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + "equ" + " traineddata");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + "equ" + " traineddata " + e.toString());
			allDone=false;
			return null;
		}

		try {


			in = assetManager.open("tessdata/" + "osd" + ".traineddata");
			//GZIPInputStream gin = new GZIPInputStream(in);
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + "osd" + ".traineddata");

			// Transfer bytes from in to out
			buf = new byte[1024];

			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + "osd" + " traineddata");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + "osd" + " traineddata " + e.toString());
			allDone=false;
			return null;
		}

		try {


			in = assetManager.open("tessdata/" + Constants.LANG + ".cube.bigrams");
			//GZIPInputStream gin = new GZIPInputStream(in);1
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + Constants.LANG + ".cube.bigrams");

			// Transfer bytes from in to out
			buf = new byte[1024];

			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + Constants.LANG + " cube.bigrams");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + Constants.LANG + " cube.bigrams " + e.toString());
			allDone=false;
			return null;
		}

		try {


			in = assetManager.open("tessdata/" + Constants.LANG + ".cube.fold");
			//GZIPInputStream gin = new GZIPInputStream(in);1
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + Constants.LANG + ".cube.fold");

			// Transfer bytes from in to out
			buf = new byte[1024];

			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + Constants.LANG + " cube.fold");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + Constants.LANG + " cube.fold " + e.toString());
			allDone=false;
			return null;
		}

		try {


			in = assetManager.open("tessdata/" + Constants.LANG + ".cube.lm");
			//GZIPInputStream gin = new GZIPInputStream(in);1
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + Constants.LANG + ".cube.lm");

			// Transfer bytes from in to out
			buf = new byte[1024];

			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + Constants.LANG + " cube.lm");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + Constants.LANG + " cube.lm " + e.toString());
			allDone=false;
			return null;
		}

		try {


			in = assetManager.open("tessdata/" + Constants.LANG + ".cube.lm_");
			//GZIPInputStream gin = new GZIPInputStream(in);1
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + Constants.LANG + ".cube.lm_");

			// Transfer bytes from in to out
			buf = new byte[1024];

			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + Constants.LANG + " cube.lm_");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + Constants.LANG + " cube.lm_ " + e.toString());
			allDone = false; return null;
		}

		try {


			in = assetManager.open("tessdata/" + Constants.LANG + ".cube.nn");
			//GZIPInputStream gin = new GZIPInputStream(in);1
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + Constants.LANG + ".cube.nn");

			// Transfer bytes from in to out
			buf = new byte[1024];

			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + Constants.LANG + " cube.nn");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + Constants.LANG + " cube.nn " + e.toString());
			allDone = false; return null;
		}

		try {


			in = assetManager.open("tessdata/" + Constants.LANG + ".cube.params");
			//GZIPInputStream gin = new GZIPInputStream(in);1
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + Constants.LANG + ".cube.params");

			// Transfer bytes from in to out
			buf = new byte[1024];

			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + Constants.LANG + " cube.params");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + Constants.LANG + " cube.params " + e.toString());
			allDone = false; return null;
		}

		try {


			in = assetManager.open("tessdata/" + Constants.LANG + ".cube.size");
			//GZIPInputStream gin = new GZIPInputStream(in);1
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + Constants.LANG + ".cube.size");

			// Transfer bytes from in to out
			buf = new byte[1024];

			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + Constants.LANG + " cube.size");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + Constants.LANG + " cube.size " + e.toString());
			allDone = false; return null;
		}

		try {


			in = assetManager.open("tessdata/" + Constants.LANG + ".cube.word-freq");
			//GZIPInputStream gin = new GZIPInputStream(in);1
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + Constants.LANG + ".cube.word-freq");

			// Transfer bytes from in to out
			buf = new byte[1024];

			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + Constants.LANG + " cube.word-freq");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + Constants.LANG + " cube.word-freq " + e.toString());
			allDone = false; return null;
		}

		try {


			in = assetManager.open("tessdata/" + Constants.LANG + ".tesseract_cube.nn");
			//GZIPInputStream gin = new GZIPInputStream(in);1
			out = new FileOutputStream(Constants.DATA_PATH
					+ "tessdata/" + Constants.LANG + "tesseract_cube.nn");

			// Transfer bytes from in to out
			buf = new byte[1024];

			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();

			Log.v(TAG, "Copied " + Constants.LANG + " tesseract_cube.nn");
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + Constants.LANG + " tesseract_cube.nn " + e.toString());
			allDone = false; 
			return null;
		}


		Log.v(TAG, "End of copying traineddata if block");
		//END OF SPECIFIC TO OCR

		return null;
	}

}
