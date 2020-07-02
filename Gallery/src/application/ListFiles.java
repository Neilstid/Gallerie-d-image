package application;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;

public class ListFiles {
	private List<ImageFile> listOfFiles = new ArrayList<ImageFile>(); //liste des images
	private List<String> listOfDirectories = new ArrayList<String>(); //liste des dossier des image stocker
	private HashMap<String, Integer> numFileDirectory = new HashMap<String, Integer>(); //nombre d'image par fichier

	//constructeur
	public ListFiles() {

	}

	//ajoute les image d'un dossier a la liste (peut calculer la couleur)
	public void addImage(File folder, boolean calculDomCol) {
		String folderName = folder.getAbsolutePath();

		File[] temp = folder.listFiles(); // liste temporaire pour stocker les fichier dans le dossier selectionner

		ImageFile image;
		// ne garde que les images pas encore dans la liste
		for (final File imgFile : temp) {
			//ajoute les fichier png, jpg, jpeg
			if (imgFile.getAbsolutePath().matches(".*png") || imgFile.getAbsolutePath().matches(".*jpg") || imgFile.getAbsolutePath().matches(".*jpeg")) {
				if(calculDomCol == true) {
					image = new ImageFile(imgFile.getAbsolutePath(), true);//creer une nouvelle image et calcul la couleur
				}else {
					image = new ImageFile(imgFile.getAbsolutePath());//creer une nouvelle image
				}
				if (!listOfFiles.contains(image)) {//si l'image n'est pas dans la liste
					listOfFiles.add(image);//ajoute l'image

					//si le dossier n'est pas encore dans la liste
					if (!listOfDirectories.contains(folder.getAbsolutePath())) {
						listOfDirectories.add(folder.getAbsolutePath()); //creer le dossier dans la liste des fichier
						numFileDirectory.put(folderName, 0);
					}

					//ajoute 1 au dossier
					int numFile = numFileDirectory.get(folderName);
					numFile++;
					numFileDirectory.put(folderName, numFile);
				}
			}
		}
	}

	//ajoute les image d'un dossier a la liste (ne calcul pas la couleur)
	public void removeImage(ImageFile imgFile) {
		if (listOfFiles.contains(imgFile)) { //si l'image est dans la liste
			listOfFiles.remove(imgFile); //retire l'image

			//decrement le nombre d'image dans le dossier
			int numFile = (int) numFileDirectory.get(imgFile.getImagePath());
			numFile--;
			//supprime le dossier de la liste des fichier si il n'y a plus d'image
			if (numFile <= 0) {
				numFileDirectory.remove(imgFile.getImagePath());
				listOfDirectories.remove(imgFile.getImagePath());
			} else {
				numFileDirectory.put(imgFile.getImagePath(), numFile);
			}
		}
	}

	//retourne une liste de toutes les images dans le dossier donné
	public List<ImageFile> getImageInDirectory(String directory) {
		List<ImageFile> listOfFileInDir = new ArrayList<ImageFile>();

		for (int i = 0; i < listOfFiles.size(); i++) {
			if (listOfFiles.get(i).getImagePath().equals(directory)) {
				listOfFileInDir.add(listOfFiles.get(i));
			}
		}

		return listOfFileInDir;
	}

	//retourne une liste de toutes les images avec la couleur
	public List<ImageFile> getImageOfThisColor(Color col) {
		List<ImageFile> listOfFileColor = new ArrayList<ImageFile>();

		for (int i = 0; i < listOfFiles.size(); i++) {
			try {
				if (listOfFiles.get(i).getMainColor().equals(col)) {
					listOfFileColor.add(listOfFiles.get(i));
				}
			}catch(Exception e) {
				
			}
		}

		return listOfFileColor;
	}

	//retourne une liste de toutes les images avec les parametre selectionner
	public List<ImageFile> getImageOfParam(String directory, Color col) {
		List<ImageFile> listOfFileColor = new ArrayList<ImageFile>(); //creer une liste pour stocker les image correspondant au critere
		
		//pour toutes les images dans la liste
		for (int i = 0; i < listOfFiles.size(); i++) {
			try {
				if (listOfFiles.get(i).getMainColor().equals(col)
						&& listOfFiles.get(i).getImagePath().equals(directory)) { //si respecte les consultation
					listOfFileColor.add(listOfFiles.get(i)); //ajoute a liste tempporaire
				}
			} catch (Exception e) {

			}
		}

		return listOfFileColor;
	}

	public void CalculmainColorImage() {
		for(ImageFile img : listOfFiles) {
			img.CalculateDominantColour();
		}
	}
	
	//vide la liste
	public void clearList() {
		listOfFiles.clear();
		listOfDirectories.clear();
		numFileDirectory.clear();
	}

	//tri la liste
	
	//ordre alphabetique
	public void sortAlphabetical() {
		Collections.sort(listOfFiles, ImageFile.compareByLink);
	}

	//ordre alphabetique inverser
	public void sortUnalphabetical() {
		Collections.sort(listOfFiles, ImageFile.compareByLink);
		Collections.reverse(listOfFiles);
	}

	//taille croissante
	public void sortSizeAsc() {
		Collections.sort(listOfFiles, ImageFile.compareBySize);
	}
	
	//taille decroissante
	public void sortSizeDsc() {
		Collections.sort(listOfFiles, ImageFile.compareBySize);
		Collections.reverse(listOfFiles);
	}

	//retourne la liste des images
	public List<ImageFile> getListOfFiles() {
		return listOfFiles;
	}

	//retourne la liste des dossiers
	public List<String> getListOfDirectories() {
		return listOfDirectories;
	}
}
