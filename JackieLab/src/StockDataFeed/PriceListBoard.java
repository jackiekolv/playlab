package StockDataFeed;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



public class PriceListBoard extends Thread {

	/**
	 * @param args
	 */
	
//	List<String> SymbolList;
	
	final int refresh_sec = 30;
	
	String [] SymbolList = {"BTS","SPA","CPF"/*,"KTB","SCB","ADVANC","TCAP","ROBINS","MBKET","ASP"*/};
	
	int open1 = 930;
	int close1 = 1245;
	
	int open2 = 1400;
	int close2 = 1645;
	
	
	jBrowser _browser = new jBrowser();
	
	DateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yy HH:mm:ss");
	
	DateFormat dateFormatForLog = new SimpleDateFormat("yyyyMMddHHmmss");
	
	DateFormat HHmm = new SimpleDateFormat("HHmm");
	
	boolean isFirstTime = true;
	
	public PriceListBoard() {
//		initLog();
	}
	
	final int refresh_millisec = refresh_sec*1000;
	

    public boolean isOpenTime() {
		
		int day_of_week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		boolean isWeekDay = (day_of_week != Calendar.SUNDAY && day_of_week != Calendar.SATURDAY);
		if(!isWeekDay) return false;
		int ctime = Integer.parseInt(HHmm.format(Calendar.getInstance().getTime()));		
		return isWeekDay && (ctime > open1 && ctime < close1) || (ctime > open2 && ctime < close2);
    }
	
	@Override
    public void run() {
		while (true) {
			
			try {
				
				String display_date = dateFormat.format(Calendar.getInstance().getTime());
				
				if(isOpenTime() || isFirstTime){
					String str_display = "\n\n" + display_date + " \n" + getNewData();
					System.out.print(str_display);
//					logger.info(str_display);
				}
				//System.out.print("====================================================================================");
				
				isFirstTime = false;
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				Thread.sleep(refresh_millisec);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
	
	public String getDataFromXMLelement(String inputLine) {
		return inputLine.split(">", 2)[1].split("<", 2)[0];
	}
	
	public String getHeadData() throws IOException {
		String resultStr = "";
		_browser.setUrl(new URL("http://marketdata.set.or.th/mkt/sectorquotation.do?sector=SETHD"));
		String inputLine;
		BufferedReader in = _browser.connectURL();
		while ((inputLine = in.readLine())!=null){

			// Find Last SET Index
			if(inputLine.contains("     SET")){
				while ((inputLine = in.readLine())!=null){
					if(inputLine.contains("<td>")){
						resultStr += "SET     " + getDataFromXMLelement(inputLine);
						break;
					}
				}
				while ((inputLine = in.readLine())!=null){
					if(inputLine.contains("<td>")){
						inputLine = inputLine.replace("<td>", "").replace("<\td>", "");
						String data = getDataFromXMLelement(inputLine);
						String upDown = "";
						if(data.startsWith("+")) upDown = "↑";
						else if(data.startsWith("-")) upDown = "↓";
						
						resultStr += " " + data + " " + upDown;
						break;
					}
				}
				while ((inputLine = in.readLine())!=null){
					if(inputLine.contains("<td>")){
						inputLine = inputLine.replace("<td>", "").replace("<\td>", "");
						resultStr += " (" + getDataFromXMLelement(inputLine) + "%)";
						break;
					}
				}
				while ((inputLine = in.readLine())!=null){
					if(inputLine.contains("<td>")){
						resultStr += " H: " + getDataFromXMLelement(inputLine) + " ";
						break;
					}
				}
				while ((inputLine = in.readLine())!=null){
					if(inputLine.contains("<td>")){
						resultStr += " L: " + getDataFromXMLelement(inputLine) + " ";
						break;
					}
				}
				while ((inputLine = in.readLine())!=null){
					if(inputLine.contains("<td>")){
						//resultStr += " V('000 Shares): " + getDataFromXMLelement(inputLine) + " ";
						break;
					}
				}
				while ((inputLine = in.readLine())!=null){
					if(inputLine.contains("<td>")){
						resultStr += " V: " + getDataFromXMLelement(inputLine) + " MB";
						break;
					}
				}
				break;
			}
		}
		return resultStr;
	}

	public String getNewData() throws IOException {
		String resultStr = getHeadData() + "";
//		jBrowser browser = new jBrowser();
		
		try {

			for (int i = 0; i < SymbolList.length; i++) {
				resultStr += "\n\t";
				resultStr += SymbolList[i] + " \t";
				_browser.setUrl(new URL("http://marketdata.set.or.th/mkt/stockquotation.do?symbol="+SymbolList[i]));
				String inputLine;
				BufferedReader in = _browser.connectURL();
				
				String bid_offer = "";
				
				// skip first 45900 of unnecessary character.
				in.skip(45800);
				
				while ( (inputLine = in.readLine()) != null ){
					
					// double last, prior, open, high, low;
					
					// Find Last Price
					if(inputLine.contains("<td>Last</td>")){
						int line = 1;
						while ((inputLine = in.readLine())!=null){
//							if(inputLine.contains("</font>")){
//								resultStr += inputLine.trim().split("<", 2)[0] + "\t";
//								break;
//							}
							if(++line == 3){
								if(inputLine.contains("</i>")) {
									resultStr += inputLine.trim().split("</i>", 2)[1];
								} else {
									resultStr += inputLine.trim();
								}
								
								resultStr += "\t";
								
								break;
							}
						}
					}

					// Find %Change
					else if(inputLine.contains("<td>%Change</td>")){
						while ((inputLine = in.readLine())!=null){
							// If %Change != 0.00%
							if(inputLine.contains("</font>")){
								String data = inputLine.split(">", 2)[1].split("<", 2)[0];
								String upDown = "";
								if(data.startsWith("+")) upDown = "↑";
								else if(data.startsWith("-")) upDown = "↓";
								resultStr += data + "% " + upDown + "\t";
//								resultStr += inputLine.split(">", 1)[1].split("<", 1)[0];
//								resultStr += inputLine.trim().replace("</font>", "");
								break;
							}
							// If %Change = 0.00%
							else if(inputLine.trim().equals("-")){
								resultStr += " 0.00" + "% " + "=" + "\t";
								break;
							}
						}
						// break if last data is retrieved.
//						break;
					}
					
					// Find Prior Price
					else if(inputLine.contains("<td>Prior</td>")){
						while ((inputLine = in.readLine())!=null){
							if(inputLine.contains("</td>")){
								resultStr += "P: " + inputLine.split(">", 2)[1].split("<", 2)[0] + " \t";
								break;
							}
						}
					}
					
					// Find Open Price
					else if(inputLine.contains("<td>Open</td>")){
						while ((inputLine = in.readLine())!=null){
							if(inputLine.contains("</td>")){
								resultStr += "O: " + inputLine.split(">", 2)[1].split("<", 2)[0] + " \t";
								break;
							}
						}
					}
					
					// Find High Price
					else if(inputLine.contains("<td>High</td>")){
						while ((inputLine = in.readLine())!=null){
							if(inputLine.contains("</td>")){
								resultStr += "H: " + inputLine.split(">", 2)[1].split("<", 2)[0] + " \t";
								break;
							}
						}
					}
					
					// Find Low Price
					else if(inputLine.contains("<td>Low</td>")){
						while ((inputLine = in.readLine())!=null){
							if(inputLine.contains("</td>")){
								resultStr += "L: " + inputLine.split(">", 2)[1].split("<", 2)[0] + " \t";
								break;
							}
						}
						
						
						// skip first 45980 of unnecessary character.
//						in.skip(2400);
						
						
					}
					
					
					// Find Bid Price/Volume
					else if(inputLine.contains("<td>Bid Price&nbsp;/&nbsp;Volume (Shares)</td>")){
						while ((inputLine = in.readLine())!=null){
							if(inputLine.contains("&nbsp;/&nbsp;")){
								String price = "";
								String volume = "";
								price = inputLine.replace("&nbsp;/&nbsp;", "").trim();
								inputLine = in.readLine();
								volume = inputLine.trim();
								volume = fixLengthString(volume, 12);
								price = fixLengthString(price, 9);
								//"BTS	3,277,600   9.20     "
								bid_offer += SymbolList[i] + "\t" + volume + price;
								break;
							}
						}
					}
					// Find Offer Price/Volume
					else if(inputLine.contains("<td>Offer Price&nbsp;/&nbsp;Volume (Shares)</td>")){
						while ((inputLine = in.readLine())!=null){
							if(inputLine.contains("&nbsp;/&nbsp;")){
								bid_offer += inputLine.replace("&nbsp;/&nbsp;", "").trim() + "\t";
								inputLine = in.readLine();
								bid_offer += inputLine.trim();
								break;
							}
						}
						break;
					}
					
				}
				
				resultStr += bid_offer;
				
				in.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return resultStr;
		}
		
		return resultStr;
	}
	
	public String fixLengthString(String str, int length) {
		while(str.length() < length) str += " ";
		return str;
	}
	
    Logger logger = Logger.getLogger("MyLog");  
    FileHandler fh;  
	
	public void initLog() {  

	    try {
	    	
	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler("D:/MyConsole/price.log", 1000000, 100);  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);
	        
	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }

	}
	
	public static void main(String[] args) {
		PriceListBoard cs = new PriceListBoard();
		cs.start();
	}

}
