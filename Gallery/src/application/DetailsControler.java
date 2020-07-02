package application;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class DetailsControler implements Initializable{
	private ImageFile img; //img

    @FXML
    private TextField dimension;
    @FXML
    private TextField color;
    @FXML
    private TextField size;
    @FXML
    private TextField directory;
    @FXML
    private TextField name;
    
    //recupere l'image et affiche les dtails
    public void getImageFile(ImageFile i) {
    	name.setText(i.getImageName()); //nom de l'image
    	
    	directory.setText(i.getImagePath()); //repertoire
    	
    	size.setText(i.getSize() + " bytes");//taille
    	
    	//erreur si fichier non trouver donc try/catch
		File fi = new File(i.getLink());
		String localUrl = null;
		try {
			
			//chemin de l'image
			localUrl = fi.toURI().toURL().toString();
			Image nouvImage = new Image(localUrl);
			
			//dimension de l'image
			int h, w;
			h = (int) nouvImage.getHeight(); //recupere la hauteur 
			w = (int) nouvImage.getWidth();
			
			dimension.setText(String.valueOf(w + " x " + h));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dimension.setText("Error : Unknown dimension"); //message d'erreur dans la fenetre
		}
		
		//erreur de la fonction de calcul de la couleur
		try {
			color.setText(i.CalculateDominantColour()); //affiche la couleur moyenne
		}catch(Exception e) {
			color.setText("Error : Unknown color");  //message d'erreur dans la fenetre
		}
    }
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}

}
