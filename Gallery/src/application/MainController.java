package application;

import java.io.File;
import java.lang.Object;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;

public class MainController implements Initializable {

	@FXML
	private TextField fileLink; // Lien vers le dossier
	@FXML
	private BorderPane Window; // Fenetre de l'app
	@FXML
	private Button browse; // bouton pour chercher fichier
	@FXML
	private ToggleGroup Sort; // group radiobutton
	@FXML
	private RadioButton NoSortRdnButton; //
	@FXML
	private RadioButton AlphaAscRdnButton; //
	@FXML
	private RadioButton AlphaDscRdnButton; //
	@FXML
	private RadioButton SizeAscRdnButton; //
	@FXML
	private RadioButton SizeDscRdnButton; //
	@FXML
	private ChoiceBox<String> ListDirectory; // listbox de tous les dossiers utilisé
	@FXML
	private CheckMenuItem ShowSideMenu; //
	@FXML
	private SplitPane SideMenu; // gauche du borderpane
	@FXML
	private ColorPicker colorPickerImage; // selection de la couleur
	@FXML
	private CheckMenuItem calculColor; //

	private Setting settings = new Setting();
	private ListFiles listOfFiles = new ListFiles(); // liste des images et des dossiers

	public Stage stageDetails = new Stage(); // fenetre detail d'une image
	public Stage stageImageView = new Stage(); // fenetre d'affichage individuel

	// Clique sur le bouton chercher
	@FXML
	private void handleBrowse() {
		DirectoryChooser chooser = new DirectoryChooser(); // fentre de fichier
		chooser.setTitle("Select folder"); // Nom de la fenetre
		File defaultDirectory = new File(settings.getSLink()); // Chemin par defaut
		chooser.setInitialDirectory(defaultDirectory.getParentFile()); // initialise le chemin par defaut
		File selectedDirectory = chooser.showDialog(null); // Ouvre la fenetre de recherche de doc
		fileLink.setText(selectedDirectory.toString()); // met le lien dans la zone de text
	}

	// bouton load
	@FXML
	private void handleLoad() throws Exception {
		settings.setSLink(fileLink.getText().toString()); // sauvegarde le chemin
		LoadDirectory(); // Genere la gallery
	}

	// charge les images et la gallery
	private void LoadGallery(List<ImageFile> listf) {

		Window.setCursor(Cursor.WAIT);

		//protege la fentre 
		synchronized (Window) {
			Task<ScrollPane> task = new Task<ScrollPane>() {
				@Override
				public ScrollPane call() {

					// format de la gallerie
					ScrollPane root = new ScrollPane();
					TilePane tile = new TilePane();
					tile.setPadding(new Insets(15, 15, 15, 15));
					tile.setHgap(15); // ecart entre les photo

					// creer une vision pour chaque image
					for (final ImageFile file : listf) { // tous les fichier du dossier
						if (file.getImageName().matches(".*png") || file.getImageName().matches(".*jpg")
								|| file.getImageName().matches(".*jpeg")) {// retient que les images
							ImageView imageView; // creer un nouvelle espace pour l'image
							imageView = createImageView(new File(file.getLink())); // ajoute l'image

							tile.getChildren().addAll(imageView); // ajoute la vu a la gallery
						}
					}

					// possibilte de scroller
					root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // jamais en horrizontal
					root.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // si besoin en vertical

					// met en bonne dimension
					root.setFitToWidth(true);

					// met le contenu
					root.setContent(tile);

					return root;
				}
			};

			task.setOnSucceeded(e -> {
				ScrollPane root = task.getValue();
				// on position la galerie au centre du borderpane
				Window.setCenter(root);
				Window.setCursor(Cursor.DEFAULT);
			});

			new Thread(task).start();
		}
	}

	private void LoadDirectory() throws Exception {

		//indique a l'utilisateur le chargement
		Window.setCursor(Cursor.WAIT);

		String path = settings.getSLink();// prend le lien du fichier
		File folder = new File(path); // charge le dossier
		// listOfFiles.addImage(folder, calculColor.isSelected()); // ajoute l'image
		// dans la liste

		// on protege la liste des image et on lance en tache de fond
		synchronized (listOfFiles) {
			Task<ListFiles> task = new Task<ListFiles>() {
				@Override
				public ListFiles call() {
					// process long-running computation, data retrieval, etc...

					listOfFiles.addImage(folder, calculColor.isSelected());
					return listOfFiles;
				}
			};

			task.setOnSucceeded(e -> {
				listOfFiles = task.getValue();
				updateInterfaceLoadDirectory();
			});

			new Thread(task).start();
		}
	}

	private void updateInterfaceLoadDirectory() {
		ListDirectory.getItems().clear(); // vide la liste afficher
		ListDirectory.getItems().add("All"); // ajoute l'element "all"
		ListDirectory.getItems().addAll(listOfFiles.getListOfDirectories()); // ajoute tous les dossier encore actif
		ListDirectory.setValue("All");

		// tri voulu par l'utilisateur
		if (AlphaAscRdnButton.isSelected()) { // tri alphabetique
			listOfFiles.sortAlphabetical();
		} else if (AlphaDscRdnButton.isSelected()) {// tri alphabetique inversé
			listOfFiles.sortUnalphabetical();
		} else if (SizeAscRdnButton.isSelected()) { // tri croissant par taille
			listOfFiles.sortSizeAsc();
		} else if (SizeDscRdnButton.isSelected()) { // tri decroissant par taille
			listOfFiles.sortSizeDsc();
		}

		LoadGallery(listOfFiles.getListOfFiles()); // recharche la gallery
	}

	// creer les images
	private ImageView createImageView(final File imageFile) {

		ImageView imageView = null;
		try {
			final Image image = new Image(new FileInputStream(imageFile), 150, 0, true, true);
			imageView = new ImageView(image);// creer un nouvau imageView pour y mettre
			imageView.setFitWidth(150); // Definint la largeur de l'image
			imageView.setCursor(Cursor.HAND); // change le curseur de la souris

			// menu contextuel pour supprimer la vue d'un image
			ContextMenu contextMenu = new ContextMenu();
			MenuItem Delete = new MenuItem("Delete this picture");

			// action appeler pour delete
			Delete.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					ImageFile img = new ImageFile(imageFile.getAbsolutePath()); // creer une nouvelle imageFile
					listOfFiles.removeImage(img); // retire sa copie de la liste
					ListDirectory.getItems().clear(); // on vide la liste des dossier
					ListDirectory.getItems().add("All"); // on ajoute l'element "all"
					ListDirectory.getItems().addAll(listOfFiles.getListOfDirectories()); // on ajoute tous les les
																							// fichier encore present
					ListDirectory.setValue("All"); // on met par defaut sur "all"
					LoadGallery(listOfFiles.getListOfFiles());// recharge la gallery avec la nouvelle liste d'image
				}
			});

			// affiche les info lié a l'image
			MenuItem Details = new MenuItem("Details");
			Details.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					loadImageDetails(imageFile); // fonction qui va ouvrir la fentre pour les details
				}
			});

			// annuler
			MenuItem Cancel = new MenuItem("Cancel");
			Cancel.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// ne fait rien
				}
			});

			contextMenu.getItems().addAll(Delete, Details, Cancel); // ajoute les element au dessus au menu
			// affiche le menu a l'endruit du clique
			imageView.setOnContextMenuRequested(e -> contextMenu.show(Window, e.getScreenX(), e.getScreenY()));

			// sur clique de souris
			imageView.setOnMouseClicked(new EventHandler<MouseEvent>() { // creer gestionnaire d'évènement

				@Override
				public void handle(MouseEvent mouseEvent) {
					contextMenu.hide();

					if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) { // Clique gauche
						if (mouseEvent.getClickCount() == 2) { // Double clique
							loadImageView(imageFile);
						}
					}
				}
			});
		} catch (FileNotFoundException ex) { // fichier introuvable
			ex.printStackTrace();
		}
		return imageView;// affiche
	}

	// vide la vue
	@FXML
	private void ClearContent(ActionEvent event) {
		listOfFiles.clearList();
		LoadGallery(listOfFiles.getListOfFiles());
	}

	// tri

	// alphabetique
	@FXML
	private void SortContent(ActionEvent event) {
		listOfFiles.sortAlphabetical();
		LoadGallery(listOfFiles.getListOfFiles());
	}

	// alphabetique inverser
	@FXML
	private void SortReverseContent(ActionEvent event) {
		listOfFiles.sortUnalphabetical();
		LoadGallery(listOfFiles.getListOfFiles());
	}

	// modifie selon les parametre de l'utilisateur
	@FXML
	private void ApplyModiferGallery(ActionEvent event) {
		try {
			// selon les parametre
			if (ListDirectory.getValue().equals("All") && colorPickerImage.getValue().equals(Color.WHITE)) {
				LoadGallery(listOfFiles.getListOfFiles());
			} else if (ListDirectory.getValue().equals("All")) {
				LoadGallery(listOfFiles.getImageOfThisColor(colorPickerImage.getValue()));
			} else if (colorPickerImage.getValue().equals(Color.WHITE)) {
				LoadGallery(listOfFiles.getImageInDirectory(ListDirectory.getValue()));
			} else {
				LoadGallery(listOfFiles.getImageOfParam(ListDirectory.getValue(), colorPickerImage.getValue()));
			}
		} catch (java.lang.NullPointerException ex) {
			LoadGallery(listOfFiles.getListOfFiles());
		}
	}

	// cache ou affiche le menu sur le code
	@FXML
	private void ReNewViewSideMenu(ActionEvent event) {
		if (ShowSideMenu.isSelected()) {
			SideMenu.setVisible(true);
			SideMenu.setManaged(true);
		} else {
			SideMenu.setVisible(false); // n'est plus visible
			SideMenu.setManaged(false); // n'est plus prit en compte
		}
	}

	// tri

	// par taille croissante
	@FXML
	private void SortSizeAsc(ActionEvent event) {
		listOfFiles.sortSizeAsc();
		LoadGallery(listOfFiles.getListOfFiles());
	}

	// par taille decroissante
	@FXML
	private void SortSizeDsc(ActionEvent event) {
		listOfFiles.sortSizeDsc();
		;
		LoadGallery(listOfFiles.getListOfFiles());
	}

	// genere la fentre pour voir l'image
	private void loadImageView(final File imageFile) {
		try {
			settings.setSImagePath(imageFile.getAbsolutePath());
			// charge la vision de l'image
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageView.fxml")); // fxml
			Parent root1 = (Parent) fxmlLoader.load(); // charge le fxml

			ViewController viewC = fxmlLoader.getController(); // charge le controler et le recupere
			viewC.setImageViewWindow(new ImageFile(imageFile.getAbsolutePath()), listOfFiles); // donne l'image a
																								// afficher

			// nouvelle fenetre
			stageImageView.setScene(new Scene(root1));
			// met le nom de l'image en nom de fenetre
			stageImageView.setTitle(imageFile.getPath().substring(imageFile.getPath().lastIndexOf("\\") + 1));

			// icone de la fenetre
			String currentDirectory = System.getProperty("user.dir");
			File f = new File(currentDirectory);
			f.getParentFile().getName();
			File fi = new File(f.getAbsolutePath() + "\\Ressource\\Icon\\IconViewImage.png");
			String localUrl = fi.toURI().toURL().toString();
			Image icon = new Image(localUrl);
			stageImageView.getIcons().add(icon);

			stageImageView.show(); // affiche
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// genere la fentre avec les details de l'image
	private void loadImageDetails(final File imageFile) {
		try {
			// creer une autre fenetre
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ImageDetails.fxml"));
			Parent root = loader.load();

			// Get controller
			DetailsControler d = loader.getController();

			// icone de la fenetre
			String currentDirectory = System.getProperty("user.dir");
			File f = new File(currentDirectory);
			f.getParentFile().getName();
			File fi = new File(f.getAbsolutePath() + "\\Ressource\\Icon\\IconDetails.png");
			String localUrl = fi.toURI().toURL().toString();
			Image icon = new Image(localUrl);
			stageDetails.getIcons().add(icon);

			// affiche
			stageDetails.setScene(new Scene(root));
			stageDetails.setResizable(false);
			stageDetails
					.setTitle("Details : " + imageFile.getPath().substring(imageFile.getPath().lastIndexOf("\\") + 1));

			stageDetails.show();

			// passe l'image au controler
			d.getImageFile(new ImageFile(imageFile.getAbsolutePath()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// tri
	@FXML
	private void AlphAscAction(ActionEvent event) {
		reSort();
	}

	@FXML
	private void AlphDscAction(ActionEvent event) {
		reSort();
	}

	@FXML
	private void NoSortAction(ActionEvent event) {
		reSort();
	}

	@FXML
	private void SizeAscAction(ActionEvent event) {
		reSort();
	}

	@FXML
	private void SizeDscAction(ActionEvent event) {
		reSort();
	}

	// tri
	private void reSort() {
		// tri voulu par l'utilisateur
		if (AlphaAscRdnButton.isSelected()) {
			listOfFiles.sortAlphabetical();
		} else if (AlphaDscRdnButton.isSelected()) {
			listOfFiles.sortUnalphabetical();
		} else if (SizeAscRdnButton.isSelected()) {
			listOfFiles.sortSizeAsc();
		} else if (SizeDscRdnButton.isSelected()) {
			listOfFiles.sortSizeDsc();
		}

		LoadGallery(listOfFiles.getListOfFiles()); // recharche la gallery
	}

	// calcul la couleur de toutes les images presente dans la gallerie
	@FXML
	private void CalculColorImageGallery(ActionEvent event) {
		Window.setCursor(Cursor.WAIT);
		
		//protege la liste des fichiers 
		synchronized (listOfFiles) {
			//equivalent a thread en javafx
			Task<ListFiles> task = new Task<ListFiles>() {

				@Override
				protected ListFiles call() {

					listOfFiles.CalculmainColorImage();

					return listOfFiles;
				}
			};

			task.setOnSucceeded(e -> {
				listOfFiles = task.getValue();
				Window.setCursor(Cursor.DEFAULT);
			});

			new Thread(task).start();
		}
		
		Window.setCursor(Cursor.DEFAULT);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		fileLink.setText(settings.getSLink()); // met le dernier chemin dans la barre de recherche

	}
}
