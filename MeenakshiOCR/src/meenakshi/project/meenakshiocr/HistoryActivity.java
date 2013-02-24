package meenakshi.project.meenakshiocr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HistoryActivity extends Activity {
	
	String recognizedText="";
	
	TextView tv[];
	int clicked = 0;
	
	private String TAG="HistoryActivity";
	
	private SharedPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		tv = new TextView[5];
		
		tv[0] = (TextView)findViewById(R.id.his0);
		tv[1] = (TextView)findViewById(R.id.his1);
		tv[2] = (TextView)findViewById(R.id.his2);
		tv[3] = (TextView)findViewById(R.id.his3);
		tv[4] = (TextView)findViewById(R.id.his4);
		
		
		mPreferences = getSharedPreferences("MeenakshiOCRSharedPreferences", Context.MODE_PRIVATE);
		tv[0].setText(mPreferences.getString("his0", " "));
		tv[1].setText(mPreferences.getString("his1", " "));
		tv[2].setText(mPreferences.getString("his2", " "));
		tv[3].setText(mPreferences.getString("his3", " "));
		tv[4].setText(mPreferences.getString("his4", " "));
        
        
		
		final String [] items			= new String [] {"Copy to clipboard", "Google it", "Directly send SMS", "Share", "Delete"};				
		ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder		= new AlertDialog.Builder(this);
		
		builder.setTitle("Select Action");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick( DialogInterface dialog, int item ) { //pick from camera
				if (item == 0)
					copyRTToClipBoard();
				else if(item==1)
					googleRT();
				else if(item == 2)
					sendSMS();
				else if(item == 3)
					share();
				else
					delete();
					
					
					
			}
		} );
		
		final AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		
		tv[0].setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				clicked = 0;
				recognizedText = (String) tv[0].getText();
				if(!recognizedText.equals("") && !recognizedText.equals(" "))
					dialog.show();
			}
		});
		
		tv[1].setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				clicked = 1;
				recognizedText = (String) tv[1].getText();
				if(!recognizedText.equals("") && !recognizedText.equals(" "))
					dialog.show();
			}
		});
		
		tv[2].setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				clicked = 2;
				recognizedText = (String) tv[2].getText();
				if(!recognizedText.equals("") && !recognizedText.equals(" "))
					dialog.show();
			}
		});
		
		tv[3].setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				clicked = 3;
				recognizedText = (String) tv[3].getText();
				if(!recognizedText.equals("") && !recognizedText.equals(" "))
					dialog.show();
			}
		});
		
		tv[4].setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				clicked = 4;
				recognizedText = (String) tv[4].getText();
				if(!recognizedText.equals("") && !recognizedText.equals(" "))
					dialog.show();
			}
		});
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	
	public void delete()
	{
		SharedPreferences.Editor editor = mPreferences.edit();
		
		for(int i=clicked; i<=3; i++)
		{
			tv[i].setText(tv[i+1].getText());
            editor.putString("his" + i, (String) tv[i+1].getText());	
		}
		tv[4].setText(" ");
		editor.putString("his4", " ");
		editor.commit();

	}
	
	
	
	
	public void copyRTToClipBoard()
	{
		RecognizedTextUses.copyRTToClipBoard(recognizedText, this);
	}
	
	
	public void googleRT()
	{
		RecognizedTextUses.googleRT(recognizedText, this);
	}
	
	public void share()
	{
		RecognizedTextUses.share(recognizedText, this);
	}
	
	public void sendSMS()
	{
		RecognizedTextUses.sendSMS(recognizedText, this);
	}
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_history, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

}
