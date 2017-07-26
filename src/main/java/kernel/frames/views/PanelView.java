package kernel.frames.views;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import gui.PaneScrollerFX;



public class PanelView extends PaneScrollerFX implements I_Panel{
	
	
	public void init (){
		setStyle("-fx-border-color: blue;");
		delegate.requestFocus();
		setPanelColor();
	}

	public EditorView getEditor (){
		return(EditorView)  omViewContainer();
	}
	
	public PanelView getPanel (){
		return this;
	}
	
	public Point2D PanelPosition (){
		return new Point2D(0,0);
	}

	public void setPanelColor () {
		omSetBackground(Color.WHITE);
	}
	
	@Override
	public void updatePanel (boolean changed) {
	}

	@Override
	public void KeyHandler(String car) {
		// TODO Auto-generated method stub
		
	}
	
	////////////////////////////////////////////////
	
}



