package sample.logic;

import java.io.IOException;
import java.io.OutputStream;

public class Output {

	private OutputStream out;

	int codeWordLength;

	private int mask;

	private long buf; // 8 bytes (64 bits), i hope

	private int written;

	private int bufUsageBits;

	private int bufUsageBytes;

	private int bufUsageSymbols;

	public Output(OutputStream out, int codeWordLength) {
		this.out = out;
		this.codeWordLength = codeWordLength;

		written = 0;
		buf = 0;
		bufUsageBits = (int) Euclid.LCM(Constants.BITS_IN_BYTE, codeWordLength);
		bufUsageBytes = bufUsageBits / Constants.BITS_IN_BYTE;
		bufUsageSymbols = bufUsageBits / codeWordLength;
		mask = (1 << codeWordLength) - 1;
	}

	public void write(int code) throws IOException {
		code = (code & mask) << ((written) * codeWordLength);
		buf |= code;
		written++;
		if (written >= bufUsageSymbols) {
			for (int i = 0; i < bufUsageBytes; i++) {
				out.write((int) (buf & Constants.BYTE_MASK));
				buf >>= Constants.BITS_IN_BYTE;
			}
			written = 0;
			buf = 0;
		}
	}

	public void flush() throws IOException {
		while ((written < bufUsageSymbols) && (written != 0)) {
			write(-1);
		}
		out.flush();
	}
}
