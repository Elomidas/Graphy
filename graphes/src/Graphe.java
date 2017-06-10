import java.io.FileInputStream;
import java.util.ArrayList;

public class Graphe
{
	private ArrayList<Ville> _villes;

	//Distance maximae par défaut pour que deux villes soient reliées
	protected static final int _distance = 150;

	public Graphe()
	{
		_villes = new ArrayList<Ville>();
	}

	public Graphe(ArrayList<Ville> villes)
	{
		_villes = villes;
	}

	public Graphe(String chemin, int minHab)
	{
		_villes = new ArrayList<Ville>();
		ChargerFichier(chemin, minHab);
	}

	public Graphe(String chemin)
	{
		this(chemin, 0);
	}

	//Copie les Villes d'un graphe existant, sans copier les distances
	public Graphe(Graphe g)
	{
		ArrayList<Ville> src = g.getVilles();
		_villes = new ArrayList<Ville>();
		for(int i = 0; i < src.size(); i++)
		{
			Ville v = new Ville(src.get(i));
			_villes.add(v);
		}
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
		String str[] = new String[] {"", "", "", "", ""};
		try
		{
			while((val = fichier.read()) != -1)
			{
				//Lecture du fichier caractère par caractère
				//On converti les deux bytes en un caractère
				char c = (char)(val);
				//System.out.println("Val : " + val + ", char : " + c);
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
								id++;
							}
							str = new String[] {"", "", "", "", ""};
						}
						else start = true;

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

	//Fonctions de création des liaisons
	//1 : On ne relie que les villes qui ne sont pas trop éloignées
	public void Liaisons()
	{
		Liaisons(_distance);
	}

	public void Liaisons(int distance)
	{
		int taille = _villes.size();
		for(int i = 0; i < taille; i++)
		{
			Ville v1 = _villes.get(i);
			for(int j = (i + 1); i < taille; i++)
			{
				if((Distance.calculDistancePigeon(v1, _villes.get(j))) <= distance)
				{
					Distance d = new Distance(v1, _villes.get(j));
					v1.ajouteDistance(d);
					_villes.get(j).ajouteDistance(d);
				}
			}
		}
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
}
