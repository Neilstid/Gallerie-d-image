package application;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Object.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageFile {
	private String link; // lien absolue vers l'image
	private int size; // taille de l'image
	private Color mainColor = Color.WHITE; // couleur principale
	private String imageName; // nom de l'image
	private String imagePath; // nom du dossier

	// constructors
	public ImageFile(String linkValue, boolean calculColor) {
		//chemin de l'image
		link = linkValue;

		//thread pour trouver la couleur de l'image
		if(calculColor == true) {
			new Thread(new Runnable() {
			     @Override
			     public void run() {
			    	 CalculateDominantColour();
			     }
			}).start();
		}
		
		//creer le chemin
		Path path = Paths.get(link);
		try {
			size = (int) Files.size(path); //recupere la taille du fichier
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		imageName = link.substring(link.lastIndexOf("\\") + 1); //recupere le derniere nom
		imagePath = link.substring(0, link.length() - imageName.length() - 1); //recupere le nom du chemin - le nom
	}
	
	public ImageFile(String linkValue) {
		link = linkValue;
		
		Path path = Paths.get(link);
		try {
			size = (int) Files.size(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		imageName = link.substring(link.lastIndexOf("\\") + 1);
		imagePath = link.substring(0, link.length() - imageName.length() - 1);
	}

	// calculate the average color of the picture
	public String CalculateDominantColour() {
		
		//au cas ou fichier introuvable
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(new File(link));
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		int[] pixel;

		int sumR = 0; //somme des pixel rouge
		int sumG = 0; //somme des pixel vert
		int sumB = 0; //somme des pixel bleu
		int nb = 0; //nombre de pixel
		
		//pour chaque pixel de l'image
		for (int y = 0; y < bi.getHeight(); y++) {
		    for (int x = 0; x < bi.getWidth(); x++) {
		        pixel = bi.getRaster().getPixel(x, y, new int[3]);
		        sumR += pixel[0]; //ajoute a la somme
		        sumG += pixel[1]; //ajoute a la somme
		        sumB += pixel[2]; //ajoute a la somme
		        
		        nb++; //ajoute a la somme
		    }
		}
		//System.out.print("rouge : " + sumR/nb +"\nVert : " + sumG/nb +"\nBleu : " + sumB/nb);
		
		//ajoute a la somme
		int avgR = sumR/nb;
		int avgG = sumG/nb;
		int avgB = sumB/nb;
		
		Color c = Color.rgb(avgR,avgG,avgB);
		mainColor = c;
		return "[" + avgR +", "+avgG+", "+avgB+"]";
	}

	// defini chemin de l'image
	public void setLink(String value) {
		link = value;
	}

	// retourne chemin de l'image
	public String getLink() {
		return link;
	}

	// retourne poid de l'image en octets
	public int getSize() {
		return size;
	}

	//retourne la couleur principale
	public Color getMainColor() {
		return mainColor;
	}

	//retourne le nom de l'image
	public String getImageName() {
		return imageName;
	}

	//retourne le chemin absolue de l'image
	public String getImagePath() {
		return imagePath;
	}

	//deux image sont elle les meme
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ImageFile imgObject = (ImageFile) o;
		return link.contentEquals(imgObject.link);
	}

	// pour trier
	//par chemin complet
	static Comparator<ImageFile> compareByLink = (ImageFile o1, ImageFile o2) -> o1.getLink().compareTo(o2.getLink());
	//par nom
	static Comparator<ImageFile> compareByName = (ImageFile o1, ImageFile o2) -> o1.getImageName()
			.compareTo(o2.getImageName());
	//par taille
	static Comparator<ImageFile> compareBySize = (ImageFile o1, ImageFile o2) -> Integer.compare(o1.getSize(),
			o2.getSize());
}
