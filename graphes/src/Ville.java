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

}
