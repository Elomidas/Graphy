import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    Button m_browse;


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        try
        {
            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("Client");

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

    @FXML
    public void handleValider() throws IOException
    {
    	//
    }

    @FXML
    public void browse() throws IOException
    {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Importer des données");
    	fileChooser.getExtensionFilters().add(
    	         new ExtensionFilter("Fichiers CSV", "*.csv"));
    	fileChooser.showOpenDialog(primaryStage);
    }


    public static void main(String[] args)
    {
        launch(args);
    }

}
