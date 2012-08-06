package cowj.gwt;

public class ByteStore
{
	public ByteStore()
	{
		init();
	}
	
	public ByteStore(String binaryData)
	{
		init(binaryData);
	}
	
	private native void init() /*-{
		this._data = [];
	}-*/;
	
	private native void init(String binaryData) /*-{
		this._data = [];
		this._data.length = binaryData.length;
		
		for (var i = 0; i < binaryData.length; i++)
		{
			var b = binaryData.charCodeAt(i);
			if (b > 0x7f)
				b -= 0x100;
			this._data[i] = b;
		}
	}-*/;
	
	public native int length() /*-{
		return this._data.length;
	}-*/;

	public native void resize(int size) /*-{
		var oldlength = _data.length;
		_data.length = size;
		if (oldlength < size)
		{
			for (var i = oldlength; i < size; i++)
				_data[i] = 0;
		}
	}-*/;
	
	public native byte get(int index) /*-{
		return this._data[index];
	}-*/;
	
	public native void put(int index, byte value) /*-{
		this._data[index] = value;
	}-*/;
}
