import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class Lrc {
	char[] lrcContent;
	String[] lines;
	int size;
	int currenLine;

	public Lrc(String path) {
		try {
			path = path.replaceAll(".mp3", ".lrc");
			System.out.println(path);
			if (path.regionMatches(0, "http://", 0, 7)) {
				URL url;
				url = new URL(path);
				int port = url.getPort();
				if (port == -1)
					port = 80;

				Socket socket = new Socket(url.getHost(), port);
				PrintWriter out = new PrintWriter(socket.getOutputStream());
				String request = "GET " + url.getFile() + " HTTP/1.0\r\n\r\n";
				System.out.println(request);
				out.write(request);
				out.flush();

				DataInputStream in = new DataInputStream(
						socket.getInputStream());
				String line;

				while (true) {
					line = in.readLine();
					if (line.length() == 0)
						break;
					String[] strs = line.split(": ");
					if (strs[0].matches("Content-Length")) {
						size = Integer.valueOf(strs[1]);
					}
				}

				lrcContent = new char[size];
				InputStreamReader inputStreamReader = new InputStreamReader(
						socket.getInputStream());
				inputStreamReader.read(lrcContent);

			} else {
				File file = new File(path);
				FileReader fReader = new FileReader(file);

				size = (int) file.length();
				lrcContent = new char[size];
				fReader.read(lrcContent);
			}

			lines = String.valueOf(lrcContent).split("\n");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private long getTime(String time) {
		char[] timeBuff = new char[8];
		time.getChars(1, time.length() - 1, timeBuff, 0);

		if (timeBuff[0] < '0' || timeBuff[0] > '9')
			return -1;

		long m = (timeBuff[0] - '0') * 10 + (timeBuff[1] - '0');
		long s = (timeBuff[3] - '0') * 10 + (timeBuff[4] - '0');
		long ms = ((timeBuff[6] - '0') * 10 + (timeBuff[7] - '0')) * 10;

		return (m * 60 + s) * 1000 + ms;
	}

	private int getLine(long ms) {
		int n;
		long pre;
		long cur = -1;

		for (int i = 1; i < lines.length; i++) {
			String[] strs = lines[i].split("]");
			pre = cur;
			cur = getTime(strs[0]);

			if (ms > pre && ms < cur) {
				currenLine = i - 1;
				return currenLine;
			}
		}

		return lines.length - 1;
	}

	public String getLrcPart(int n) {
		String[] strs = lines[n].split("]");

		if (strs.length > 1)
			return strs[1];

		return null;
	}

	public String getLrcElement(long ms) {
		int n;

		n = getLine(ms);

		return getLrcPart(n);
	}

	public static void main(String[] args) {
		// Lrc lrc = new Lrc("http://127.0.0.1:80/GoodTime.lrc");
	}
}
