package kernel.frames.views;

import java.util.List;

import gui.PaneFX;
import gui.dialogitems.OmMenu;
import javafx.scene.control.MenuBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;

public class TitlePane extends PaneFX {
	
	EditorView editor;
	MenuBar menuBar = null;
	
	public TitlePane () {
		setStyle("-fx-border-color: red;");
		omSetBackground (new Color (0.85, 0.85, 0.85, 1.0));
		setEffect(new DropShadow());
	}
	
	public void setEditor (EditorView ed) {
		editor = ed;
		List<OmMenu> menuitems = editor.getMenu();
		if (menuitems != null) {
			menuBar = new MenuBar ();
			menuBar.getMenus().addAll(menuitems);
			getChildren().addAll(menuBar);
		}   
	}
	
	public void init () {
	}

}

