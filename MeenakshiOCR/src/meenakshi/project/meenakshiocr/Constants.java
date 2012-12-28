package meenakshi.project.meenakshiocr;

import android.content.Context;
import android.content.SharedPreferences;

public class Constants {
	
	public static String DATA_PATH;
	public static String CURRENT_IMAGE_PATH;
	public static String LANG = "eng";
	public static String OCRTextMode;
	
	//public static String PREVIOUS_IMAGE_PATH;
	
	public static void initializeConstants(MainActivity act)
	{
		SharedPreferences p = act.getSharedPreferences("MeenakshiOCRSharedPreferences", Context.MODE_PRIVATE);
		DATA_PATH = p.getString("DATA_PATH", null);
		LANG = p.getString("lang", null);
		OCRTextMode = p.getString("OCRTextMode", null);
		CURRENT_IMAGE_PATH = p.getString("CURRENT_IMAGE_PATH", null);
	}

}
