/**
 * 
 */
package meenakshi.project.meenakshiocr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author ALIEN
 *
 */
public class RecognizedTextUses {
	
	private static String TAG="RecognizedTextUses";
	static int SEND_SMS = 4;
	static String regexStr = "^[0-9]*$";
	
	public static void copyRTToClipBoard(String recognizedText, Activity act)
	{
		Log.v(TAG, "In clipboard button call");
		ClipboardManager clipboard = (ClipboardManager)act.getSystemService(act.CLIPBOARD_SERVICE); 
		clipboard.setText(recognizedText); 
		Toast.makeText(act.getApplicationContext(), "Text copied to clipboard!", Toast.LENGTH_SHORT).show();
	}
	
	
	public static void googleRT(String recognizedText, Activity act)
	{
		//AlertDialog.Builder builder = new AlterDialog.Builder(this);
		Log.v(TAG, "In google button call");
		Uri uriUrl = Uri.parse("http://www.google.com/search?q=" + recognizedText); 
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);  
		act.startActivity(launchBrowser); 
	}
	
	
	
	public static void share(String recognizedText, Activity act)
	{
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, recognizedText);
		//sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
		act.startActivity(Intent.createChooser(sharingIntent, "Share using"));
	}
	
	
	
	public static void sendSMS(final String recognizedText, final Activity act)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(act);

		alert.setTitle("Send text as SMS");
		alert.setMessage("Enter Phone Number of SMS receiver");

		// Set an EditText view to get user input 
		final EditText input = new EditText(act);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String value = input.getText().toString();
		  // Do something with value!
		  if (value.length()>0 && recognizedText.length()>0 && value.matches(regexStr))                
              sendSMS(value, recognizedText, act);
		else
			Toast.makeText(act.getApplicationContext(), "Invalid input. SMS not sent!", Toast.LENGTH_SHORT).show();
		}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}
	
	
   /* private void sendSMS(String phoneNumber, String message)
    {        
        Log.v("phoneNumber",phoneNumber);
        Log.v("MEssage",message);
        PendingIntent pi = PendingIntent.getActivity(this, SEND_SMS,
                new Intent(this, OCRActivity.class), 0);                
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, pi, null);        
    }  */
	
	
    //---sends an SMS message to another device---
    private static void sendSMS(String phoneNumber, String message, final Activity act)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
 
        PendingIntent sentPI = PendingIntent.getBroadcast(act, SEND_SMS,
            new Intent(SENT), 0);
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(act, SEND_SMS,
            new Intent(DELIVERED), 0);
 
        //---when the SMS has been sent---
        act.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(act.getBaseContext(), "SMS sent", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(act.getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(act.getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(act.getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(act.getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        act.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(act.getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(act.getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);        
    }
	

}
