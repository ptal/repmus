package kernel.metaobjects;

import javafx.geometry.Point2D;
import kernel.I_OMObject;
import kernel.frames.simples.ClassBoxFrame;
import kernel.interfaces.I_SimpleFrame;

public class CteBox extends Box {

	public String reference;
	public I_OMObject value;

	public CteBox(String val) {
		reference = val;
		value = null;
	}

	@Override
	public I_SimpleFrame makeFrame() {
		I_SimpleFrame frame = new ClassBoxFrame (this, frameposition,new Point2D(100, 60), icon );
		boxframe = frame;
		return frame;
	}

	public Object getValue() {
		return value;
	}

}
