
public class Coordonnees
{

	private double _latitude;
	private double _longitude;


	public Coordonnees()
	{
		//
	}

	public Coordonnees(double longitude, double latitude)
	{
		if (longitudeOk(longitude) && latitudeOk(latitude)) {
			_latitude = latitude;
			_longitude = longitude;
		}

		else{
			System.out.println("vous ne pouvez pas créer de ville extraterrestre ici");
		}
	}

	public boolean longitudeOk(double longitude)
	{
		if(longitude > 180 || longitude < -180)
			return false;
		else
			return true;
	}

	public boolean latitudeOk(double latitude)
	{
		if(latitude > 90 || latitude < -90)
			return false;
		else
			return true;
	}




  //SETTER & GETTER
	public double getLatitude()
	{
		return _latitude;
	}

	public double getLongitude()
	{
		return _longitude;
	}

	public void setLatitude(double latitude)
	{
		if(latitudeOk(latitude)) {
			_latitude = latitude;
		}
	}

	public void setLongitude(double longitude)
	{
		if(longitudeOk(longitude)) {
			_longitude = longitude;
		}
	}




}
