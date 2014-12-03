package stencyl.ext.polydes.dialog.types;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;

import stencyl.core.engine.actor.IActorType;
import stencyl.core.lib.Game;
import stencyl.core.lib.Resource;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.ui.comp.UpdatingCombo;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.dialog.data.Animation;
import stencyl.ext.polydes.dialog.io.Text;
import stencyl.ext.polydes.dialog.res.Resources;

public class AnimationType extends DataType<Animation>
{
	public AnimationType()
	{
		super(Animation.class, "scripts.ds.dialog.Animation", "OBJECT", "Animation");
	}

	@Override
	public Animation decode(String s)
	{
		Animation a = new Animation(null, "");
		
		String rid = s.split("-")[0];
		try
		{
			Resource r = Game.getGame().getResources().getResource(Integer.parseInt(rid));
			if(r instanceof IActorType)
			{
				a.actorType = (IActorType) r;
				a.anim = s.substring(rid.length() + 1);
			}
		}
		catch(NumberFormatException e)
		{
			a.actorType = null;
			a.anim = "";
		}
		
		return a;
	}

	@Override
	public String encode(Animation a)
	{
		if(a == null || a.actorType == null)
			return "";
		
		return a.actorType.getID() + "-" + a.anim;
	}

	@Override
	public List<String> generateHaxeClass()
	{
		return Text.readLines(Resources.getUrlStream("code/haxe/" + xml + ".hx"));
	}

	@Override
	public List<String> generateHaxeReader()
	{
		return Text.readLines(Resources.getUrlStream("code/haxer/" + xml + ".hx"));
	}

	@Override
	public DataEditor<Animation> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new AnimationEditor(style);
	}
	
	@Override
	public String toDisplayString(Animation data)
	{
		return String.valueOf(data.actorType) + " - " + data.anim;
	}

	@Override
	public Animation copy(Animation a)
	{
		return new Animation(a.actorType, a.anim);
	}

	@Override
	public ExtraProperties loadExtras(ExtrasMap arg0)
	{
		return null;
	}

	@Override
	public ExtrasMap saveExtras(ExtraProperties arg0)
	{
		return null;
	}
	
	public static class AnimationEditor extends DataEditor<Animation>
	{
		private UpdatingCombo<IActorType> actorCombo;
		private JTextField animField;
		private Animation a;
		
		public AnimationEditor(PropertiesSheetStyle style)
		{
			actorCombo = new UpdatingCombo<IActorType>(Game.getGame().getResources().getResourcesByType(IActorType.class), null);
			animField = style.createTextField();
			
			actorCombo.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					a.actorType = actorCombo.getSelected();
					updated();
				}
			});
			animField.getDocument().addDocumentListener(new DocumentAdapter(false)
			{
				@Override
				protected void update()
				{
					a.anim = animField.getText();
					updated();
				}
			});
		}
		
		@Override
		public void set(Animation t)
		{
			if(t == null)
				t = new Animation(null, "");
			a = t;
			
			actorCombo.setSelectedItem(t.actorType);
			animField.setText(t.anim);
		}
		
		@Override
		public Animation getValue()
		{
			return a;
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps(actorCombo, animField);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			actorCombo.dispose();
			actorCombo = null;
			animField = null;
		}
	}
}
