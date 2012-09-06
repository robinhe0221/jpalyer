import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Jplayer {
	InputStream dataInputStream;

	AudioFormat format;
	SourceDataLine line;

	Decoder decoder;
	byte[] rawBuff;
	final int rawBuffSize = 4096;

	final int WAVE_HEAD_SIZE = 44;

	public Jplayer() {

	}

	public void open(String path) throws IOException, LineUnavailableException {
		if (path.regionMatches(0, "http://", 0, 7)) {
			HttpLoad load = new HttpLoad(path);
			dataInputStream = load.getInputStream();
		} else {
			LocalLoad load = new LocalLoad(path);
			dataInputStream = load.getInputStream();
		}

		// parse wave or mp3
		byte[] id = new byte[4];
		int ret = dataInputStream.read(id);
		if (ret != 4) {
			System.exit(0);
		}

		if (id[0] == 'R' && id[1] == 'I' && id[2] == 'F' && id[3] == 'F') {
			// wave
			byte[] head = new byte[WAVE_HEAD_SIZE];

			dataInputStream.read(head, 4, WAVE_HEAD_SIZE - 4);
			int channels = (head[22] & 0xFF) | ((head[23] << 8) & 0xFF00);
			int rate = (head[24] & 0xFF) | (head[25] & 0xFF) << 8
					| (head[26] & 0xFF) << 16 | (head[27] & 0xFF) << 24;
			int bps = (head[34] & 0xFF) | ((head[35] << 8) & 0xFF00);

			format = new AudioFormat(rate, bps, channels, true, false);
		} else if (id[0] == 'I' && id[1] == 'D' && id[2] == '3') {
			// mp3
			byte[] head = new byte[10];
			dataInputStream.read(head, 4, 6);
			int tagSize;

			tagSize = (head[9] & 0x7F) | (head[8] & 0x7F) << 7
					| (head[7] & 0x7F) << 14 | (head[6] & 0x7F) << 21;

			// skip ID3 tag
			for (int i = 0; i < tagSize; i++) {
				dataInputStream.read();
			}

			decoder = new Mp3Decode(dataInputStream);
			format = decoder.getFormat();
		} else {
			System.exit(0);
		}

		line = AudioSystem.getSourceDataLine(format);
		line.open();
		line.start();
	}

	public void playback() throws IOException {

		while (true) {
			int len;

			if (decoder == null) {
				rawBuff = new byte[rawBuffSize];
				len = dataInputStream.read(rawBuff);
			} else {
				rawBuff = decoder.decode();
				len = decoder.getRawSize();
			}

			if (len <= 0)
				break;

			line.write(rawBuff, 0, len);
		}
	}

	public void stop() {
		line.stop();
	}

	public void close() {
		line.drain();
		line.close();
	}

	public static void main(String[] args) throws IOException,
			LineUnavailableException {
		Jplayer player = new Jplayer();
		
		player.open("http://192.168.1.109/GoodTime.mp3");

		player.playback();

		player.close();
	}
}
