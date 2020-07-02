package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ViewController implements Initializable {
	private static double initx; //
	private static double inity; //
	private static int height; //
	private static int width; //
	public static String path; //
	private static Scene initialScene, View; //
	private static double offSetX, offSetY, zoomlvl; //
	private double moveH, moveW; //
    
	private Stage stageDetails = new Stage();//
	
	private ImageFile img; //image a afficher
	private ListFiles listOfFiles; //liste des images
	private Setting settings = new Setting(); //

    @FXML
    private BorderPane PaneImage; //
	@FXML
	private Slider zoomLvl;// bar de zoom
	@FXML
	private Label value;//valeur du zoom
	@FXML
	private ImageView Cadre; //
	@FXML
	private BorderPane ImageWindow; //
	
	//va recuperer l'image et la liste des image de la precedente fenetre
	public void setImageViewWindow(ImageFile image, ListFiles list) {
		this.img = image;  //image
		this.listOfFiles = list; //liste des images
		setView(); //creer la vue de l'image
	}
	
	private void setView() {
		path = img.getLink(); //recupere le chemin de l'image
		
		//try au cas ou l'image ne serai pas charge pour quel que raison que ce soit
		try{
			final Image source = new Image(new FileInputStream(path)); //recupere l'image

			Cadre.setImage(source); //affiche l'image

			Cadre.setPreserveRatio(true); //ne deforme pas l'image
			height = (int) source.getHeight();  //hauteur de l'image
			width = (int) source.getWidth(); //largeur de l'image

			zoomLvl.setMax(4); //zoom max
			zoomLvl.setMin(1); //zoom min
			zoomLvl.setValue(1);

			offSetX = width / 2;
			offSetY = height / 2;

			//zoom
			zoomLvl.valueProperty().addListener(e -> {
				zoomlvl = zoomLvl.getValue();
				double newValue = (double) ((int) (zoomlvl * 10)) / 10;
				value.setText(newValue + "");
				if (offSetX < (width / newValue) / 2) {
					offSetX = (width / newValue) / 2;
				}
				if (offSetX > width - ((width / newValue) / 2)) {
					offSetX = width - ((width / newValue) / 2);
				} 
				if (offSetY < (height / newValue) / 2) {
					offSetY = (height / newValue) / 2;
				}
				if (offSetY > height - ((height / newValue) / 2)) {
					offSetY = height - ((height / newValue) / 2);
				}
				moveH = offSetX;
				moveW = height - offSetY;
				Cadre.setViewport(new Rectangle2D(offSetX - ((width / newValue) / 2),
						offSetY - ((height / newValue) / 2), width / newValue, height / newValue));
			});

			//change le curseur
			Cadre.setCursor(Cursor.OPEN_HAND);
			//clique de souris sur l'image
			Cadre.setOnMousePressed(e -> {
				initx = e.getSceneX();
				inity = e.getSceneY();
				Cadre.setCursor(Cursor.CLOSED_HAND);
			});
			//relacher souris
			Cadre.setOnMouseReleased(e -> {
				Cadre.setCursor(Cursor.OPEN_HAND);
			});
			//mouvement de la souris
			Cadre.setOnMouseDragged(e -> {
				moveH = moveH + (initx - e.getSceneX());
				if(moveH <= 0) {
					moveH = 0.1;
				}else if(moveH >= width) {
					moveH = width-1.0;
				}
				offSetX = moveH;
				zoomlvl = zoomLvl.getValue();
				double newValueH = (double) ((int) (zoomlvl * 10)) / 10;
				if (offSetX < (width / newValueH) / 2) {
					offSetX = (width / newValueH) / 2;
				}
				if (offSetX > width - ((width / newValueH) / 2)) {
					offSetX = width - ((width / newValueH) / 2);
				}

				Cadre.setViewport(new Rectangle2D(offSetX - ((width / newValueH) / 2),
						offSetY - ((height / newValueH) / 2), width / newValueH, height / newValueH));
				
				moveW = moveW - (inity - e.getSceneY());
				if(moveW <= 0) {
					moveW = 0.1;
				}else if(moveW >= height) {
					moveW = height-1.0;
				}
				offSetY = height - moveW;
				zoomlvl = zoomLvl.getValue();
				double newValueW = (double) ((int) (zoomlvl * 10)) / 10;
				value.setText(newValueW + "");
				if (offSetY < (height / newValueW) / 2) {
					offSetY = (height / newValueW) / 2;
				}
				if (offSetY > height - ((height / newValueW) / 2)) {
					offSetY = height - ((height / newValueW) / 2);
				}
				Cadre.setViewport(new Rectangle2D(offSetX - ((width / newValueW) / 2),
						offSetY - ((height / newValueW) / 2), width / newValueW, height / newValueW));
				
				
				initx = e.getSceneX();
				inity = e.getSceneY();
			});
			//mollette de la souris
			Cadre.setOnScroll((ScrollEvent event) ->{
				if(event.getDeltaY() > 0) {
					zoomLvl.setValue(zoomLvl.getValue() + 0.1);
				}
				if(event.getDeltaY() < 0) {
					zoomLvl.setValue(zoomLvl.getValue() - 0.1);
				}
				
			});
		} catch (FileNotFoundException ex) {

		}
		
		//changement de taille de la fenetre
		PaneImage.widthProperty().addListener((obs, oldVal, newVal) -> {
			Cadre.setFitWidth((double) newVal); //refixe la taille de l'image pour qu'elle remplisse tout la largeur ou hauteur (depend de sont ratio)
		});
		PaneImage.heightProperty().addListener((obs, oldVal, newVal) -> {
			Cadre.setFitHeight((double) newVal); //refixe la taille de l'image pour qu'elle remplisse tout la largeur ou hauteur (depend de sont ratio)
		});
	}

	//details sur l'image demander par l'utilisateur 
    @FXML
    private void LoadDetails(ActionEvent event) {
    	 try {
             //
             FXMLLoader loader = new FXMLLoader(getClass().getResource("ImageDetails.fxml"));
             Parent root = loader.load();
              
             //recupere le controler
             DetailsControler d = loader.getController();
  
             //icone
 			String currentDirectory = System.getProperty("user.dir");
 			File f = new File(currentDirectory);
 			f.getParentFile().getName();	
 			File fi = new File(f.getAbsolutePath() + "\\Ressource\\Icon\\IconDetails.png");
 			String localUrl = fi.toURI().toURL().toString();
 			Image icon = new Image(localUrl);
 			stageDetails.getIcons().add(icon);
             
             //affiche         
 			stageDetails.setScene(new Scene(root));
            stageDetails.setResizable(false);
            stageDetails.setTitle("Details : " + img.getLink().substring(img.getLink().lastIndexOf("\\")+1));//met le nom de l'image en nom de fenetre;
            stageDetails.show();

            //passe l'image
             d.getImageFile(img);
 	    } catch(Exception e) {
 	        e.printStackTrace();
 	    }
    }
    
    //demande de l'utilisateur d'afficher l'image suivante
    @FXML
    void ShowNext(ActionEvent event) {
    	int indexActual = -1;
    	indexActual = listOfFiles.getListOfFiles().indexOf(img);
    	if(indexActual != -1 && indexActual < listOfFiles.getListOfFiles().size() -1) {
    		img = listOfFiles.getListOfFiles().get(indexActual + 1);
    	}else {
    		img = listOfFiles.getListOfFiles().get(0);
    	}
    	setView();
    }

    //demande de l'utilisateur d'afficher l'image precedente
    @FXML
    void ShowPrevious(ActionEvent event) {
    	int indexActual = -1;
    	indexActual = listOfFiles.getListOfFiles().indexOf(img);
    	if(indexActual > 0) {
    		img = listOfFiles.getListOfFiles().get(indexActual -1);
    	}else {
    		img = listOfFiles.getListOfFiles().get(listOfFiles.getListOfFiles().size() -1);
    	}
    	setView();
    }
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
}
