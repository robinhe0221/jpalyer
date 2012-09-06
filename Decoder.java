import java.io.InputStream;

import javax.sound.sampled.AudioFormat;

abstract public class Decoder {

	InputStream decodeInputStream;
	int decodeSize;
	byte[] rawBuff;
	int rawSize;

	AudioFormat format;

	abstract public byte[] decode();

	public int getRawSize() {
		return rawSize;
	}

	public int getDecodeSize() {
		return decodeSize;
	}

	public AudioFormat getFormat() {
		return format;
	}
}
