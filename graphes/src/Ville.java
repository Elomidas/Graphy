import java.util.ArrayList;

public class Ville {

	private int _id;
	private String _nom;
	private int _nbHab;
	private Coordonnees _coord;
	private ArrayList<Distance> _dists;

	public Ville()
	{
		_id = 0;
		_nom = new String();
		_nbHab = -1;
		_coord = new Coordonnees();
		_dists = new ArrayList<Distance>();
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
		_coord.coordonneesOk(coord.longitude, coord.latitude);
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

	public void supprDistance()

}
