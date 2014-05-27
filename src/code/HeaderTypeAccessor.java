package code;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class HeaderTypeAccessor	// accessor class for HeaderType enum to avoid concurrency problems with the gui nad java.swing
{
	
		private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
		
		public void addPropertyChangeListener( PropertyChangeListener listener )
		{
		      propertyChangeSupport.addPropertyChangeListener( listener );
		}
		public synchronized void setData(HeaderType header, int data, boolean overrideAdditivity)
		{
			int oldData = header.getCurrentValue();
			header.setCurrentValue(data, overrideAdditivity);
			int newData = header.getCurrentValue();
			propertyChangeSupport.firePropertyChange("Header data", oldData, newData);
		}
}
