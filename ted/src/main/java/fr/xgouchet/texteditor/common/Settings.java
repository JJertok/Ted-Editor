package fr.xgouchet.texteditor.common;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;

public class Settings implements Constants {

	/** Number of recent files to remember */
	public static int MAX_RECENT_FILES = 10;

	/** Show the lines numbers */
	public static boolean SHOW_LINE_NUMBERS = true;
	/** Highlight syntax */
	public static boolean HIGHLIGHT_SYNTAX = true;
	/** automatic break line to fit one page */
	public static boolean WORDWRAP = false;
	/** color setting */
	public static int COLOR = COLOR_CLASSIC;
	/** color schem setting */
	public static int COLOR_SCHEME = COLOR_SCHEME_CLASSIC;

	/** language setting */
	public static int LANGUAGE = LANGUAGE_DEFAULT;

	/** Text size setting */
	public static int TEXT_SIZE = 12;

	/** Default end of line */
	public static int DEFAULT_END_OF_LINE = EOL_LINUX;
	/** End Of Line style */
	public static int END_OF_LINE = EOL_LINUX;
	/** Encoding */
	public static String ENCODING = ENC_UTF8;

	/** Let auto save on quit be triggered */
	public static boolean FORCE_AUTO_SAVE = false;
	public static boolean AUTO_SAVE_OVERWRITE = false;

	/** enable fling to scroll */
	public static boolean FLING_TO_SCROLL = false;

	/** Use Undo instead of quit ? */
	public static boolean UNDO = true;

	/** Use Redo ? */
	public static boolean REDO = true;

	/** Undo stack capacity */
	public static int UNDO_MAX_STACK = 25;
	/** Use back button as undo */
	public static boolean BACK_BTN_AS_UNDO = false;

	/** Use a Home Page */
	public static boolean USE_HOME_PAGE = false;
	/** Home Page Path */
	public static String HOME_PAGE_PATH = "";
	/** Cursor for Last File */
	public static int LAST_CURSOR = 0;

	/**
	 * @return the end of line characters according to the current settings
	 */
	public static String getEndOfLine() {
		switch (END_OF_LINE) {
		case EOL_MAC: // Mac OS
			return "\r";
		case EOL_WINDOWS: // Windows
			return "\r\n";
		case EOL_LINUX: // Linux / Android
		default:
			return "\n";
		}
	}

	/**
	 * Update the settings from the given {@link SharedPreferences}
	 * 
	 * @param settings
	 *            the settings to read from
	 */
	public static void updateFromPreferences(SharedPreferences settings) {

		MAX_RECENT_FILES = getStringPreferenceAsInteger(settings,
				PREFERENCE_MAX_RECENTS, "10");
		SHOW_LINE_NUMBERS = settings.getBoolean(PREFERENCE_SHOW_LINE_NUMBERS,
				true);
		HIGHLIGHT_SYNTAX = settings.getBoolean(PREFERENCE_HIGHLIGHT_SYNTAX, true);
		WORDWRAP = settings.getBoolean(PREFERENCE_WORDWRAP, false);
		TEXT_SIZE = getStringPreferenceAsInteger(settings,
				PREFERENCE_TEXT_SIZE, "12");
		DEFAULT_END_OF_LINE = getStringPreferenceAsInteger(settings,
				PREFERENCE_END_OF_LINES, ("" + EOL_LINUX));
		FORCE_AUTO_SAVE = settings.getBoolean(PREFERENCE_AUTO_SAVE, false);
		AUTO_SAVE_OVERWRITE = settings.getBoolean(
				PREFERENCE_AUTO_SAVE_OVERWRITE, false);
		COLOR = getStringPreferenceAsInteger(settings, PREFERENCE_COLOR_THEME,
				("" + COLOR_CLASSIC));
		COLOR_SCHEME = getStringPreferenceAsInteger(settings, PREFERENCE_HIGHTLIGHT_THEME,
				("" + COLOR_SCHEME_CLASSIC));
		LANGUAGE = getStringPreferenceAsInteger(settings, PREFERENCE_LANGUAGE,
				("" + LANGUAGE_DEFAULT));
		ENCODING = settings.getString(PREFERENCE_ENCODING, ENC_UTF8);
		FLING_TO_SCROLL = settings.getBoolean(PREFERENCE_FLING_TO_SCROLL, true);

		BACK_BTN_AS_UNDO = settings.getBoolean(PREFERENCE_BACK_BUTTON_AS_UNDO,
				false);
		UNDO = settings.getBoolean(PREFERENCE_ALLOW_UNDO, true);
		UNDO_MAX_STACK = getStringPreferenceAsInteger(settings,
				PREFERENCE_MAX_UNDO_STACK, "25");

		USE_HOME_PAGE = settings.getBoolean(PREFERENCE_USE_HOME_PAGE, false);
		HOME_PAGE_PATH = settings.getString(PREFERENCE_HOME_PAGE_PATH, "");
		LAST_CURSOR = settings.getInt(PREFERENCE_CURSOR, 0);

		RecentFiles.loadRecentFiles(settings.getString(PREFERENCE_RECENTS, ""));
	}

	/**
	 * Reads a preference stored as a string and returns the numeric value
	 * 
	 * @param prefs
	 *            the prefernce to read from
	 * @param key
	 *            the key
	 * @param def
	 *            the default value
	 * @return the value as an int
	 */
	protected static int getStringPreferenceAsInteger(SharedPreferences prefs,
			String key, String def) {
		String strVal;
		int intVal;

		strVal = null;
		try {
			strVal = prefs.getString(key, def);
		} catch (Exception e) {
			strVal = def;
		}

		try {
			intVal = Integer.parseInt(strVal);
		} catch (NumberFormatException e) {
			intVal = 0;
		}

		return intVal;
	}

	/**
	 * Save the Home page settings
	 * 
	 * @param settings
	 *            the settings to write to
	 */
	public static void saveHomePage(SharedPreferences settings) {
		Editor editor = settings.edit();
		editor.putString(PREFERENCE_HOME_PAGE_PATH, HOME_PAGE_PATH);
		editor.commit();

	}

	public static File getFontFile(Context ctx) {
		return new File(ctx.getDir(FONT_FOLDER_NAME, Context.MODE_PRIVATE),
				FONT_FILE_NAME);
	}

	public static Typeface getTypeface(Context ctx) {
		File fontFile = getFontFile(ctx);
		Typeface res = Typeface.MONOSPACE;
		if (fontFile.exists() && fontFile.canRead()) {
			res = Typeface.createFromFile(getFontFile(ctx));
		}
		return res;
	}

	public static String getColorSchemeName(int color_scheme_index) {
		String[] schemes = new String[] { "classicStyle", "draculaStyle" };
		return  schemes[color_scheme_index];
	}

	public static String getLanguageName(int language_index) {
		String[] schemes = new String[] { "cppTokens", "javascriptTokens", "htmlTokens" };
		return  schemes[language_index];
	}
}
