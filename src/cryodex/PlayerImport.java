package cryodex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

public class PlayerImport {

	public static void importPlayers(){
		BufferedReader reader = null;
		try {
			JFileChooser fc = new JFileChooser();
			
			int returnVal = fc.showOpenDialog(Main.getInstance());

			if (returnVal == JFileChooser.APPROVE_OPTION) {
			    File file = fc.getSelectedFile();
			    
				reader = new BufferedReader(new FileReader(file));
				
				String line = reader.readLine();
				
				Map<String, Integer> headerMap = new HashMap<String, Integer>();
				
				String[] headers = line.split(",");
				int i = 0;
				for(String s : headers){
					headerMap.put(s, i);
					i++;
				}
				
				Integer firstName = headerMap.get("\"First Name\"");
				Integer lastName = headerMap.get("\"Last Name\"");
				Integer name = headerMap.get("\"Name\"");
				Integer emailAddress = headerMap.get("\"Email Address\"");
				
				if(firstName == null && lastName == null && name == null && emailAddress == null){
					firstName = headerMap.get("First Name");
					lastName = headerMap.get("Last Name");
					name = headerMap.get("Name");
					emailAddress = headerMap.get("Email Address");
				}
				
				List<Player> players = new ArrayList<Player>();

				line = reader.readLine();
				
				while(line != null){
					
					String[] playerLine = line.split(",");
					
					String playerName = null;
					
					if(firstName != null && lastName != null){
						playerName = playerLine[firstName] + " " + playerLine[lastName];
					} else if(name != null){
						playerName = playerLine[name];
					}
					
					playerName = playerName.replace("\"", "");
					
					Player p = new Player(playerName);
					
					if(emailAddress != null){
						String playerEmailAddress = playerLine[emailAddress];
						playerEmailAddress = playerEmailAddress.replace("\"", "");
						p.setEmail(playerEmailAddress);
					}
					
					players.add(p);

					line = reader.readLine();
				}

				Main.getInstance().getRegisterPanel().importPlayers(players);
			}
		} catch (Exception e) {
			try {
				if(reader != null){
					reader.close();
				}
			} catch (IOException e1) {}
			e.printStackTrace();
		}
	}
}
