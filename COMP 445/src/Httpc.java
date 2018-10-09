import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Httpc {

	private static OutputStreamWriter osw;
	private static String key = "", value = "";
	private static boolean isGet = false, isPost = false;
	private static LinkedHashMap<String, String> inlineArg = new <String, String>LinkedHashMap();
	private static boolean isV = false;

	public static void main(String[] args) throws Exception {
		boolean nextIsData = false;
		String data = "";
		String url = "";
		String param = "";

		Scanner in = new Scanner(System.in);
		String command="";
		for(int i = 0; i<args.length;i++) {
			command +=" "+args[i];
		}
		String[] word;
		if (command.contains("'")) {
			word = command.split("'");
		} else {
			word = command.split("\\s");
		}
		for (int i = 0; i < word.length; i++) {

			if (word[i].contains("get")) {
				isGet = true;
			}
			if (word[i].contains("post")) {
				isPost = true;
			}
			if (word[i].contains("-v")) {
				isV = true;
			}
			if (command.contains("help") && !command.contains("get") && !command.contains("post")) {
				System.out.println("" + "httpc is a curl-like application but supports HTTP protocol only.\r\n"
						+ "Usage:\r\n" + " httpc command [arguments]\r\n" + "The commands are:\r\n"
						+ " get executes a HTTP GET request and prints the response.\r\n"
						+ " post executes a HTTP POST request and prints the response.\r\n"
						+ " help prints this screen.\r\n"
						+ "Use \"httpc help [command]\" for more information about a command.\r\n" + "");
				System.exit(0);

			} else if (command.contains("help") && command.contains("get") && !command.contains("post")) {
				System.out.println("usage: httpc get [-v] [-h key:value] URL\r\n"
						+ "Get executes a HTTP GET request for a given URL.\r\n"
						+ " -v Prints the detail of the response such as protocol, status,\r\n" + "and headers.\r\n"
						+ " -h key:value Associates headers to HTTP Request with the format\r\n" + "'key:value'.\r\n"
						+ "");
				System.exit(0);

			} else if (command.contains("help") && !command.contains("get") && command.contains("post")) {

				System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\r\n"
						+ "Post executes a HTTP POST request for a given URL with inline data or from\r\n" + "file.\r\n"
						+ " -v Prints the detail of the response such as protocol, status,\r\n" + "and headers.\r\n"
						+ " -h key:value Associates headers to HTTP Request with the format\r\n" + "'key:value'.\r\n"
						+ " -d string Associates an inline data to the body HTTP POST request.\r\n"
						+ " -f file Associates the content of a file to the body HTTP POST\r\n" + "request.\r\n"
						+ "Either [-d] or [-f] can be used but not both.");
				System.exit(0);

			}
			if (isPost) {

				if (!nextIsData) {
					String word2[] = word[i].split("\\s");
					for (int i2 = 0; i2 < word2.length; i2++) {
						if (word2[i2].equals("-h") || word2[i2].equals("--h")) {
							int nextI = i2 + 1;
							String[] kv = word2[nextI].split(":");
							key = kv[0];
							value = kv[1];
						}
						if (word2[i2].equals("-d") || word2[i2].equals("--d")) {
							nextIsData = true;
							continue;
						}
					}
				} else {
					if (word[i].contains("http://")) {
						nextIsData = false;
					} else {
						data = data.concat(word[i].replaceAll("[\'{}]", ""));
					}
				}

			}
			if (isGet && word[i].contains("http://")) {
				url = word[i].substring(7, 18);
				param = word[i].substring(18, word[i].length());
			}
			if (isPost && word[i].contains("http://")) {
				String temp = word[i].replaceAll("\\s", "");
				url = temp.substring(7, 18);
			}
		}

		if (isGet) {

			getRequest(url, param);
		} else if (isPost) {
			postRequest(url, data);
		}

	}

	public static void postRequest(String urlstr, String data) throws IOException {

		try {
			Socket socket = new Socket(urlstr, 80);
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println("POST /post HTTP/1.0\r\n" 
					+ "Host: "+ urlstr + "\r\n" 
					+ key + ": " + value + "\r\n"
					+ "Content-Length: " + data.length()+ "\r\n" 
					+ "\r\n" + data);	
			out.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			String inputLine;

			if (isV) {
				while ((inputLine = in.readLine()) != null) {
					if (inputLine.equals("{")) {
						System.exit(0);
					}
					System.out.println(inputLine);

				}
			} else {
				while ((inputLine = in.readLine()) != null) {
					System.out.println(inputLine);

				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void getRequest(String urlstr, String getParameter) throws Exception {
		try {
			Socket socket = new Socket(urlstr, 80);
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println("GET " + getParameter + " " + "HTTP/1.0\r\n" + "Host:" + urlstr + "\r\n\r\n");
			out.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			String inputLine;
			if (isV) {
				while ((inputLine = in.readLine()) != null) {
					if (inputLine.equals("{")) {
						System.exit(0);
					}
					System.out.println(inputLine);

				}
			} else {
				while ((inputLine = in.readLine()) != null) {
					System.out.println(inputLine);

				}
			}

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
