package com.polydes.common.sw;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stencyl.core.engine.snippet.ISnippet;
import stencyl.core.engine.snippet.SnippetInstance;
import stencyl.core.lib.Game;
import stencyl.core.lib.io.IOHelper;
import stencyl.core.lib.scene.SceneModel;
import stencyl.sw.SW;
import stencyl.sw.io.write.resource.SceneWriter;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class Scenes
{
	private static final Logger log = Logger.getLogger(Scenes.class);
	
	public static SceneModel ensureLoaded(SceneModel model)
	{
		if(!model.hasLoaded())
			model = model.headerLoad();
		
		return model;
	}
	
	public static boolean hasSnippet(SceneModel model, ISnippet s)
	{
		for(SnippetInstance si : model.getSnippets().values())
			if(si.getSnippet().getID() == s.getID())
				return true;
		
		return false;
	}
	
	public static enum Xml
	{
		Snippets("snippets", (doc, scene) -> SceneWriter.writeSnippets(doc, scene.getSnippets(), Game.getGame()));
		
		String tag;
		BiFunction<Document, SceneModel, Element> writer;
		
		private Xml(String tag, BiFunction<Document, SceneModel, Element> writer)
		{
			this.tag = tag;
			this.writer = writer;
		}
	}
	
	public static void rewriteXml(SceneModel model, Xml xml)
	{
		try
		{
			String url = Locations.getSceneHeaderLocation(model.getID());
			
			Document doc = IOHelper.readXMLFromResource(url);
			Element root = doc.getDocumentElement();
			
			Document document = FileHelper.newDocument();
			
			Node rewriteNode = null;
			NodeList nl = root.getElementsByTagName(xml.tag);
			if(nl.getLength() == 1)
				rewriteNode = nl.item(0);
			if(rewriteNode != null)
				root.removeChild(rewriteNode);
			
			root = (Element) document.adoptNode(root);
			
			Element newNode = xml.writer.apply(document, model);
		
			root.appendChild(newNode);
			
			//Finish
			document.appendChild(root);
			
			//---
			
			File sceneFile = new File(Locations.getGameLocation(Game.getGame()) + url);
			
			FileHelper.writeXMLToFile(document, sceneFile);
		}
		
		catch(IOException e)
		{
			log.error(e.getMessage(), e);
		}
	}
	
	public static void addSnippet(SceneModel model, ISnippet s)
	{
		model.getSnippets().put(s.getID(), new SnippetInstance(s));
	}
	
	public static boolean isNewScene(SceneModel model)
	{
		return SW.get().getWorkspace().isResourceOpen(model) && SW.get().getWorkspace().getDocumentForResource(model).isNew();
	}
}
