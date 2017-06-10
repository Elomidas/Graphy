import java.lang.reflect.Array;
import java.util.ArrayList;

public class Graphe
{
	private ArrayList<Ville> _villes;
	private int _nb_ville;

	public Graphe(){
		//
	}

	public Graphe(ArrayList<Ville> villes) {
		_villes = villes;
		_nb_ville = villes.size();
	}



  //GETTER & SETTER
	public ArrayList<Ville> getVilles(){
		return _villes;
	}

	public int getNb_ville(){
		return _nb_ville;
	}

}
