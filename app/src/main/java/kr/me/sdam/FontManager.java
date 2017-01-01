package kr.me.sdam;


import android.content.Context;
import android.graphics.Typeface;

public class FontManager {
	private static FontManager instance;
	public static FontManager getInstance() {
		if (instance == null) {
			instance = new FontManager();
		}
		return instance;
	}
	private FontManager() { }
	
	Typeface nanum;
	public static final String FONT_NAME_NANUM = "nanum";
	Typeface nanum_bold;
	public static final String FONT_NAME_NANUM_BOLD = "nanum_bold";
	
	public Typeface getTypeface(Context context, String fontName) {
		if (FONT_NAME_NANUM.equals(fontName)) {
			if (nanum == null) {
				nanum = Typeface.createFromAsset(context.getAssets(), "nanumgothic.ttf");
			}
			return nanum;
		}
		if (FONT_NAME_NANUM_BOLD.equals(fontName)) {
			if (nanum_bold == null) {
				nanum_bold = Typeface.createFromAsset(context.getAssets(), "NanumGothicBold.ttf");
			}
			return nanum_bold;
		}
		return null;
	}
}
