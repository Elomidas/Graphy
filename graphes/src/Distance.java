
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
		calculDistancePigeon();
		calculDistanceCoccinelle();
	}

	protected void calculDistancePigeon()
	{
		if((_ville1 != null) && (_ville2 != null))
		{
			double dx = Math.abs(_ville1.getCoord().getLatitude() - _ville2.getCoord().getLatitude());
			double dy = Math.abs(_ville1.getCoord().getLongitude() - _ville2.getCoord().getLongitude());
			_distance_pigeon = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		}
		else _distance_pigeon = 0;
	}

	protected void calculDistanceCoccinelle()
	{
		if((_ville1 != null) && (_ville2 != null))
		{
			//calcul
		}
		else _distance_coccinelle = 0;
	}

	public void setVille1(Ville ville)
	{
		_ville1 = ville;
		calculDistances();
	}

	public void setVille2(Ville ville)
	{
		_ville2 = ville;
		calculDistances();
	}

	public void setVilles(Ville ville1, Ville ville2)
	{
		_ville1 = ville1;
		_ville2 = ville2;
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
