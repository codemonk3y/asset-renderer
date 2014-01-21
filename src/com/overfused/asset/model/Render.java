package com.overfused.asset.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Render implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = -6792354954973277201L;
	

    private String device;
    private String view;
    private String partPath;
    private List<Set<String>> sets = new ArrayList<Set<String>>();
    
	
    public Render() {
	    super();
    }
    
    

	public Render(String device, String view, String partPath, List<Set<String>> sets) {
	    super();
	    this.device = device;
	    this.view = view;
	    this.partPath = partPath;
	    this.sets = sets;
    }



	public String getDevice() {
    	return device;
    }
	
    public String getView() {
    	return view;
    }
	
    public String getPartPath() {
    	return partPath;
    }
	
    public List<Set<String>> getSets() {
    	return sets;
    }



	@Override
    public String toString() {
	    return "Render [sets=" + sets + "]";
    }
    
    
    
}
