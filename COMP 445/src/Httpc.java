import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Httpc {

	private final static String USER_AGENT = "Mozilla/5.0";
	private final static String ACCEPT_LANG = "en-US,en;q=0.5";
	private static boolean isV = false;
	private static boolean isH = false;
	private static boolean isD = false;
	private static boolean isF = false;
	private static boolean isGet = false;
	private static boolean isPost = false;
	private static HashMap<String, String> headerArgument = new <String, String>HashMap();
	private static HashMap<String, String> inlineArg = new <String, String>HashMap();

	private static String url = "";
	// CMD Option

	public static void main(String[] args) throws Exception {

		System.out.println("Hello user, enter a cURL command:");
		Scanner in = new Scanner(System.in);
		String command = in.nextLine();
		String[] word = command.split("\\s");

		for (int i = 0; i < word.length; i++) {
			System.out.println(word[i]);
			if (word[i].equals("-v")) {
				isV = true;
			}
			if (word[i].equals("-h")) {
				isH = true;
				int nextI = i + 1;
				String[] kv = word[nextI].split(":");
				headerArgument.put(kv[0], kv[1]);
			}
			if (word[i].contains("-d")) {
				isD = true;
				int nextI = i + 1;
				String[] kv = word[nextI].split(":");
				inlineArg.put(kv[0], kv[1]);
			}
			if (word[i].equals("-f")) {
				if (isD) {
					System.out.println("You cannot have -d and -f at the same time.");
					System.exit(0);
				} else {
					isF = true;
				}
			}
			if (word[i].contains("http://")) {
				url = word[i];
			}
			if (word[i].contains("get")) {
				isGet = true;
			}
			if (word[i].contains("post")) {
				isPost = true;
			}
		}		
		if (command.contains("help") && !command.contains("get") && !command.contains("post")) {
			System.out.println("" + "httpc is a curl-like application but supports HTTP protocol only.\r\n"
					+ "Usage:\r\n" + " httpc command [arguments]\r\n" + "The commands are:\r\n"
					+ " get executes a HTTP GET request and prints the response.\r\n"
					+ " post executes a HTTP POST request and prints the response.\r\n"
					+ " help prints this screen.\r\n"
					+ "Use \"httpc help [command]\" for more information about a command.\r\n" + "");

		} else if (command.contains("help") && command.contains("get") && !command.contains("post")) {
			System.out.println("usage: httpc get [-v] [-h key:value] URL\r\n"
					+ "Get executes a HTTP GET request for a given URL.\r\n"
					+ " -v Prints the detail of the response such as protocol, status,\r\n" + "and headers.\r\n"
					+ " -h key:value Associates headers to HTTP Request with the format\r\n" + "'key:value'.\r\n" + "");

		} else if (command.contains("help") && !command.contains("get") && command.contains("post")) {

			System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\r\n"
					+ "Post executes a HTTP POST request for a given URL with inline data or from\r\n" + "file.\r\n"
					+ " -v Prints the detail of the response such as protocol, status,\r\n" + "and headers.\r\n"
					+ " -h key:value Associates headers to HTTP Request with the format\r\n" + "'key:value'.\r\n"
					+ " -d string Associates an inline data to the body HTTP POST request.\r\n"
					+ " -f file Associates the content of a file to the body HTTP POST\r\n" + "request.\r\n"
					+ "Either [-d] or [-f] can be used but not both.");

		} else {
			if (isGet) {
				getMethod(url, isV, headerArgument, "");
			} else if (isPost) {
				

				String content = "{\"Assignment\":1}";
				byte[] bytes = doPostMethod(url, content.getBytes("UTF-8"), headerArgument);
				String doc2=new String(bytes, "UTF-8");
				System.out.println(doc2);
				//postMethod(url, isV, headerArgument, inlineArg);
			}
		}
	}
	
	public static byte[] doPostMethod(String urlstr, byte[] content,Map<String, String> hArgs) throws IOException {
        InputStream is = null;
        byte[] data = null;
        ByteArrayOutputStream baos = null;
        try {
            final URL url = new URL("http://httpbin.org/post");
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            
            if (hArgs == null) {
            	connection.setRequestProperty("User-Agent", USER_AGENT);
            	connection.setRequestProperty("Accept-Language", ACCEPT_LANG);
    		} else {
    			for (String key : hArgs.keySet()) {
    				connection.setRequestProperty(key, hArgs.get(key));
    			}
    		}
            
            connection.setRequestProperty("Content-Length", String.valueOf(content.length));            
            OutputStream os = null;
            os = connection.getOutputStream();
            os.write(content);
            os.close();
            is = connection.getInputStream();            
            final byte[] buffer = new byte[2 * 1024];
            baos = new ByteArrayOutputStream();
            int n;
            while ((n = is.read(buffer)) >= 0) {
                baos.write(buffer, 0, n);
            }
            data = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            baos.close();
        }
        return data;
    }
	private static void getMethod(String urlString, Boolean verbose, Map<String, String> hArgs, String getParameter)
			throws Exception {
		String url = urlString.concat(getParameter);
		System.out.println("\nSending 'GET' request to URL : " + url);

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setInstanceFollowRedirects(true);
		HttpURLConnection.setFollowRedirects(true);

		// Add Request Header Details
		if (hArgs == null) {
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", ACCEPT_LANG);
		} else {
			for (String key : hArgs.keySet()) {
				con.setRequestProperty(key, hArgs.get(key));
			}
		}

		// isRedirect(con, verbose); 
		isVerbose(con, verbose); 

		// Display output
		outputResponse(con, url, con.getResponseCode());
	}

	private static void outputResponse(HttpURLConnection con, String url, int responseCode) throws IOException {

		InputStream in = con.getInputStream();

		byte[] buffer = new byte[256];
		int bytesRead = 0;

		while (true) {
			bytesRead = in.read(buffer);
			if (bytesRead == -1)
				break;
			System.out.write(buffer, 0, bytesRead);
		}
		System.out.close();

	}

	private static void isVerbose(HttpURLConnection connection, Boolean verbose) {
		// For verbose option
		Map<String, List<String>> headers = connection.getHeaderFields();
		Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
		if (verbose == true) {
			System.out.println();
			System.out.println("Full Header: ");

			for (Map.Entry<String, List<String>> entry : entrySet) {
				String headerName = entry.getKey();
				List<String> headerValues = entry.getValue();
				for (String value : headerValues) {
					System.out.println(headerName + " : " + value);
				}
			}

			System.out.println("End of Full Header");
			System.out.println();

		}
	}	
}
