package kr.me.sdam;

import kr.me.sdam.alarm.AlarmData;
import kr.me.sdam.alarm.AlarmDataManager;
import kr.me.sdam.autologin.SplashActivity;
import kr.me.sdam.database.DBManager;
import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	
	public static final String ACTION_NOTIFICATION_MESSAGE = "notification";
	
	private static final String TAG="GcmIntengService";
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
//                sendNotification("Deleted messages on server: " +
//                        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging. MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
//            	String time = intent.getStringExtra("time");
            	String num = intent.getStringExtra("num");
            	String pushCase = intent.getStringExtra("pushCase");
            	sendNotification("from Server: " + num +" ,  "+pushCase,
            			Integer.parseInt(num),Integer.parseInt(pushCase));
//                sendNotification("Received: " + extras.toString());
//                ===================
//                sendOrderedBroadcast(event, null, new BroadcastReceiver() {
//                    
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                       int code = getResultCode();
//                       if (code == Activity.RESULT_CANCELED) {
//                          //못받았을때
////                          sendNotification(messageData, memberName, roomNo, profilThumbnail, memberTo);
//                    	   sendNotification("Received: " + extras.toString());
//                       }
//                       
//                    }
//                 }, null, Activity.RESULT_CANCELED, null, null);
//              ===================
                
                
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg,int num, int pushCase) {
    	String message="push message";
    	switch(pushCase){
    	case 0:
    		message = "Invalid Message..";
    	case 1:
    		message = "누군가 나의 담을 좋아합니다.";
    		break;
    	case 2:
    		message = "누군가 내 댓글을 좋아합니다.";
    		break;
    	case 3:
    		message = "누군가 나의 담에 댓글을 남겼습니다.";
    		break;
    	case 4:
    		message = "누군가 당신에게 이야기합니다.";
    		break;
    	case 5:
    		message = "공지사항이 있습니다.";
    		break;
    	}
//    	1=좋아요 2=댓글좋아요 3=댓글을남겼습니다 4=태그
    	//디비에 저장
    	DBManager.getInstance().addPushData(new AlarmData(pushCase, num));
    	
    	if(PropertyManager.getInstance().getAlarm()==1){
    		mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
    		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        
    		NotificationCompat.Builder mBuilder =
        		new NotificationCompat.Builder(this)
    		.setSmallIcon(R.drawable.a_launcher_1_icon_notification)
    		.setContentTitle("쓰담")
    		.setStyle(new NotificationCompat.BigTextStyle()
    		.bigText(message))
    		.setContentText(message)
    		.setWhen(System.currentTimeMillis());
    		
    		mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        	mBuilder.setAutoCancel(true);
        	mBuilder.setContentIntent(contentIntent);
        	mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    	}//if
    }
}

