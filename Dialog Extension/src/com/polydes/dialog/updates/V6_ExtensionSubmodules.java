package com.polydes.dialog.updates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.polydes.common.ext.ExtensionInterface;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.datastruct.data.structure.StructureFolder;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.updates.TypenameUpdater;

import stencyl.core.engine.sound.ISoundClip;
import stencyl.core.lib.Game;
import stencyl.core.lib.Resource;

public class V6_ExtensionSubmodules implements Runnable
{
	@Override
	public void run()
	{
		ExtensionInterface.doUponAllLoaded(new String[] {"com.polydes.dialog", "com.polydes.datastruct"}, () ->
		{
			Structure log = make("dialog.ds.ext.Logic", "Dialog/Plugins/Logic");
			Structure ms = make("dialog.ds.ext.MessagingScripts", "Dialog/Plugins/Messaging Scripts");
			Structure ss = make("dialog.ds.ext.SoundScripts", "Dialog/Plugins/Sound Scripts");
			
			StructureDefinitions sdefs = DataStructuresExtension.get().getStructureDefinitions();
			StructureDefinition unknownDef = sdefs.getItem("Style");
			for(Structure style : loopDef(unknownDef))
			{
				String prefix = "Dialog/Plugins/" + style.dref.getName() + " ";
				Structure db = make("dialog.ds.ext.DialogBase", prefix + "Dialog Base");
				Structure ts = make("dialog.ds.ext.TypingScripts", prefix + "Typing Scripts");
				Structure eg = make("dialog.ds.ext.ExtraGlyphs", prefix + "Extra Glyphs");
				Structure cs = make("dialog.ds.ext.CharacterScripts", prefix + "Character Scripts");
				Structure sks = make("dialog.ds.ext.SkipScripts", prefix + "Skip Scripts");
				Structure fs = make("dialog.ds.ext.FlowScripts", prefix + "Flow Scripts");
				Structure te = make("dialog.ds.ext.TextEffects", prefix + "Text Effects");
				Structure dop = make("dialog.ds.ext.DialogOptions", prefix + "Dialog Options");
				
				Map<String,String> map = style.getUnknownData();
				
				//Dialog Base
				for (String prop : new String[] {
					"msgWindow", "msgBounds", "msgFont",
					"msgTypeSpeed", "msgStartSound",
					"controlAttribute", "lineSpacing",
					"charSpacing", "clearSound", "closeSound",
					"endSound" })
					set(db, prop, map.remove(prop));
				
				//Typing Scripts
				set(ts, "defaultRandomTypeSounds", stringArrayToSoundArray(map.remove("defaultRandomTypeSounds")));
				set(ts, "characterSkipSFX", map.remove("characterSkipSFX"));
				set(ts, "playTypeSoundOnSpaces", map.remove("playTypeSoundOnSpaces"));
				
				//Extra Glyphs
				set(eg, "glyphPadding", map.remove("glyphPadding"));
				
				//Character Scripts
				for (String prop : new String[] { "nameboxWindow",
					"nameboxFont", "faceImagePrefix",
					"faceRelation" })
					set(cs, prop, map.remove(prop));
				set(cs, "faceOrigin", convertRatioPoint(map.remove("faceOrigin")));
				set(cs, "facePos", convertRatioPoint(map.remove("facePos")));
				set(cs, "faceMsgOffset", convertRectangleToInsets(map.remove("faceMsgOffset"), "[%s,-%s,-%s,%s]"));
				
				//Skip Scripts
				for (String prop : new String[] { "fastSpeed",
					"fastButton", "fastSoundInterval", "zoomSpeed",
					"zoomButton", "zoomSoundInterval",
					"instantButton", "instantSound",
					"skippableDefault" })
					set(sks, prop, map.remove(prop));
				set(sks, "fastSound", stringArrayToSoundArray(map.remove("fastSound")));
				set(sks, "zoomSound", stringArrayToSoundArray(map.remove("zoomSound")));
				
				//Flow Scripts
				for (String prop : new String[] { "advanceDialogButton",
					"waitingSound", "waitingSoundInterval",
					"inputSound", "noInputSoundWithTags" })
					set(fs, prop, map.remove(prop));
				set(fs, "animForPointer", convertAnimation(map.remove("animForPointer")));
				set(fs, "pointerPos", convertRatioPoint(map.remove("pointerPos")));
				
				//Text Effects
				for (String prop : new String[] { "v_maxShakeOffsetX",
					"v_maxShakeOffsetY", "v_shakeFrequency",
					"s_magnitude", "s_frequency", "s_pattern",
					"r_diameter", "r_frequency", "r_pattern",
					"g_start", "g_stop", "g_duration" })
					set(te, prop, map.remove(prop));
				
				//Dialog Options
				for(Entry<Object, Object> entry : Lang.hashmap(
					"optWindow", "windowTemplate",
					"optWindowFont", "windowFont",
					"optCursorType", "cursorType",
					"optCursorImage", "cursorImage",
					"optCursorOffset", "cursorOffset",
					"optCursorWindow", "cursorWindow",
					"optChoiceLayout", "choiceLayout",
					"optSelectButton", "selectButton",
					"optScrollWait", "scrollWait",
					"optScrollDuration", "scrollDuration",
					"optAppearSound", "appearSound",
					"optChangeSound", "changeSound",
					"optConfirmSound", "confirmSound",
					"optItemPadding", "itemPadding",
					"optInactiveTime", "inactiveTime").entrySet())
					set(dop, (String) entry.getValue(), map.remove((String) entry.getKey()));
				
				String extensionList =
					StringUtils.join(
						Lang.mapCA
						(
							Lang.arraylist(log,ms,ss,db,ts,eg,cs,sks,fs,te,dop),
							Integer.class,
							(struct) -> ((Structure) struct).getID()
						),
						","
					);
				extensionList = "[" + extensionList + "]:dialog.ds.DialogExtension";
				map.put("extensions", extensionList);
			}
			
			unknownDef = sdefs.getItem("ScalingImage");
			for(Structure scalingImage : loopDef(unknownDef))
			{
				Map<String,String> map = scalingImage.getUnknownData();
				map.put("origin", convertRatioPoint(map.get("origin")));
				map.put("border", convertPointToInsets(map.remove("border")));
				
				System.out.println(scalingImage.dref.getName());
				System.out.println(map);
			}
			
			unknownDef = sdefs.getItem("Tween");
			for(Structure tween : loopDef(unknownDef))
			{
				Map<String,String> map = tween.getUnknownData();
				map.put("positionStart", convertRatioPoint(map.get("positionStart")));
				map.put("positionStop", convertRatioPoint(map.get("positionStop")));
				
				System.out.println(tween.dref.getName());
				System.out.println(map);
			}
			
			unknownDef = sdefs.getItem("Window");
			for(Structure window : loopDef(unknownDef))
			{
				Map<String,String> map = window.getUnknownData();
				map.put("position", convertRatioPoint(map.get("position")));
				map.put("scaleWidthSize", convertRatioInt(map.get("scaleWidthSize")));
				map.put("scaleHeightSize", convertRatioInt(map.get("scaleHeightSize")));
				map.put("insets", convertRectangleToInsets(map.remove("insets"), "[%s,%s,%s,%s]"));
				
				System.out.println(window.dref.getName());
				System.out.println(map);
			}
			
			TypenameUpdater tu = new TypenameUpdater();
			tu.addTypes(Lang.hashmap(
				"Animation", "dialog.core.Animation",
				"RatioInt", "dialog.geom.RatioInt",
				"RatioPoint", "dialog.geom.RatioPoint",
				"Point", "openfl.geom.Point",
				"Rectangle", "openfl.geom.Rectangle",
				"Window", "dialog.ds.WindowTemplate",
				"Tween", "dialog.ds.TweenTemplate",
				"Style", "dialog.ds.Style",
				"ScalingImage", "dialog.ds.ScalingImageTemplate"
			));
			tu.convert();
			
			DataStructuresExtension.forceUpdateData = true;
			DataStructuresExtension.get().onGameSave(Game.getGame());
		});
	}
	
	private Collection<Structure> loopDef(StructureDefinition def)
	{
		if(def == null || !Structures.structures.containsKey(def))
			return new ArrayList<Structure>();
		return Structures.structures.get(def);
	}
	
	private void set(Structure s, String field, String value)
	{
		System.out.println(s.dref.getName() + ":" + field + "=" + value);
		StructureField f = s.getTemplate().getField(field);
		s.setPropertyFromString(f, value);
		s.setPropertyEnabled(f, !f.isOptional() || s.getProperty(f) != null);
	}
	
	private Structure make(String def, String path)
	{
		String[] pathParts = path.split("/");
		String name = pathParts[pathParts.length - 1];
		pathParts = ArrayUtils.remove(pathParts, pathParts.length - 1);
		
		StructureDefinitions sdefs = DataStructuresExtension.get().getStructureDefinitions();
		
		int id = Structures.newID();
		StructureDefinition type = sdefs.getItem(def);
		Structure toReturn = new Structure(id, name, type);
		toReturn.loadDefaults();
		Structures.structures.get(type).add(toReturn);
		Structures.structuresByID.put(id, toReturn);
		
		DefaultLeaf item = Structures.root;
		for(String pathPart : pathParts)
		{
			Folder f = (Folder) item;
			if(f.getItemByName(pathPart) == null)
				f.addItem(new StructureFolder(pathPart));
			
			item = (DefaultLeaf) f.getItemByName(pathPart);
		}
		((Folder) item).addItem(toReturn.dref);
		
		return toReturn;
	}
	
	private String convertAnimation(String s)
	{
		return s == null ? null : "[" + s.replace("-", ",") + "]";
	}
	
	private String convertRatioInt(String s)
	{
		return s == null ? null : "[" + s + "]";
	}
	
	private String convertRatioPoint(String s)
	{
		return s == null ? null : s.replaceAll("([^,]+)", "\\[$0\\]");
	}
	
	private String convertRectangleToInsets(String s, String format)
	{
		String[] parts = s.replaceAll("\\[|\\]| ", "").split(",");
		String newValue = String.format(format, parts[1], parts[2], parts[3], parts[0]);
		newValue.replaceAll("--", "").replaceAll("-0","0");
		return newValue;
	}
	
	private String convertPointToInsets(String s)
	{
		if(s == null)
			return "[0,0,0,0]";
		String[] parts = s.replaceAll("\\[|\\]| ", "").split(",");
		String newValue = String.format("[%s,%s,%s,%s", parts[1], parts[0], parts[1], parts[0]);
		return newValue;
	}
	
	private String simplifyArray(String s)
	{
		return s == null ? null : s.replaceAll(":[^,\\]]+", "").replaceAll("\\[|\\]", "");
	}
	
	private String stringArrayToSoundArray(String s)
	{
		if(s == null)
			return null;
		s = simplifyArray(s);
		String[] ids = s.split(",");
		for(int i = 0; i < ids.length; ++i)
		{
			Resource r = Game.getGame().getResources().getResourceWithName(s);
			if(r != null && ISoundClip.class.isAssignableFrom(r.getClass()))
				ids[i] = String.valueOf(r.getID());
			else
				ids[i] = "";
		}
		return "[" + StringUtils.join(ids, ",") + "]:com.stencyl.models.Sound";
	}
}
