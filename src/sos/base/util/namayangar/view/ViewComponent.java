package sos.base.util.namayangar.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import rescuecore2.config.Config;
import sos.base.util.namayangar.misc.gui.PanZoomListener;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.tools.SOSRenderObject;
import sos.base.util.namayangar.tools.SOSSelectedObj;

/**
 * A JComponent that shows a view of a world model.
 */
public abstract class ViewComponent extends JComponent {
	private static final long serialVersionUID = 1L;

	private static final Color BACKGROUND = new Color(120, 120, 120);

	private PanZoomListener panZoom;
	private ScreenTransform transform;

	private List<RenderedObject> renderedObjects;
	private Set<ViewListener> listeners;

	/**
	 * Construct a new ViewComponent.
	 */
	protected ViewComponent() {
		renderedObjects = new ArrayList<RenderedObject>();
		listeners = new HashSet<ViewListener>();
		ViewerMouseListener l = new ViewerMouseListener();
		addMouseListener(l);
		addMouseMotionListener(l);
		panZoom = new PanZoomListener(this);
		setBackground(BACKGROUND);
		setOpaque(true);
	}

	/**
	 * Initialise this view component. Default implementation does nothing.
	 * 
	 * @param config
	 *            The system configuration.
	 */
	public void initialise(Config config) {
	}

	/**
	 * Get the name of this view component. Default implementation calls toString().
	 * 
	 * @return The name of this view component.
	 */
	public String getViewerName() {
		return toString();
	}

	/**
	 * Disable the pan-zoom feature.
	 */
	public void disablePanZoom() {
		removeMouseListener(panZoom);
		removeMouseMotionListener(panZoom);
		removeMouseWheelListener(panZoom);
		transform.resetZoom();
		repaint();
	}

	/**
	 * Enable the pan-zoom feature.
	 */
	public void enablePanZoom() {
		addMouseListener(panZoom);
		addMouseMotionListener(panZoom);
		addMouseWheelListener(panZoom);
	}

	/**
	 * @author Ali
	 * @return panZoom
	 */
	public PanZoomListener getPanZoom() {
		return panZoom;
	}

	/**
	 * @author Ali
	 * @param b
	 */
	public void setEnableDrag(boolean b) {
		if (b)
			addMouseMotionListener(panZoom);
		else
			removeMouseMotionListener(panZoom);
	}

	/**
	 * Add a view listener.
	 * 
	 * @param l
	 *            The listener to add.
	 */
	public void addViewListener(ViewListener l) {
		synchronized (listeners) {
			listeners.add(l);
		}
	}

	/**
	 * Remove a view listener.
	 * 
	 * @param l
	 *            The listener to remove.
	 */
	public void removeViewListener(ViewListener l) {
		synchronized (listeners) {
			listeners.remove(l);
		}
	}

	/**
	 * View a set of objects. Default implementation just calls repaint.
	 * 
	 * @param objects
	 *            The objects to view.
	 */
	public void view(Object... objects) {
		repaint();
	}

	@Override
	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = getWidth();
		int height = getHeight();
		Insets insets = getInsets();
		width -= insets.left + insets.right;
		height -= insets.top + insets.bottom;
		renderedObjects.clear();
		transform.rescale(width, height);
		Graphics2D copy = (Graphics2D) g.create(insets.left, insets.top, width, height);
		if (isOpaque()) {
			copy.setColor(getBackground());
			copy.fillRect(0, 0, width, height);
		}
		renderedObjects.addAll(render(copy, transform, width, height));
	}

	/**
	 * Render the world and return the set of RenderedObjects.
	 * 
	 * @param graphics
	 *            The graphics to draw on. The origin is guaranteed to be at 0, 0.
	 * @param transform
	 *            The ScreenTransform that will convert world coordinates to screen coordinates.
	 * @param width
	 *            The width of the viewport.
	 * @param height
	 *            The height of the viewport.
	 * @return The set of RenderedObjects.
	 */
	// CHECKSTYLE:OFF:HiddenField Supress bogus warning about transform hiding a field: it's not really hidden since this is an abstract method and transform is private.
	protected abstract Collection<RenderedObject> render(Graphics2D graphics, ScreenTransform transform, int width, int height);

	// CHECKSTYLE:ON:HiddenField

	/**
	 * Update the bounds of the view in world coordinates.
	 * 
	 * @param xMin
	 *            The minimum x coordinate.
	 * @param yMin
	 *            The minimum y coordinate.
	 * @param xMax
	 *            The maximum x coordinate.
	 * @param yMax
	 *            The maximum y coordinate.
	 */
	protected void updateBounds(double xMin, double yMin, double xMax, double yMax) {
		transform = new ScreenTransform(xMin, yMin, xMax, yMax);
		panZoom.setScreenTransform(transform);
	}	


	
	// private List<RenderedObject> getObjectsAtPoint(int x, int y) {
	// List<RenderedObject> result = new ArrayList<RenderedObject>();
	// for (RenderedObject next : renderedObjects) {
	// Shape shape = next.getShape();
	// if (shape != null && shape.contains(x, y)&& !result.contains(next)) {
	// result.add(next);
	// }
	// }
	// return result;
	// }

	private void fireObjectsClicked(Collection<SOSSelectedObj> collection, MouseEvent e) {
		List<ViewListener> all = new ArrayList<ViewListener>();
		synchronized (listeners) {
			all.addAll(listeners);
		}
		for (ViewListener next : all) {
			if (next instanceof SOSViewListener)
				((SOSViewListener) next).objectsClicked(this, collection, e);
//			else
//				next.objectsClicked(this, clicked);
		}
	}

	private void fireObjectsRollover(Collection<SOSSelectedObj> collection, MouseEvent e) {
		List<ViewListener> all = new ArrayList<ViewListener>();
		synchronized (listeners) {
			all.addAll(listeners);
		}
		for (ViewListener next : all) {
			if (next instanceof SOSViewListener)
				((SOSViewListener) next).objectsRollover(this, collection, e);
//			else
//				next.objectsRollover(this, collection);
		}
	}

	/**
	 * @author Ali
	 * @return transform
	 */
	public ScreenTransform getTransform() {
		return transform;
	}

	private class ViewerMouseListener implements MouseListener, MouseMotionListener {
		@Override
		public void mouseClicked(MouseEvent e) {
//			if (e.getButton() == MouseEvent.BUTTON1) {
				fireObjectsClicked(getObjects(e), e);
//			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			fireObjectsRollover(getObjects(e), e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		private Collection<SOSSelectedObj> getObjects(MouseEvent e) {
			Point p = e.getPoint();
			Insets insets = getInsets();
//			return getObjectsAtPoint(p.x - insets.left, p.y - insets.top);
			return getObjectsAtCircle(p.x - insets.left, p.y - insets.top,5);
		}
	}

	public Collection<SOSSelectedObj> getObjectsAtCircle(int x, int y, int d) {
		HashMap<Object, SOSSelectedObj> objs=new HashMap<Object, SOSSelectedObj>();
		for (RenderedObject next : renderedObjects) {
			Shape shape = next.getShape();
			if (shape != null && shape.intersects(x-d/2,y-d/2,d,d)) {
				if (next instanceof SOSRenderObject) {
					SOSRenderObject sro = (SOSRenderObject) next;
					if(objs.get(sro.getObject())==null)
						objs.put(sro.getObject(), new SOSSelectedObj(sro.getObject()));
					objs.get(sro.getObject()).addLayer(sro.getLayer());
				}
			}
		}
		return objs.values();
	}

	/*private boolean isInList(List<RenderedObject> result, RenderedObject next) {
		for (RenderedObject renderedObject : result) {
			if(renderedObject.getObject().hashCode()==next.getObject().hashCode())return true;
		}
		return false;
	}*/

	
}
