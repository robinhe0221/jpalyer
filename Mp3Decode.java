import java.io.InputStream;

import javax.sound.sampled.AudioFormat;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.Obuffer;

class RawOutputBuff extends Obuffer {
	short[] buffer;
	int[] index;
	int channels;
	byte[] rawBuff;
	int rawSize;

	public RawOutputBuff(int channels, byte[] rawBuff) {
		buffer = new short[OBUFFERSIZE];
		index = new int[MAXCHANNELS];
		this.channels = channels;
		this.rawBuff = rawBuff;

		for (int i = 0; i < index.length; i++) {
			index[i] = i;
		}
	}

	@Override
	public void append(int channelNum, short val) {
		buffer[index[channelNum]] = val;
		index[channelNum] += channels;
	}

	@Override
	public void clear_buffer() {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void set_stop_flag() {
		// TODO Auto-generated method stub

	}

	@Override
	public void write_buffer(int arg0) {
		rawSize = buffer.length * 2;
		for (int i = 0; i < index[0]; i++) {
			rawBuff[2 * i] = (byte) (buffer[i] & 0xFF);
			rawBuff[2 * i + 1] = (byte) ((buffer[i] >> 8) & 0xFF);
		}

		for (int i = 0; i < index.length; i++) {
			index[i] = i;
		}
	}

}

public class Mp3Decode extends Decoder {
	Header header;
	Bitstream bitstream;
	javazoom.jl.decoder.Decoder decoder;
	RawOutputBuff output;

	public Mp3Decode(InputStream in) {
		try {
			decodeInputStream = in;

			bitstream = new Bitstream(decodeInputStream);

			Thread.sleep(1000);
			header = bitstream.readFrame();

			System.out.println(header);

			int channels = header.mode() == Header.SINGLE_CHANNEL ? 1 : 2;
			int rate = header.frequency();
			bitRate = header.bitrate();

			format = new AudioFormat(rate, 16, channels, true, false);
			decoder = new javazoom.jl.decoder.Decoder();
			rawBuff = new byte[Obuffer.OBUFFERSIZE * 2];
			output = new RawOutputBuff(channels, rawBuff);
			decoder.setOutputBuffer(output);
		} catch (BitstreamException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] decode() {
		try {
			decoder.decodeFrame(header, bitstream);
			rawSize = output.rawSize;
			decodeSize = header.framesize;
			bitstream.closeFrame();
			header = bitstream.readFrame();
		} catch (DecoderException e) {
			e.printStackTrace();
		} catch (BitstreamException e) {
			e.printStackTrace();
		}

		return rawBuff;
	}
}
