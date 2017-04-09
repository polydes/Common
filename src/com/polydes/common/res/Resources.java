package com.polydes.common.res;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import misc.gfx.GraphicsUtilities;
import misc.gfx.PunchIconFactory;

public abstract class Resources
{
	private static final Logger log = Logger.getLogger(Resources.class);
	
	protected static HashMap<String, Object> resourceCache = new HashMap<String, Object>();
	
	protected final String packageName;
	
	protected Resources(String packageName)
	{
		this.packageName = packageName.replaceAll("\\.", "/") + "/";
	}
	
	public abstract URL getUrl(String name);
	public abstract InputStream getUrlStream(String name);
	
	public <T> T load(String name, Function<URL, T> constructor)
	{
		String key = packageName + name;
		
		@SuppressWarnings("unchecked")
		T result = (T) resourceCache.get(key);
		
		if(result != null)
		{
			return result;
		}
		
		URL u = getUrl(name);
		
		try
		{
			result = constructor.apply(u);
			resourceCache.put(key, result);
			return result;
		}
		catch (Exception e)
		{
			log.error("Failed to load resource: " + key, e);
		}
		
		return null;
	}
	
	private static Function<URL, ImageIcon> iconLoader =
		url -> new ImageIcon(url);
		
	private static Function<URL, String> textLoader =
		url -> {
			try
			{
				return IOUtils.toString(url);
			}
			catch(IOException ex)
			{
				log.error(ex.getMessage(), ex);
				return "";
			}
		};
		
	private static Function<URL, BufferedImage> imageLoader =
		url -> {
			try
			{
				return ImageIO.read(url);
			}
			catch(IOException ex)
			{
				log.error(ex.getMessage(), ex);
				return null;
			}
		};
	
	public ImageIcon loadIcon(String url)
	{
		return load(url, iconLoader);
	}
	
	public ImageIcon loadPunchIcon(String url)
    {
		Image img = loadIcon(url).getImage();
		
		return PunchIconFactory.createPunchedIcon(img, 2);
    }
	
	public ImageIcon loadThumbnail(String url, int size)
	{
		Image img = loadIcon(url).getImage();
		
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		g.drawImage(img, 0, 0, bi.getWidth(), bi.getHeight(), null);
		
		return new ImageIcon(GraphicsUtilities.createThumbnail(bi, size));
	}
	
	public String loadText(String name)
	{
		return load(name, textLoader);
	}
	
	public BufferedImage loadImage(String name)
	{
		return load(name, imageLoader);
	}
}
