import java.util.ArrayList;

public class Ville {

	protected int _id;
	protected String _nom;
	protected int _nbHab;
	protected Coordonnees _coord;
	protected ArrayList<Distance> _dists;

	//Ctr défaut
	public Ville()
	{
		_id = 0;
		_nom = new String();
		_nbHab = -1;
		_coord = new Coordonnees();
		_dists = new ArrayList<Distance>();
	}

	//Ctr surchargé
	public Ville(int id, String nom, int nbHab, double longitude, double latitude)
	{
		_id = id;
		_nom = new String(nom);
		_nbHab = nbHab;
		_coord = new Coordonnees(longitude, latitude);
		_dists = new ArrayList<Distance>();
	}

	//Copie une ville sans le tableau des distances
	public Ville(Ville v)
	{
		this(v.getId(),
			 new String(v.getNom()),
			 v.getNbHab(),
			 v.getCoord().getLongitude(),
			 v.getCoord().getLatitude());
	}

	public int getId()
	{
		return _id;
	}

	public void setId(int id)
	{
		if(id>= 0)
			_id = id;
	}

	public String getNom()
	{
		return _nom;
	}

	public void setNom(String nom)
	{
		if(nom.compareTo("") != 0)
			_nom = nom;
	}

	public int getNbHab()
	{
		return _nbHab;
	}

	public void setNbHab(int nbHab)
	{
		if(nbHab >= 0)
			_nbHab = nbHab;
	}

	public Coordonnees getCoord()
	{
		return _coord;
	}

	public void setCoord(Coordonnees coord)
	{
		_coord.setLatitude(coord.getLatitude());
		_coord.setLongitude(coord.getLongitude());
		for(int i=0;i<_dists.size();i++)
			_dists.get(i).refreshDistances();
	}

	public ArrayList<Distance> getDistances()
	{
		return _dists;
	}

	/*
	public void setDictances(ArrayList<Distance> dists)
	{
		_dists = dists;
	}
	*/

	public void ajouteDistance(Distance dist)
	{
		_dists.add(dist);
	}

	public void supprDistance(int index)
	{
		_dists.remove(index);
	}

	public void supprDistance(Ville ville)
	{
		for(int index=0;index<_dists.size();index++)
		{
			if(_dists.get(index).getVille(this).equals(ville))
			{
				_dists.remove(index);
				return;
			}
		}

	}

	public void supprDistance(Distance distance)
	{
		for(int index=0;index<_dists.size();index++)
		{
			if(_dists.get(index).equals(distance))
			{
				_dists.remove(index);
				return;
			}
		}
	}



}
