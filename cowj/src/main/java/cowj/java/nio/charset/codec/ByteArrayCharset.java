package cowj.java.nio.charset.codec;

import java.util.HashMap;
import cowj.java.nio.ByteBuffer;
import cowj.java.nio.CharBuffer;
import cowj.java.nio.charset.Charset;
import cowj.java.nio.charset.CharsetDecoder;
import cowj.java.nio.charset.CharsetEncoder;
import cowj.java.nio.charset.CoderResult;

public abstract class ByteArrayCharset extends Charset
{
	private static char[] _decoderTable;
	
	private static HashMap<Character, Byte> _encoderTable;
	
	protected static void setDecoderTable(char[] decoderTable)
	{
		_decoderTable = decoderTable;
		_encoderTable = new HashMap<Character, Byte>();
		for (int i = 0; i < 0x100; i++)
			_encoderTable.put(decoderTable[i], (byte) i);
	}
	
	static class Decoder extends CharsetDecoder
	{
		public Decoder(Charset charset)
		{
			super(charset, 1.0f, 1.0f);
		}
		
		@Override
		protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out)
		{
			int length = Math.min(in.remaining(), out.remaining());
			for (int i = 0; i < length; i++)
			{
				byte b = in.get();
				out.put(_decoderTable[((int) b & 0xff)]);
			}
			
			if (in.remaining() > out.remaining())
				return CoderResult.OVERFLOW;
			return CoderResult.UNDERFLOW;
		}
	}
	
	static class Encoder extends CharsetEncoder
	{
		public Encoder(Charset charset)
		{
			super(charset, 1.0f, 1.0f);
		}
		
		@Override
		protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out)
		{
			int length = Math.min(in.remaining(), out.remaining());
			for (int i = 0; i < length; i++)
			{
				char c = in.get();
				if (c > 0xff)
					return CoderResult.unmappableForLength(1);
				out.put(_encoderTable.get(c));
			}
			
			if (in.remaining() > out.remaining())
				return CoderResult.OVERFLOW;
			return CoderResult.UNDERFLOW;
		}
	}
	
	public ByteArrayCharset(String name, String[] aliases)
    {
	    super(name, aliases);
    }
	
	@Override
	public boolean contains(Charset cs)
	{
		return false;
	}
	
	@Override
	public CharsetEncoder newEncoder()
	{
		return new Encoder(this);
	}
	
	@Override
	public CharsetDecoder newDecoder()
	{
		return new Decoder(this);
	}
}
