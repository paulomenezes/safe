package menezes.paulo.safe.entity;

import java.io.Serializable;

public class Place implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public int idUser;
	public String category;
	public String name;
	public String city;
	public String address;
	public String phone;
	public String image;
}
