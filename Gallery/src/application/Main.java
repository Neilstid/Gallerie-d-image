package application;
	
import java.io.File;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("MainApp.fxml"));
			Scene scene = new Scene(root,600,600);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			
			primaryStage.setTitle("Gallery"); //nom de la fenetre
			
			//icon
			String currentDirectory = System.getProperty("user.dir");
			File f = new File(currentDirectory);
			f.getParentFile().getName();	
			File fi = new File(f.getAbsolutePath() + "\\Ressource\\Icon\\IconGallery.png");
			String localUrl = fi.toURI().toURL().toString();
			Image icon = new Image(localUrl);
			primaryStage.getIcons().add(icon);
			
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
