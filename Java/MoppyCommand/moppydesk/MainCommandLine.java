package moppydesk;

import java.io.FileNotFoundException;
import java.util.HashMap;
import moppydesk.inputs.MoppySequencer;
import moppydesk.outputs.MoppyCOMBridge;
import moppydesk.outputs.MoppyPlayerOutput;
import moppydesk.outputs.ReceiverMarshaller;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class MainCommandLine {

	static MoppySequencer ms =null;
	static MoppyPlayerOutput mpo = null;
	static ReceiverMarshaller r = null;
	public static void main(String[] args) {    
	      CommandLineParser parser  = null;  
	      CommandLine       cmdLine = null;      
		
		
	      Options options = new Options();
	      
	      Option o = new Option("c", "channel", true, "Send channel data (1,2,3...) to the specified port (/dev/ttyUSB0, COM2...)");
	      o.setArgs(2);
	      o.setArgName("channelNumber:port");
	      o.setValueSeparator(':');
	      o.setRequired(true);
	      
	      Option o2 = new Option("i", "input", true, "Input file to play");
	      o2.setArgs(2);
	      o2.setArgName("filename");
	      o2.setRequired(true);
	      options.addOption(o);
	      options.addOption(o2);
	      
	      Option o3 = new Option("h", "help", false, "This help menu");
	      options.addOption(o3);
	      
      
	      try {  
	      
		  parser  = new BasicParser();  
		  cmdLine = parser.parse(options, args);  
		    
		  if (cmdLine.hasOption("h")){  
		      new HelpFormatter().printHelp(MainCommandLine.class.getCanonicalName(), options );  
		      showExample();
		      return;  
		  }  

		  HashMap<String, MoppyPlayerOutput> portMap = new HashMap<String, MoppyPlayerOutput>();
		  ms = new MoppySequencer();
		  
		  r = new ReceiverMarshaller();
		  
		  
		  String optionValues[] = cmdLine.getOptionValues('c');
		  for(int i =0; i < optionValues.length; i+=2){
		      String port = optionValues[i+1];
		      int channel = Integer.parseInt(optionValues[i]);
		      MoppyPlayerOutput mpo;
		      //Serial port
		      if(portMap.containsKey(port)){
			      mpo = portMap.get(port); 
		      }else{
			      mpo = new MoppyPlayerOutput(new MoppyCOMBridge(optionValues[i+1]));
			      portMap.put(optionValues[i+1], mpo);
		      }
		      //Set channel
		      r.setReceiver(channel,mpo);
		  }
		  
		  ms.setReceiver(r);
		  ms.loadFile(cmdLine.getOptionValue('i'));
		  ms.startSequencer();
		  ms.setTempo(1);
		  
		  
		  System.out.println("Playing music, press CTRL + C to stop");
		  Runtime.getRuntime().addShutdownHook(new Thread() {
			      @Override
			      public void run() {
			      System.out.println("W: interrupt received, stopping...");
				      ms.stopSequencer();
				      r.close();
			      }
		      });
		    
	      } catch (org.apache.commons.cli.ParseException ex){  
		  System.out.println(ex.getMessage());  
		  new HelpFormatter().printHelp(MainCommandLine.class.getCanonicalName(), options );
		  showExample();
	      } catch (java.lang.NumberFormatException ex){  
		  new HelpFormatter().printHelp(MainCommandLine.class.getCanonicalName(), options );
		  showExample();
	      }catch(FileNotFoundException ex){
	        ms.stopSequencer();
	        r.close();
		System.out.println("Error: file not found");
	      }catch (Exception ex) {
		  ex.printStackTrace();
	      }

	    
	}
	private static void showExample() {
		System.out.println("Example: java " +MainCommandLine.class.getCanonicalName()+" -c 1:/dev/ttyUSB0 -c 2:/dev/ttyUSB0 -c 3:/dev/ttyUSB1 -i music.mid");
		
	}
}
