import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;

public class Controller extends Application
{

    private Stage primaryStage;
    private AnchorPane gui;


    @FXML
    protected Button m_valider;

    @FXML
    protected TextField m_minHab;

    @FXML
    protected TextField m_maxDist;

    @FXML
    protected RadioButton m_pigeon;

    @FXML
    protected RadioButton m_coccinelle;

	protected static final String chemin = "data/CommunesFrance.csv";

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        try
        {
            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("Graphy");

            // Permet l'arret du programme lorsque la fenêtre est quit�e
            this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
	            {
	                @Override
	                public void handle(WindowEvent t)
	                {
	                    Platform.exit();
	                    System.exit(0);
	                }
	            });

            // Chargement du rootLayout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Controller.class.getResource("GUI.fxml"));
            gui = (AnchorPane)loader.load();

            // Affichage du rootLayout
            Scene scene = new Scene(gui);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static int toInt(String str)
    {
    	if(str.equals(""))
    		return -1;
    	int num = 0;
    	for(int i = 0; i < str.length(); i++)
    	{
    		char c = str.charAt(i);
    		if((c < '0') || (c > '9'))
    			return -1;
    		num *= 10;
    		num += Integer.parseInt("" + c);
    	}
    	return num;
    }

    @FXML
    public void handleValider() throws IOException
    {
    	System.out.println("Chargement des communes");
    	Graphe graphy = new Graphe(chemin, toInt(m_minHab.getText()));
		graphy.Afficher();
		graphy.Liaisons(toInt(m_maxDist.getText()));
		graphy.AfficherInfos();
    }


    public static void main(String[] args)
    {
        launch(args);
    }

}
