package de.woody64k.libs.vhl.files;

public class VhlFileHolder {
	private String name;
	private byte[] content;

	public VhlFileHolder(String name, byte[] content) {
		super();
		this.name = name;
		this.content = content;
	}

	public VhlFileHolder(byte[] content) {
		super();
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getContent() {
		return content;
	}
}
