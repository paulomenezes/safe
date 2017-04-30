package menezes.paulo.safe.entity;

import java.io.Serializable;

public class Report implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int id;
	public int idPlace;
	public int idUser;
	public String type;
	public String title;
	public String description;
	public int anonymuos;
}
