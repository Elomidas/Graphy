import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Edge;
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
	//Clé d'API
	protected static final String _cle = "AIzaSyCQEUEgOb-E14qKKnTi5mPnhHzT_DT5oBc";

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
		Liaisons(_distance, true);
	}

	//Calcul la distance à vol d'oiseau si vol = true, par la route sinon
	public void Liaisons(int distance, boolean vol)
	{
		new Thread()
		{
			public void run()
			{
				Arretes(distance, vol);
			}
		}.start();
	}

	protected void Arretes(int distance, boolean vol)
	{
		System.out.println("Vol : " + vol);
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
				double dist = 0.0;
				if(vol)
					dist = Distance(Double.parseDouble(n1.getAttribute("Latitude")),
							Double.parseDouble(n1.getAttribute("Longitude")),
							Double.parseDouble(n2.getAttribute("Latitude")),
							Double.parseDouble(n2.getAttribute("Longitude")));
				else dist = Distance(n1.getAttribute("Nom"), n2.getAttribute("Nom"));
				if(dist <= distance)
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

	//Distance en km entre 2 positions
	protected static double Distance(double lat1, double lon1, double lat2, double lon2)
	{

        int r = 6378; //Rayon de la terre en kilomètres

	    double lat_a = toRad(lat1);
	    double lon_a = toRad(lon1);
	    double lat_b = toRad(lat2);
	    double lon_b = toRad(lon2);

	    return r * (Math.PI/2 - Math.asin(Math.sin(lat_b) * Math.sin(lat_a) + Math.cos(lon_b - lon_a) * Math.cos(lat_b) * Math.cos(lat_a)));
	}

	//Distance en km entre deux villes, en suivant la route
	//Assez long
	protected static double Distance(String ville1, String ville2)
	{
		//
		String nom1 = ville1.replace(" ", "+");
		String nom2 = ville2.replace(" ", "+");
		nom1 += "+,+France";
		nom2 += "+,+France";
		String requete = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
						 + nom1
						 + "&destinations="
						 + nom2
						 + "&key="
						 + _cle;
		URL oracle;
		boolean b_dist = false;
		try
		{
			oracle = new URL(requete);
	        BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                if((!b_dist) && inputLine.contains("distance"))
                	b_dist = true;
                else if(b_dist)
                {
                	if(inputLine.contains("value"))
                	{
                		String val = "";
                		for(int i = 0; i < inputLine.length(); i++)
                		{
                			char c = inputLine.charAt(i);
                			if((c >= '0') && (c <= '9'))
                				val += c;
                		}
                		return Double.parseDouble(val) / 1000.0;
                	}
                }
            }
            in.close();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			System.out.println(requete);
			e.printStackTrace();
		}
		return 0;
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
			int[] pos = Position(n);
			n.setAttribute("xyz", pos[0], pos[1], 0);
			_controller.setProgress(-1, -1, compteur / taille);
			compteur++;
		}
	}

	public int Dijkstra(String depart, String arrivee)//1 si fonctionne, 2 si ville dep pas réferencée, 3 si ville arr pas réferencée, 4 si pas de chemin.
	{
		int[][] tab_poids = new int[_graphe.getNodeCount()][2];//colonne 1 --> poids, colonne 2 -->1 si deja parcouru
		String[] antecedent = new String[_graphe.getNodeCount()];//Antecedent du noeud associe
		InitDij(tab_poids, antecedent, depart);
		Node noeud_fils;
		Node noeud_pere = GetNodeString(depart);
		if(GetNodeString(depart) == null)
			if(GetNodeString(arrivee) == null)
				return 5;
			else
				return 2;
		else if(GetNodeString(arrivee) == null)
			return 3;
		else
		{
			while((noeud_pere.getAttribute("Nom")+ "").compareTo(arrivee) != 0)
			{
				for(Edge e : noeud_pere.getEachEdge()) // pour chaque liaison du noeud
				{
					noeud_fils = e.getOpposite(noeud_pere);// on récupère le fils
					int indice_fils = RecuperationIndiceNoeud(noeud_fils);
					int indice_pere = RecuperationIndiceNoeud(noeud_pere);
					if(tab_poids[indice_fils][1] == 0)//Si on est jamais passé par ce noeud
						if(tab_poids[indice_fils][0] == -1 || tab_poids[indice_pere][0]+ Distance(Double.parseDouble(noeud_pere.getAttribute("Latitude")),
																Double.parseDouble(noeud_pere.getAttribute("Longitude")),
																Double.parseDouble(noeud_fils.getAttribute("Latitude")),
																Double.parseDouble(noeud_fils.getAttribute("Longitude")))
													< tab_poids[indice_fils][0] )//
						{
							tab_poids[indice_fils][0] = tab_poids[indice_pere][0] + (int)Distance(Double.parseDouble(noeud_pere.getAttribute("Latitude")),
																							Double.parseDouble(noeud_pere.getAttribute("Longitude")),
																							Double.parseDouble(noeud_fils.getAttribute("Latitude")),
																							Double.parseDouble(noeud_fils.getAttribute("Longitude")));
							antecedent[indice_fils] = noeud_pere.getAttribute("Nom") + "";
						}
				}
				noeud_pere = NoeudSuivant(tab_poids, antecedent);
				if(noeud_pere == null)
					return 4;
				tab_poids[RecuperationIndiceNoeud(noeud_pere)][1] = 1;
			}
		}
		_controller.SetTitleDij("Algorithme de Dijkstra de " + depart + " vers " + arrivee + ".");
		int km=tab_poids[RecuperationIndiceNoeud(noeud_pere)][0];
		_controller.SetMsgDij("La duree du parcours est de " + km + " kilometres.\n");
		String[] villes;
		String ville = arrivee;
		int i=1;
		while(ville.compareTo(depart) != 0)
		{
			i++;
			ville = antecedent[RecuperationIndiceNoeud(GetNodeString(ville))];
		}
		ville = arrivee;
		villes = new String[i];
		for(int index=0;index<i;index++)
		{
			villes[index] = ville;
			ville = antecedent[RecuperationIndiceNoeud(GetNodeString(ville))];
		}
		int pos=1;
		for(int index = i-1;index>=0;index--)
		{
			_controller.SetMsgDij(_controller.GetMsgDij() + "La " + pos + "e ville traversee est : " + villes[index] + ".\n");
			pos++;
		}
		return 1;
	}

	Node GetNodeString(String depart)
	{
		for(int i=0;i<_graphe.getNodeCount();i++)
			if(_graphe.getNode(i).getAttribute("Nom").equals(depart))
				return _graphe.getNode(i);
		return null;
	}

	Node NoeudSuivant(int[][] tab_poids, String[] antecedent)
	{
		int pos = -1;
		int minimum=1000000000;
		for(int i=0;i<tab_poids.length;i++)
			if(tab_poids[i][0] > 0)
				if((tab_poids[i][0] <= minimum) && (tab_poids[i][1] == 0))
				{
					minimum = tab_poids[i][0];
					pos = i;
				}
		if(pos==-1)
			return null;
		else
			return _graphe.getNode(pos);
	}

	int RecuperationIndiceNoeud(Node n)
	{
		int i;
		for(i=0;i<_graphe.getNodeCount();i++)
			if(_graphe.getNode(i).equals(n))
				return i;
		return -1;
	}

	public void InitDij(int[][] tab_poids, String[] antecedent, String depart)
	{
		for(int i=0; i < _graphe.getNodeCount();i++)
		{
			if(_graphe.getNode(i).getAttribute("Nom", String.class).compareTo(depart) == 0)
			{
				tab_poids[i][0] = 0;
				tab_poids[i][1] = 1;
			}
			else
			{
				tab_poids[i][0] = -1;
				tab_poids[i][1] = 0;
			}
			antecedent[i] = "";
		}
	}


	//A vol d'oiseau
	public void Astar(String depart, String arrivee)
	{
		double[][] tab_poids = new double[_graphe.getNodeCount()][2];//colonne 1 --> coutAstar, colonne 2 --> indice predecesseur courant
		InitAstar(tab_poids, depart);

		ArrayList<String> aExplorer = new ArrayList<>();
		ArrayList<String> DejaExplore = new ArrayList<>();
		DejaExplore.add(depart);

		Node noeud_pere = GetNodeString(depart);/*_graphe.getNode(depart);*/
		Node noeud_fils;

		//System.out.println("testAvantWhile");

		while((aExplorer.size() > 0) || !(DejaExplore.contains(arrivee)))
		{

			//System.out.println("testAvantFor");

			for(Edge e : noeud_pere.getEachEdge()) // pour chaque liaison du noeud
			{
				//System.out.println("testApresFor");

				noeud_fils = e.getOpposite(noeud_pere);// on récupère le fils

				if(!(DejaExplore.contains(noeud_fils.getAttribute("nom")))) {

					int f = RecuperationIndiceNoeud(noeud_fils);
					int p = RecuperationIndiceNoeud(noeud_pere);

					if (aExplorer.contains(noeud_fils.getAttribute("nom"))) {
						if (CoutAstar(noeud_pere,GetNodeString(arrivee), noeud_fils) < tab_poids[f][0]) {
							tab_poids[f][0] = CoutAstar(noeud_pere, GetNodeString(arrivee), noeud_fils);
							tab_poids[f][1] = p;
						}
					} else {
						aExplorer.add(noeud_fils.getAttribute("nom"));
						tab_poids[f][0] = CoutAstar(noeud_pere,GetNodeString(arrivee), noeud_fils);
						tab_poids[f][1] = p;
					}
				}

			}

			String X = aExplorer.get(0);

			if(aExplorer.size() > 1)
			{

				for (int i = 1; i < aExplorer.size(); i++)
				{

					int y = RecuperationIndiceNoeud(GetNodeString(aExplorer.get(i)));
					int x = RecuperationIndiceNoeud(GetNodeString(X));

					if (tab_poids[y][0] < tab_poids[x][0])
					{
						X = aExplorer.get(i); //On récupere la ville du tableau aExplorer avec le cout minimum
					}

				}
			}

			noeud_pere = GetNodeString(X);
			DejaExplore.add(X);
			aExplorer.remove(X);

			//System.out.println("testFinWhile");

		}

		if(aExplorer.size() == 0) {
			System.out.println("pas de chemin possible entre ces 2 villes");
		}

		else
			System.out.println("testfin");
	}

	//cout a vol d'oiseau
	public double CoutAstar(Node antecedant, Node arrivee,Node ville){
		double cout = 1000000000;

		double distanceAntecedant = Distance(Double.parseDouble(antecedant.getAttribute("Latitude")),
				Double.parseDouble(antecedant.getAttribute("Longitude")),
				Double.parseDouble(ville.getAttribute("Latitude")),
				Double.parseDouble(ville.getAttribute("Longitude")));

		double distanceArrivee = Distance(Double.parseDouble(ville.getAttribute("Latitude")),
				Double.parseDouble(ville.getAttribute("Longitude")),
				Double.parseDouble(arrivee.getAttribute("Latitude")),
				Double.parseDouble(arrivee.getAttribute("Longitude")));

		cout = distanceAntecedant + distanceArrivee;


		return(cout);
	}

	public void InitAstar(double[][] tab_poids, String depart)
	{
		for(int i=0; i < _graphe.getNodeCount();i++)
		{
			if(_graphe.getNode(i).getAttribute("Nom", String.class).compareTo(depart) == 0)
			{
				tab_poids[i][0] = 0;	//poids
				tab_poids[i][1] = -1;	//indice du predecesseur (pas de predecesseur)
			}
			else
			{
				tab_poids[i][0] = 1000000000;
				tab_poids[i][1] = -1;
			}
		}
	}

}
