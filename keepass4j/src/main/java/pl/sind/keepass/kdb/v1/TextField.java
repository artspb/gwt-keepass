package pl.sind.keepass.kdb.v1;

import cowj.java.nio.ByteBuffer;

public class TextField extends Field {

	public TextField(short fieldType, int fieldSize, ByteBuffer data) {
		// Strings are null terminated, remove 1 from size and remove from buffer
		super(fieldType, fieldSize-1,fieldSize-1, data);
		data.get();
	}
	
	public String getText() {
		return new String(getFieldData());
	}

	public void setText(String text) {
		setFieldData(text.getBytes());
	}

}
