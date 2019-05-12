package asd.logic;

import java.io.IOException;
import java.io.InputStream;

public class Input {

	private InputStream in;

	private int codeWordLength;

	private int mask;

	private long buf;

	private int bufUsageBits;

	private int bufUsageBytes;

	private int bufUsageSymbols;

	private int bufferedCodes;

	private boolean eof;

	Input(InputStream in, int codeWordLength) {
		this.in = in;
		this.codeWordLength = codeWordLength;

		bufferedCodes = 0;
		buf = 0;
		bufUsageBits = (int) Euclid.LCM(Constants.BITS_IN_BYTE, codeWordLength);
		bufUsageBytes = bufUsageBits / Constants.BITS_IN_BYTE;
		bufUsageSymbols = bufUsageBits / codeWordLength;
		mask = (1 << codeWordLength) - 1;
	}

	int read() throws IOException {
		if ((bufferedCodes <= 0) && (!eof)) {
			buf = 0;
			for (int i = 0; i < bufUsageBytes; i++) {
				int read = in.read();
				if (-1 == read) {
					// read = 0;
					eof = true;
				}
				read = read & Constants.BYTE_MASK;
				read <<= i * Constants.BITS_IN_BYTE;
				buf |= read;
			}
			bufferedCodes = bufUsageSymbols;
		}
		if (bufferedCodes > 0) {
			int code = (int) (buf & mask);
			buf >>= codeWordLength;
			bufferedCodes--;
			if (code < mask) {
				return code;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	public void close() throws IOException {
		in.close();
	}
}
