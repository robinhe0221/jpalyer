import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

public class HttpLoad {
	InputStream httpIn;
	OutputStream fifoOut;
	InputStream fifoIn;
	long size;

	final static int HTTP_PORT = 80;

	Socket socket;

	HashMap<String, String> hm;

	final static int FIFO_SIZE = 1024 * 1024;
	final static int _1K = 1024;

	class Load extends Thread {
		@Override
		public void run() {
			super.run();

			try {
				while (true) {
					byte[] buff = new byte[_1K];
					int ret;
					ret = httpIn.read(buff);
					if (ret <= 0)
						break;
					fifoOut.write(buff, 0, ret);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public HttpLoad(String path) {
		Fifo fifo = new Fifo(FIFO_SIZE);
		fifoOut = fifo.getOutputStream();
		fifoIn = fifo.getInputStream();
		URL url;
		try {
			url = new URL(path);
			int port = url.getPort();
			if (port < 0)
				port = HTTP_PORT;

			socket = new Socket(url.getHost(), port);

			httpIn = socket.getInputStream();
			PrintWriter out = new PrintWriter(socket.getOutputStream());

			out.write("GET /" + url.getFile() + " HTTP/1.0\r\n\r\n");
			out.flush();

			DataInputStream din = new DataInputStream(httpIn);

			hm = new HashMap<String, String>();
			String line;
			while (true) {
				line = din.readLine();
				System.out.println(line);
				if (line.length() == 0)
					break;
				String[] strs = line.split(": ");
				if (strs.length > 1) {
					hm.put(strs[0], strs[1]);
				}
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		size = Long.valueOf(hm.get("Content-Length"));
		Load loadTread = new Load();
		loadTread.start();
	}

	public InputStream getInputStream() {
		return fifoIn;
	}
}
