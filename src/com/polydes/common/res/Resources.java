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

public class Resources
{
	private static final Logger log = Logger.getLogger(Resources.class);
	
	private static HashMap<String, Object> resourceCache = new HashMap<String, Object>();
	
	private String packageName;
	
	Resources(String packageName)
	{
		this.packageName = "/res/" + packageName.replaceAll("\\.", "/") + "/";
	}
	
	public URL getUrl(String name)
	{
		return getClass().getResource(packageName + name);
	}
	
	public InputStream getUrlStream(String name)
	{
		return getClass().getResourceAsStream(packageName + name);
	}

	public <T> T load(String name, Function<URL, T> constructor)
	{
		String url = packageName + name;
		
		@SuppressWarnings("unchecked")
		T result = (T) resourceCache.get(url);
		
		if(result != null)
		{
			return result;
		}
		
		URL u = getClass().getResource(url);
		
		try
		{
			result = constructor.apply(u);
			resourceCache.put(url, result);
			return result;
		}
		catch (Exception e)
		{
			log.error("Failed to load resource: " + url, e);
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
