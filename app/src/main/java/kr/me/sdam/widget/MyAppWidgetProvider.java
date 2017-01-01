package kr.me.sdam.widget;

import kr.me.sdam.MainActivity;
import kr.me.sdam.R;
import kr.me.sdam.autologin.SplashActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MyAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.app_widget_layout);
		view.setImageViewResource(R.id.image_widget_icon, R.drawable.a_launcher_1_icon_512x512);
		view.setTextViewText(R.id.text_widget_title, "SDAM Widget");
		Intent intent = new Intent(context, SplashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		view.setOnClickPendingIntent(R.id.gridView1, pi);
		view.setOnClickPendingIntent(R.id.image_widget_icon, pi);
		appWidgetManager.updateAppWidget(appWidgetIds, view);
	}
	
	
}
