class DialogFontLibrary
{
	private static var fontInfo:Map<String, DialogFontInfo>;
	private static var fonts:Map<String, DialogFont>;
	private static var defaultFont:DialogFont;

	private function new()
	{

	}

	public static function getFontInfo(name:String):DialogFontInfo
	{
		if(fontInfo == null)
			fontInfo = new Map<String, DialogFontInfo>();

		if(!fontInfo.exists(name))
			fontInfo.set(name, new DialogFontInfo(name));

		return fontInfo.get(name);
	}

	public static function getFont(name:String):DialogFont
	{
		if(fonts == null)
		{
			initFonts();
		}

		return fonts.get(name);
	}

	public static function getDefaultFont():DialogFont
	{
		return defaultFont;
	}

	private static function initFonts():Void
	{
		var n_fonts:Map<String, Map<String, Dynamic>> = Util.readPrefsFile("fonts");
		var cur_font:Map<String, Dynamic>;
		fonts = new Map<String, DialogFont>();

		for(key in n_fonts.keys())
		{
			cur_font = n_fonts.get(key);
			fonts.set(key, new DialogFont(cur_font.get("base"), cur_font.get("color"), cur_font.get("dropshadow_color"), cur_font.get("dropshadow_xOffset"), cur_font.get("dropshadow_yOffset")));
		}

		defaultFont = new DialogFont("", -1, -1, 0, 0);
		fonts.set("null", defaultFont);
		fonts.set("", defaultFont);
	}
}
