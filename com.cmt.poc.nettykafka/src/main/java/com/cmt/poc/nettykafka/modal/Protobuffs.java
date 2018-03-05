/**
 * 
 */
package com.cmt.poc.nettykafka.modal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author c.tripathi
 *
 */
public class Protobuffs implements Serializable {
	
	private static final long serialVersionUID = -1965916233747761384L;
	
	private List<Protobuff> protobuffs;

	public Protobuffs() {
		super();
		this.protobuffs = new ArrayList<Protobuff>();
	}

	/**
	 * @return the protobuffs
	 */
	public List<Protobuff> getProtobuffs() {
		return protobuffs;
	}

	/**
	 * @param protobuffs the protobuffs to set
	 */
	public void setProtobuffs(List<Protobuff> protobuffs) {
		this.protobuffs = protobuffs;
	}
	
	public void addProtobuff(Protobuff protobuff) {
		protobuffs.add(protobuff);
	}

}
