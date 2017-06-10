
public class Distance
{
	//Classe représentant la distance entre deux villes
	protected Ville _ville1;
	protected Ville _ville2;
	//Distance à vol d'oiseau
	protected double _distance_pigeon;
	//Distance en suivant les routes
	protected double _distance_coccinelle;

	public Distance()
	{
		this(null, null);
	}

	public Distance(Ville ville)
	{
		this(ville, null);
	}

	public Distance(Ville ville1, Ville ville2)
	{
		_ville1 = ville1;
		_ville2 = ville2;
		if((_ville1 != null) && (_ville2 != null))
		{
			calculDistances();
		}
	}

	protected void calculDistances()
	{
		calculDistancePigeon();//le pigeon passe par la voie aérienne
		calculDistanceCoccinelle();//la coccinelle emprunte la route, et fait attention aux voitures
	}

	protected void calculDistancePigeon()
	{
		_distance_pigeon = calculDistancePigeon(_ville1, _ville2);
	}

	public static double calculDistancePigeon(Ville ville1, Ville ville2)
	{
		if((ville1 != null) && (ville2 != null) && (ville1 != ville2))
		{
			//Différence de latitude
			double dx = Math.abs(ville1.getCoord().getLatitude() - ville2.getCoord().getLatitude());
			//Différence de longitude
			double dy = Math.abs(ville1.getCoord().getLongitude() - ville2.getCoord().getLongitude());
			//Théorème de Pythagore
			return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		}
		return 0;
	}

	protected void calculDistanceCoccinelle()
	{
		_distance_coccinelle = calculDistanceCoccinelle(_ville1, _ville2);
	}

	public static double calculDistanceCoccinelle(Ville ville1, Ville ville2)
	{
		if((ville1 != null) && (ville2 != null))
		{
			//calcul
		}
		return 0;
	}

	public void setVille1(Ville ville)
	{
		setVilles(ville, _ville2);
	}

	public void setVille2(Ville ville)
	{
		setVilles(_ville1, ville);
	}

	public void setVilles(Ville ville1, Ville ville2)
	{
		boolean b1, b2;
		if(b1 = (ville1 != _ville1))
		{
			if(_ville1 != null)
				_ville1.supprDistance(this);
			_ville1 = ville1;
			if(_ville1 != null)
				_ville1.ajouteDistance(this);
		}
		if(b2 = (ville2 != _ville2))
		{
			if(_ville2 != null)
				_ville2.supprDistance(this);
			_ville2 = ville2;
			if(_ville2 != null)
				_ville2.ajouteDistance(this);
		}
		if(b1 || b2)
			calculDistances();
	}

	//Retourne la ville 1 pour l'index 1, la ville 2 pour l'index 2 et null pour tout autre index
	public Ville getVille(int index)
	{
		if(index == 1)
			return _ville1;
		if(index == 2)
			return _ville2;
		return null;
	}

	//Retourne la ville à l'autre extrémité par rapport à celle passée en paramètre.
	//Si la ville en paramètre représente les deux extrémités ou aucune des deux, retourne null
	public Ville getVille(Ville depart)
	{
		if((_ville1 == depart) && (_ville2 != depart))
			return _ville2;
		if((_ville2 == depart) && (_ville1 != depart))
			return _ville1;
		return null;
	}

	//Distance à vol d'oiseau
	public double distancePigeon()
	{
		return _distance_pigeon;
	}

	//Distance en suivant les routes
	public double distanceCoccinelle()
	{
		return _distance_coccinelle;
	}

	//Recalcule les distances
	public void refreshDistances()
	{
		calculDistances();
	}

}
