package cowj.java.nio.charset.codec;

import cowj.java.nio.ByteBuffer;
import cowj.java.nio.CharBuffer;
import cowj.java.nio.charset.Charset;
import cowj.java.nio.charset.CharsetDecoder;
import cowj.java.nio.charset.CharsetEncoder;
import cowj.java.nio.charset.CoderResult;

public class UTF8 extends Charset
{
	static class Decoder extends CharsetDecoder
	{
		private static final int[] offsets = new int[]
		{
			0x00000000, 0x00003080, 0x000E2080,
		    0x03C82080, 0xFA082080, 0x82082080
		};
		
		private static final int[] trailing_bytes = new int[]
		{
		     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 0
		     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 1
		     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 2
		     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 3
		     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 4
		     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 5
		     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 6
		     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 7
		    -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,  // 8
		    -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,  // 9
		    -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,  // A
		    -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,  // B
		     1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  // C
		     1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  // D
		     2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,  // E
		     3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5,  // F
		};

		private int[] buffer = new int[6];
		private int bsize = 0;
		
		public Decoder(Charset charset)
        {
			super(charset, 1.0f, 1.0f);
        }
		
		@Override
		protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out)
		{
			while ((in.remaining() > 0) && (out.remaining() > 1))
			{
				int b = ((int)in.get()) & 0xff;
				
				buffer[bsize++] = b;
				int tb = trailing_bytes[buffer[0]];
				if (tb == -1)
				{
					bsize = 0;
					return CoderResult.malformedForLength(1);
				}
				if (bsize > tb)
				{
					int i = 0;
					int uni = 0;
					switch (tb)
					{
						/* These fall through deliberately. */
						case 3: uni += buffer[i++]; uni <<= 6;
						case 2: uni += buffer[i++]; uni <<= 6;
						case 1: uni += buffer[i++]; uni <<= 6;
						case 0: uni += buffer[i++];
					}
					
					bsize = 0;
					uni -= offsets[tb];
					out.put(Character.toChars(uni));
				}
			}

			if (in.remaining() > out.remaining())
				return CoderResult.OVERFLOW;
			return CoderResult.UNDERFLOW;
		}
	}
	
	public UTF8()
    {
		super("UTF8", null);
    }
	
	@Override
	public boolean contains(Charset cs)
	{
	    return false;
	}
	
	@Override
	public CharsetEncoder newEncoder()
	{
		throw new UnsupportedOperationException("Encoding UTF8 not supported");
	}
	
	@Override
	public CharsetDecoder newDecoder()
	{
		return new Decoder(this);
	}
}
