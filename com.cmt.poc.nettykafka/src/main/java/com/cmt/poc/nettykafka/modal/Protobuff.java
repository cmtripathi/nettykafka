/**
 * 
 */
package com.cmt.poc.nettykafka.modal;

import java.io.Serializable;

/**
 * @author c.tripathi
 *
 */
public class Protobuff implements Serializable {

	private static final long serialVersionUID = 3865376084391104508L;
	
	private String name;
	
	public Protobuff(String name) {
		super();
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
