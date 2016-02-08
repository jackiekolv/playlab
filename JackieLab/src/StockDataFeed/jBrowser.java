package StockDataFeed;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;


public class jBrowser {

	/**
	 * @param args
	 */
	
	public URL _url;
	public Proxy _proxy = Proxy.NO_PROXY;

	public jBrowser() {
//        _proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.244.1", 8888));
	}
	
	public jBrowser(URL url) {
		_url = url;
//        _proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.244.1", 8888));
	}
	
	public jBrowser(Proxy proxy) {
        _proxy = proxy;
	}
	
	public jBrowser(URL url, Proxy proxy) {
        _proxy = proxy;
        _url = url;
	}
	
	public BufferedReader connectURL() throws IOException {
//        URLConnection urlConn = url.openConnection(_proxy);
//        return new BufferedReader(new InputStreamReader(_url.openConnection(_proxy).getInputStream()));
        return new BufferedReader(new InputStreamReader(_url.openConnection().getInputStream()));
	}
	
	public Proxy getProxy() {
		return _proxy;
	}

	public void setProxy(Proxy proxy) {
		this._proxy = proxy;
	}

	public URL getUrl() {
		return _url;
	}

	public void setUrl(URL url) {
		this._url = url;
	}

	public static void main(String[] args) throws IOException {
		jBrowser browser = new jBrowser(new URL("https://www.google.co.th/"));
		String inputLine;
		BufferedReader in = browser.connectURL();
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
	}
	

}
