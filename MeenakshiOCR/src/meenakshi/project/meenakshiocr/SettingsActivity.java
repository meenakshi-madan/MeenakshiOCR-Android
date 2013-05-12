package meenakshi.project.meenakshiocr;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(
                MainActivity.PREFS_NAME);
        addPreferencesFromResource(R.xml.prefs);
    }

}
