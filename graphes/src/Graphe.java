import java.lang.reflect.Array;
import java.util.ArrayList;

public class Graphe
{
	private ArrayList<Ville> _villes;

	public Graphe(){
		//
	}

	public Graphe(ArrayList<Ville> villes) {
		_villes = villes;
	}

	
  //GETTER & SETTER
	public ArrayList<Ville> getVilles(){
		return _villes;
	}

}
