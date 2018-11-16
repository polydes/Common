package com.polydes.common.comp.colors;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class CheckerboardPaint implements Paint
{
	private CheckerboardPaintContext context;
	
	public CheckerboardPaint(int pixelSize)
	{
		context = new CheckerboardPaintContext(pixelSize);
	}
	
	@Override
	public int getTransparency()
	{
		return Transparency.OPAQUE;
	}

	@Override
	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints)
	{
		context.setOffset(userBounds.getBounds().x - deviceBounds.x, userBounds.getBounds().y - deviceBounds.y);
		return context;
	}
	
	class CheckerboardPaintContext implements PaintContext
	{
		private WritableRaster savedTile;
		private int pixelSize;
		private Point offset;
		
		public CheckerboardPaintContext(int pixelSize)
		{
			this.pixelSize = pixelSize;
			offset = new Point(0, 0);
		}
		
		public void setOffset(int x, int y)
		{
			offset.x = x;
			offset.y = y;
		}
		
		@Override
		public void dispose()
		{
		}
		
		@Override
		public ColorModel getColorModel()
		{
			return ColorModel.getRGBdefault();
		}
		
		@Override
		public Raster getRaster(int x, int y, int w, int h)
		{
			WritableRaster t = savedTile;
			
			if(t == null)
			{
				Rectangle r = new Rectangle(x, y, w, h);
				
				x = 0;
				y = 0;
				w = 32 + pixelSize * 2;
				h = 32 + pixelSize * 2;
				
				int[] gray = new int[] {0xBF, 0xBF, 0xBF, 0xFF};
				int[] white = new int[] {0xFF, 0xFF, 0xFF, 0xFF};
				
				t = getColorModel().createCompatibleWritableRaster(w, h);
				
				for(int x1 = 0; x1 < w; ++x1)
					for(int y1 = 0; y1 < h; ++y1)
						t.setPixel(x1, y1, white);
				
				int xstart;
				
				for(int y1 = y; y1 < y + h; ++y1)
				{
					if((y1 / pixelSize) % 2 == 0)
						xstart = x / pixelSize * pixelSize;
					else
						xstart = x / pixelSize * pixelSize + pixelSize;
					
					for(int x1 = xstart; x1 < x + w; x1 += pixelSize* 2)
					{
						int from = x1;
						int to = x1 + pixelSize;
						
						from -= x;
						to -= x;
						
						if(from < 0)
							from = 0;
						if(to > w)
							to = w;
						
						for(int _x = from; _x < to; ++_x)
							t.setPixel(_x, y1, gray);
					}
				}
				
				savedTile = t;
				
				x = r.x;
				y = r.y;
				w = r.width;
				h = r.height;
			}
			
			int dx = (offset.x + x) % (pixelSize * 2);
			int dy = (offset.y + y) % (pixelSize * 2);
			
			return t.createChild(dx, dy, w, h, 0, 0, null);
		}
	}
}
