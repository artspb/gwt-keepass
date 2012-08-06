package pl.sind.keepass.kdb.v1;

import cowj.java.nio.ByteBuffer;

public class BinnaryField extends Field {

	public BinnaryField(short fieldType, int fieldSize, ByteBuffer data) {
		super(fieldType, fieldSize,fieldSize, data);
	}

}
