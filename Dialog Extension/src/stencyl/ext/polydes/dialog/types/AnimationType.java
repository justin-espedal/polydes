package stencyl.ext.polydes.dialog.types;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;

import stencyl.core.engine.actor.IActorType;
import stencyl.core.lib.Game;
import stencyl.core.lib.Resource;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
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
		Animation a = new Animation();
		
		String rid = s.split("-")[0];
		try
		{
			Resource r = Game.getGame().getResources().getResource(Integer.parseInt(rid));
			if(r instanceof IActorType)
			{
				a.actor = (IActorType) r;
				a.anim = s.substring(rid.length() + 1);
			}
		}
		catch(NumberFormatException e)
		{
			a.actor = null;
			a.anim = "";
		}
		
		return a;
	}

	@Override
	public String encode(Animation a)
	{
		if(a == null || a.actor == null)
			return "";
		
		return a.actor.getID() + "-" + a.anim;
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
	public JComponent[] getEditor(final DataUpdater<Animation> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final UpdatingCombo<IActorType> actorCombo = new UpdatingCombo<IActorType>(Game.getGame().getResources().getResourcesByType(IActorType.class), null);
		final JTextField animField = style.createTextField();
		if(updater.get() == null)
			updater.set(new Animation());
		final Animation a = updater.get();
		
		actorCombo.setSelectedItem(a.actor);
		animField.setText(a.anim);
		
		actorCombo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				a.actor = actorCombo.getSelected();
				updater.updated();
			}
		});
		animField.getDocument().addDocumentListener(new DocumentAdapter(false)
		{
			@Override
			protected void update()
			{
				a.anim = animField.getText();
				updater.updated();
			}
		});
		
		return comps(actorCombo, animField);
	}
	
	@Override
	public String toDisplayString(Animation data)
	{
		return String.valueOf(data.actor) + " - " + data.anim;
	}

	@Override
	public Animation copy(Animation a)
	{
		Animation copyAnim = new Animation();
		copyAnim.actor = a.actor;
		copyAnim.anim = a.anim;
		return copyAnim;
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
}
