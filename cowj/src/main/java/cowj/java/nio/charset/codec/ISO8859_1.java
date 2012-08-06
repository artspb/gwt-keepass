package cowj.java.nio.charset.codec;

import cowj.java.nio.ByteBuffer;
import cowj.java.nio.CharBuffer;
import cowj.java.nio.charset.Charset;
import cowj.java.nio.charset.CharsetDecoder;
import cowj.java.nio.charset.CharsetEncoder;
import cowj.java.nio.charset.CoderResult;

public class ISO8859_1 extends Charset
{
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
				out.put((char) ((int)b & 0xff));
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
				out.put((byte) c);
			}
			
			if (in.remaining() > out.remaining())
				return CoderResult.OVERFLOW;
			return CoderResult.UNDERFLOW;
		}
	}
	
	public ISO8859_1()
    {
		super("ISO8859_1", null);
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
