package pl.sind.keepass.kdb.v1;

import cowj.java.nio.ByteBuffer;
import pl.sind.keepass.util.Utils;

import java.util.Date;

public class DateField extends Field {

	public DateField(short fieldType, int fieldSize, ByteBuffer data) {
		super(fieldType, fieldSize,DATE_FIELD_SIZE, data);
	}

	public Date getDate(){
		return Utils.unpackDate(getFieldData());
	}
	
	public void setDate(Date date){
		setFieldData(Utils.packDate(date));
	}
	
}
