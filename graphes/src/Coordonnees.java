
public class Coordonnees
{

	private double _latitude;
	private double _longitude;

	public Coordonnees(double longitude, double latitude)
	{
		_latitude = latitude;
		_longitude = longitude;
	}



	public boolean coordonneesOk(double longitude, double latitude)
	{
		if(longitude < 180 || longitude > -180 || latitude < 90 || latitude > 90) return true;
		else return false;
	}




  //SETTER & GETTER
	public double getLatitude(){
		return _latitude;
	}

	public double getLongitude(){
		return _longitude;
	}

	public void setLatitude(double latitude){
		_latitude = latitude;
	}

	public void setLongitude(double longitude){
		_longitude = longitude;
	}




}
