import java.io.FileInputStream;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;

public class Graphe
{
	//Index de la plus grosse ville
	protected int _indexCap;
	protected int _etape;
	protected Controller _controller;
	protected Graph _graphe;
	//Coordonnées utiles à l'affichage
	protected double _longitudeMinimum;
	protected double _latitudeMinimum;

	//Encadrement de la France métropolitaine
	protected static final double _latMax = 51.248163159055906;
	protected static final double _latMin = 41.2282490151853;
	protected static final double _lonMax = 8.382568359375;
	protected static final double _lonMin = -5.372314453125;

	//Distance maximae par défaut pour que deux villes soient reliées
	protected static final int _distance = 50;

	public Graphe()
	{
		Init();
	}

	public Graphe(String chemin, int minHab, Controller c)
	{
		Init();
		_controller = c;
		new Thread()
		{
			public void run()
			{
				ChargerFichier(chemin, (minHab >= 0) ? minHab : 0);
			}
		}.start();
	}

	public Graphe(String chemin, Controller c)
	{
		this(chemin, 0, c);
	}

	protected void Init()
	{
		_controller = null;
		_indexCap = -1;
		_etape = 0;
		_graphe = new SingleGraph("Graphy");
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		_graphe.addAttribute("ui.quality");
		_graphe.addAttribute("ui.antialias");
	}

	//GETTER & SETTER
	public int getEtape()
	{
		return _etape;
	}



	//Charge une liste de villes contenues dans un fichier .csv
	//Ne prend que les villes ayant au moins minHab d'habitants
	public boolean ChargerFichier(String chemin, int minHab)
	{
		FileInputStream fichier = null;
		//Ouverture su fichier
		try
		{
			fichier = new FileInputStream(chemin);
		}
		catch(Exception e)
		{
			System.out.println("Le fichier n'a pas pu être ouvert.");
			e.printStackTrace();
			return false;
		}

		//Traitement
		//Format du fichier CSV :
		// > id;nom;population;longitude;latitude
		// > Ligne 1 = nom des colonnes
		int val, iterateur = 0, id = 0, compteur = 0;
		boolean start = false;
		int habMax = 0;
		String str[] = new String[] {"", "", "", "", ""};
		try
		{
			while((val = fichier.read()) != -1)
			{
				//Lecture du fichier caractère par caractère
				//On converti les deux bytes en un caractère
				char c = (char)(val);
				switch(c)
				{
					case ';' :
						iterateur++;
						break;

					//En cas de retour CRLF, on ignore le CR et on traite le LF
					case '\r' : //CR
						break;

					case '\n' : //LF
						//On passe à une nouvelle instance
						iterateur = 0;
						if(id == 0)
							id++;
						if(start && Filtre(str[3], str[4]))
						{
							int hab = Integer.parseInt(str[2]);
							if(hab >= minHab)
							{
								//On ajoute le noeud au graphe
								Node n = _graphe.addNode("" + id);
								n.setAttribute("Nom", str[1]);
								n.setAttribute("Habitants", Integer.toString(hab));
								n.setAttribute("Longitude", str[3]);
								n.setAttribute("Latitude", str[4]);

								//Si on a plus d'habitants dans cette ville que dans les autres,
								//on enregistre son ID
								if(hab > habMax)
								{
									_indexCap = (id - 1);
									habMax = hab;
								}
								id++;
							}
						}
						else start = true;
						str = new String[] {"", "", "", "", ""};
						compteur++;
						_controller.setProgress(compteur / 36701.0, -1, -1);

						break;

					default :
						if((iterateur != 0) && start)
						{
							if((c == ',')
								&& ((iterateur == 3)
									|| (iterateur == 4)))
								str[iterateur] += '.';
							else str[iterateur] += c;
						}
						break;
				}
			}
			_controller.setProgress(100, -1, -1);
		}
		catch(Exception e)
		{
			System.out.println("Erreur lors de la lecture du fichier.");
			e.printStackTrace();
			return false;
		}
		finally
		{
			//Fermeture du fichier
			try
			{
				fichier.close();
			}
			catch(Exception e)
			{
				System.out.println("Le fichier n'a pas pu être fermé.");
				e.printStackTrace();
				return false;
			}
		}
		_etape = 1;
		//Succès
		return true;
	}

	//Filtre les villes selon la latitude et la longitude pour ne garder que celles en France métropolitaine
	protected static boolean Filtre(String lon, String lat)
	{
		double lati = Double.parseDouble(lat);
		if(lati < _latMin)
			return false;
		if(lati > _latMax)
			return false;
		double longi = Double.parseDouble(lon);
		if(longi < _lonMin)
			return false;
		if(longi > _lonMax)
			return false;
		return true;
	}

	public void AfficherGUI()
	{
		_graphe.display(false);
	}

	public void Afficher()
	{
		for(Node n : _graphe)
		{
			System.out.println(n.getAttribute("Nom") + ", " +
							   n.getAttribute("Habitants") + ", " +
							   n.getAttribute("Latitude") + ", " +
							   n.getAttribute("Longitude"));
		}
	}

	public void AfficherInfos()
	{
		System.out.println(_graphe.getNodeCount() + " villes chargées.\n" +
						   _graphe.getEdgeCount() + " liaisons.");
	}

	//Fonctions de création des liaisons
	//1 : On ne relie que les villes qui ne sont pas trop éloignées
	public void Liaisons()
	{
		Liaisons(_distance);
	}

	public void Liaisons(int distance)
	{
		new Thread()
		{
			public void run()
			{
				Arretes(distance);
			}
		}.start();
	}

	protected void Arretes(int distance)
	{
		if(distance < 0)
			distance = _distance;
		int taille = _graphe.getNodeCount();
		for(int i = 0; i < taille; i++)
		{
			Node n1 = _graphe.getNode(i);
			for(int j = (i + 1); j < taille; j++)
			{
				Node n2 = _graphe.getNode(j);
				//On ajoute un arc entre les sommets
				//si les villes sont assez proches
				if(Distance(Double.parseDouble(n1.getAttribute("Latitude")),
							Double.parseDouble(n1.getAttribute("Longitude")),
							Double.parseDouble(n2.getAttribute("Latitude")),
							Double.parseDouble(n2.getAttribute("Longitude"))) <= distance)
					_graphe.addEdge(i + "-" + j, i, j);
			}
			_controller.setProgress(-1, i / ((double)taille), -1);
		}
		_etape = 2;
	}

	//Divers
	//Calcul de l'écart de longitude et de latitude
	public double DLatitude()
	{
		double dmin = Double.parseDouble(_graphe.getNode(0).getAttribute("Latitude"));
		double dmax = dmin;
		for(Node n : _graphe)
		{
			double l = Double.parseDouble(n.getAttribute("Latitude"));
			if(l > dmax)
				dmax = l;
			else if(l < dmin)
				dmin = l;
		}
		_latitudeMinimum = dmin;
		return dmax - dmin;
	}

	public double DLongitude()
	{
		double dmin = Double.parseDouble(_graphe.getNode(0).getAttribute("Longitude"));
		double dmax = dmin;
		for(Node n : _graphe)
		{
			double l = Double.parseDouble(n.getAttribute("Longitude"));
			if(l > dmax)
				dmax = l;
			else if(l < dmin)
				dmin = l;
		}
		_longitudeMinimum = dmin;
		return dmax - dmin;
	}

	protected int[] Position(Node n)
	{
		int h = (int)(1000 * Distance(Double.parseDouble(n.getAttribute("Latitude")),
									  _longitudeMinimum,
									  _latitudeMinimum,
									  _longitudeMinimum));
		int l = (int)(1000 * Distance(_latitudeMinimum,
									  Double.parseDouble(n.getAttribute("Longitude")),
									  _latitudeMinimum,
									  _longitudeMinimum));
		return new int[] {l, h};
	}

	protected static double toRad(double angle)
	{
        return (Math.PI * angle) / 180.0;
	}

	protected static double Distance(double lat1, double lon1, double lat2, double lon2)
	{

        int r = 6378; //Rayon de la terre en kilomètres

	    double lat_a = toRad(lat1);
	    double lon_a = toRad(lon1);
	    double lat_b = toRad(lat2);
	    double lon_b = toRad(lon2);

	    return r * (Math.PI/2 - Math.asin(Math.sin(lat_b) * Math.sin(lat_a) + Math.cos(lon_b - lon_a) * Math.cos(lat_b) * Math.cos(lat_a)));
	}

	//Donne des positions aux sommets pour les afficher
	public void Positionner(double hauteur, double largeur)
	{
		//On calcule la largeur et la hauteur du graphe
		double dh = DLongitude();
		double dl = DLatitude();
		int compteur = 0;
		double taille = _graphe.getNodeCount();

		//On calcul les positions des sommets
		for(Node n : _graphe)
		{
			/*
			//Hauteur
			int h = (int)(hauteur * (_latitudeMaximum - Double.parseDouble(n.getAttribute("Latitude"))) / dh);
			//Largeur
			int l = (int)(largeur * (Double.parseDouble(n.getAttribute("Longitude")) - _longitudeMinimum) / dl);
			System.out.println("Position : " + l + ", " + h);
			*/
			/*
			int h = (int)(1000 * Double.parseDouble(n.getAttribute("Latitude")));
			int l = (int)(1000 * Double.parseDouble(n.getAttribute("Longitude")));
			*/
			int[] pos = Position(n);
			n.setAttribute("xyz", pos[0], pos[1], 0);
			_controller.setProgress(-1, -1, compteur / taille);
			compteur++;
		}
	}
	
	public void Astar()
	{
		//Algo A*
	}
}
