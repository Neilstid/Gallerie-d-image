package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Setting {
	private String sLink; //memorie le lien du fichier
	private String sImagePath; //memorise le lien de l'image
	
	//constructor
	public Setting() {
		Properties properties = new Properties(); //nouvelle propriete
		
		//fichier introuvable
		try {
			properties.loadFromXML(new FileInputStream("config.xml")); //ouvre le fichier
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		sLink = properties.getProperty("Link", ""); //recupere le lien du dossier
		sImagePath = properties.getProperty("ImagePath", ""); //recupere le lien de l'image
	}
	
	private void saveSetting(String reference, String value) {
		Properties props = new Properties();
		
		//fichier introuvable
		try {
			props.loadFromXML(new FileInputStream("config.xml")); //charge les parametre 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Properties propClone = new Properties(); //Nouvelle propriete pour stocké les anciennes
		propClone = (Properties) props.clone();//clone les anciens parametre pour etre sur de ne rien perdre
		
		propClone.setProperty(reference, value); //On remplace ou on set la valeur que l'on souhaite
		
		//fichier introuvable
		try {
			//creer un fichier config.xml pour stocker les valeurs
			File configFile = new File("config.xml"); 
			FileOutputStream out = new FileOutputStream(configFile);
			propClone.storeToXML(out, "Configuration"); //met les parametre dans config.xml
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//change la valeur du lien du dossier
	public void setSLink(String value) {
		sLink = value;
		saveSetting("Link", value);
	}
	
	//recupere la valeur du lien du dossier
	public String getSLink() {
		return sLink;
	}
	
	//change la valeur du lien de l'image
	public void setSImagePath(String value) {
		sImagePath = value;
		saveSetting("ImagePath", value);
	}
	
	//recupere la valeur du lien de l'image
	public String getSImagePath() {
		return sImagePath;
	}
}
