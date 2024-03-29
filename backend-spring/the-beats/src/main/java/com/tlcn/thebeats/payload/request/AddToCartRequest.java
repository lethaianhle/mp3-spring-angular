package com.tlcn.thebeats.payload.request;

public class AddToCartRequest {

	private int userId;
	
	private double price;

	private int songId;

	private String songName;

	private String avatar;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getSongId() {
		return songId;
	}

	public void setSongId(int songId) {
		this.songId = songId;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public AddToCartRequest(int userId, double price, int songId, String songName) {
		super();
		this.userId = userId;
		this.price = price;
		this.songId = songId;
		this.songName = songName;
	}
	
	private AddToCartRequest() {};

	@Override
	public String toString() {
		return "AddToCartRequest{" +
				"userId=" + userId +
				", price=" + price +
				", songId=" + songId +
				", songName='" + songName + '\'' +
				", avatar='" + avatar + '\'' +
				'}';
	}
}
