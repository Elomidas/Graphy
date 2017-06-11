import java.io.FileInputStream;
import java.util.ArrayList;

public class Graphe
{
	private ArrayList<Ville> _villes;
	//Index de la plus grosse ville
	protected int _indexCap;
	protected int _etape;
	protected Controller _controller;

	//Distance maximae par défaut pour que deux villes soient reliées
	protected static final int _distance = 50;

	public Graphe()
	{
		Init();
	}

	public Graphe(ArrayList<Ville> villes, Controller c)
	{
		Init();
		for(int i = 0; i < villes.size(); i++)
		{
			Ville v = new Ville(villes.get(i));
			if((_indexCap < 0)
				|| (v.getNbHab() > _villes.get(_indexCap).getNbHab()))
				_indexCap = i;
			_villes.add(v);
		}
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

	//Copie les Villes d'un graphe existant, sans copier les distances
	public Graphe(Graphe g, Controller c)
	{
		this(g.getVilles(), c);
	}

	protected void Init()
	{
		_villes = new ArrayList<Ville>();
		_controller = null;
		_indexCap = -1;
		_etape = 0;
	}

  //GETTER & SETTER
	public ArrayList<Ville> getVilles()
	{
		return _villes;
	}

	public int getNb_ville()
	{
		return _villes.size();
	}

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
		int val, iterateur = 0, id = 0;
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
						if(start)
						{
							int hab = Integer.parseInt(str[2]);
							if(hab >= minHab)
							{
								Ville v = new Ville(id,
													str[1],
													hab,
													Double.parseDouble(str[3]),
													Double.parseDouble(str[4]));
								_villes.add(v);
								if(hab > habMax)
								{
									_indexCap = (id - 1);
									habMax = hab;
								}
								id++;
							}
							str = new String[] {"", "", "", "", ""};
						}
						else start = true;
						_controller.setProgress(id / 36701.0, -1, -1);

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

	public void Afficher()
	{
		for(int i = 0; i < _villes.size(); i++)
		{
			Ville v = _villes.get(i);
			System.out.println(v.getId() + ", " + v.getNom() + ", " + v.getNbHab() + ", " + v.getCoord());
		}
	}

	public void AfficherInfos()
	{
		int taille = _villes.size();
		int nbLi = 0;
		for(int i = 0; i < taille; i++)
			nbLi += _villes.get(i).getDegre();
		nbLi /= 2;
		System.out.println(taille + " villes chargées.\n" + nbLi + " liaisons.");
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
		int taille = _villes.size();
		for(int i = 0; i < taille; i++)
		{
			int nb = 0;
			Ville v1 = _villes.get(i);
			int idConn = _villes.get(i).getConnexe();
			for(int j = (i + 1); j < taille; j++)
			{
				if((Distance.calculDistancePigeon(v1, _villes.get(j))) <= distance)
				{
					nb++;
					Distance d = new Distance(v1, _villes.get(j));
					v1.ajouteDistance(d);
					_villes.get(j).ajouteDistance(d);
					_villes.get(j).setConnexe(idConn);
				}
			}
			_controller.setProgress(-1, i / ((double)taille), -1);
		}
		_etape = 2;
	}

	//Divers
	//Calcul de l'écart de longitude et de latitude
	public double DLatitude()
	{
		double dmin = _villes.get(0).getCoord().getLatitude();
		double dmax = dmin;
		for(int i = 1; i < _villes.size(); i++)
		{
			double l = _villes.get(0).getCoord().getLatitude();
			if(l > dmax)
				dmax = l;
			else if(l < dmin)
				dmin = l;
		}
		return dmax - dmin;
	}

	public double DLongitude()
	{
		double dmin = _villes.get(0).getCoord().getLongitude();
		double dmax = dmin;
		for(int i = 1; i < _villes.size(); i++)
		{
			double l = _villes.get(0).getCoord().getLongitude();
			if(l > dmax)
				dmax = l;
			else if(l < dmin)
				dmin = l;
		}
		return dmax - dmin;
	}

	//Supprime toutes les villes non-reliées à la plus grosse ville du graphe
	//Rend le graphe connexe
	public void Connexe()
	{
		new Thread()
		{
			public void run()
			{
				Slice();
			}
		}.start();
	}

	protected void Slice()
	{
		//On récupère l'ID de la partie connexe qui nous interesse
		int idConn = _villes.get(_indexCap).getConnexe();
		int compteur = 0;
		int taille = _villes.size();
		for(int i = taille - 1; i >= 0; i--)
		{
			if(_villes.get(i).getConnexe() != idConn)
			{
				//On détruit ce sommet
				compteur++;
				_villes.remove(i);
			}
			_controller.setProgress(-1, -1, 100.0 - (i / ((double)taille)));
		}
		if(compteur == 0)
			System.out.println("Aucune ville n'a été détruite.");
		else if(compteur == 1)
			System.out.println("Une seule ville a été détruite.");
		else System.out.println(compteur + " villes ont été détruites.");
		_etape = 3;
	}
}
