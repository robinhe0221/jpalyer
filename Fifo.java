import java.io.InputStream;
import java.io.OutputStream;

public class Fifo {
	byte[] data;
	int size;
	int read;
	int write;
	int used;

	InputStream fifoInputStream;
	OutputStream fifoOutputStream;

	public InputStream getInputStream() {
		return fifoInputStream;
	}

	public OutputStream getOutputStream() {
		return fifoOutputStream;
	}

	private byte readByte() {
		byte val;
		
		try {
			while (used <= 0) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}

		synchronized (this) {
			val = data[read];
			read++;
			read %= size;
			used--;
		}

		return val;
	}

	private void writeByte(byte val) {
		try {
			while (used >= size) {
				Thread.sleep(100);
			}

			synchronized (this) {
				data[write] = val;
				write++;
				write %= size;
				used++;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Fifo(int fifoSize) {
		size = fifoSize;
		read = write = 0;
		used = 0;
		data = new byte[size];

		fifoInputStream = new InputStream() {

			@Override
			public int read() {
				return readByte() & 0xFF;
			}
		};

		fifoOutputStream = new OutputStream() {

			@Override
			public void write(int b) {
				writeByte((byte) b);
			}
		};
	}
}
