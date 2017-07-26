package gui;

import java.util.List;

public interface I_ContainerView extends I_SimpleView {
	
	public void omAddSubview (I_SimpleView child) ;
	public default void  omAddSubviews (I_SimpleView... child) {
		for (int i = 0; i < child.length ; i++) {
			omAddSubview(child[i]);
		}
	}
	public void omRemoveSubview (I_SimpleView child) ;
	public default void omRemoveSubviews (I_SimpleView... child) {
		for (int i = 0; i < child.length ; i++){
			omRemoveSubview(child[i]);
		}
	}
	public List<I_SimpleView> omSubviews ();
	public List<I_SimpleView> getActives ();
	}

