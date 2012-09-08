import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.text.Position.Bias;

public class Jplayer {
	InputStream dataInputStream;

	AudioFormat format;
	SourceDataLine line;

	Lrc lrc;

	Decoder decoder;
	byte[] rawBuff;
	final int rawBuffSize = 4096;

	final int WAVE_HEAD_SIZE = 44;

	boolean isPlaying = false;

	long audioFileSize = 0;
	long readFileSize = 0;
	long totalTime;
	long currenTime;

	public Jplayer() {

	}

	public void updateProgressBar(long current, long total) {

	}

	public void open(String path) throws IOException, LineUnavailableException {
		if (path.regionMatches(0, "http://", 0, 7)) {
			HttpLoad load = new HttpLoad(path);
			dataInputStream = load.getInputStream();
			audioFileSize = load.size;
		} else {
			LocalLoad load = new LocalLoad(path);
			dataInputStream = load.getInputStream();
			audioFileSize = load.size;
		}

		lrc = new Lrc(path);
		if (lrc.size == 0)
			lrc = null;

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

	public void start() {
		Thread playingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					playback();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		playingThread.start();
	}

	public void playback() throws IOException {
		readFileSize = 0;
		isPlaying = true;

		while (isPlaying) {
			int len;

			if (decoder == null) {
				rawBuff = new byte[rawBuffSize];
				len = dataInputStream.read(rawBuff);
				readFileSize += len;
				currenTime = readFileSize
						/ (long) (format.getSampleRate() * format.getChannels() * format
								.getFrameSize());
			} else {
				rawBuff = decoder.decode();
				len = decoder.getRawSize();
				readFileSize += decoder.decodeSize;
				currenTime = readFileSize * 8 * 1000 / decoder.bitRate;
			}

			if (len <= 0)
				break;

			showLrc(currenTime);
			updateProgressBar(readFileSize, audioFileSize);

			line.write(rawBuff, 0, len);
		}
	}

	public void stop() {
		isPlaying = false;
		line.stop();
	}

	public void close() {
		line.drain();
		line.close();
	}

	int lrcLine = -1;

	public void showLrc(long ms) {
		// TODO Auto-generated method stub
		if (lrc == null)
			return;

		String lrcString = lrc.getLrcElement(ms);

		if (lrcLine == lrc.currenLine)
			return;

		lrcLine = lrc.currenLine;
		if (lrcString == null) {
			System.out.println();
		} else {
			System.out.println(lrcString);
		}
	}

	public static void main(String[] args) throws IOException,
			LineUnavailableException {
		Jplayer player = new Jplayer();

		player.open("http://127.0.0.1/GoodTime.mp3");

		player.playback();

		player.close();
	}
}
