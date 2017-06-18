import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Controller extends Application
{

    private Stage primaryStage;
    protected Stage loadStage;
    private AnchorPane gui;
    protected boolean m_progress;

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

    @FXML
    protected ProgressBar m_pConstr;

    @FXML
    protected ProgressBar m_pLiaison;

    @FXML
    protected ProgressBar m_pSlice;

	protected static final String chemin = "data/CommunesFrance.csv";

    @Override
    public void start(Stage primStg) throws Exception
    {
        try
        {
        	m_progress = false;
            this.primaryStage = primStg;
            primaryStage.setTitle("Graphy");

            // Permet l'arret du programme lorsque la fenêtre est quitée
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
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
            Scene scene = null;
        	gui = (AnchorPane)loader.load();
            scene = new Scene(gui);

            // Affichage du rootLayout
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void LoadFenetre()
    {
    	m_valider.setDisable(true);

    	//Création d'une fenêtre servant à afficher la progression lors de la création du graphe
    	Group root = new Group();
    	root.getChildren().removeAll();

    	//Ajout des sommets
    	Label lConstr = new Label();
    	lConstr.setText("Création des sommets : ");
    	lConstr.setLayoutX(8);
    	lConstr.setLayoutY(14);
    	root.getChildren().add(lConstr);
    	m_pConstr = new ProgressBar();
    	m_pConstr.setProgress(0.0);
    	m_pConstr.setLayoutX(185);
    	m_pConstr.setLayoutY(14);
    	m_pConstr.setPadding(new Insets(5.0));
    	root.getChildren().add(m_pConstr);

    	//Ajout des arrêtes
    	Label lSommet = new Label();
    	lSommet.setText("Création des arrêtes : ");
    	lSommet.setLayoutX(8);
    	lSommet.setLayoutY(43);
    	root.getChildren().add(lSommet);
    	m_pLiaison = new ProgressBar();
    	m_pLiaison.setProgress(0.0);
    	m_pLiaison.setLayoutX(185);
    	m_pLiaison.setLayoutY(43);
    	m_pLiaison.setPadding(new Insets(5.0));
    	root.getChildren().add(m_pLiaison);

    	//Suppression des villes non reliées à Paris
    	Label lSlice = new Label();
    	lSlice.setText("Suppression des villes perdues : ");
    	lSlice.setLayoutX(8);
    	lSlice.setLayoutY(72);
    	root.getChildren().add(lSlice);
    	m_pSlice = new ProgressBar();
    	m_pSlice.setProgress(0.0);
    	m_pSlice.setLayoutX(185);
    	m_pSlice.setLayoutY(68);
    	m_pSlice.setPadding(new Insets(5.0));
    	root.getChildren().add(m_pSlice);

    	//Affichage
    	Scene sc = new Scene(root);
    	loadStage = new Stage();
    	loadStage.setTitle("Création du graphe");
    	loadStage.setScene(sc);
    	loadStage.show();

    	m_progress = true;
    }

    public void CloseLoad()
    {
    	Platform.runLater(() ->
    	{
    		m_valider.setDisable(false);
    		loadStage.close();
    	});
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
    	LoadFenetre();
    	Graphe graphy = new Graphe(chemin, toInt(m_minHab.getText()), this);
    	new Thread()
    	{
    		public void run()
    		{
    			while(graphy.getEtape() < 1)
    			{
    				try
    				{
    					Thread.sleep(100);
    				}
    				catch(Exception e)
    				{
    					e.printStackTrace();
    				}
    			}
    			ToggleGroup gr = m_pigeon.getToggleGroup();
				graphy.Liaisons(toInt(m_maxDist.getText()), m_pigeon == gr.getSelectedToggle());
    			while(graphy.getEtape() < 2)
    			{
    				try
    				{
    					Thread.sleep(100);
    				}
    				catch(Exception e)
    				{
    					e.printStackTrace();
    				}
    			}
				graphy.AfficherInfos();
				graphy.Positionner(500, 500);
				CloseLoad();
				graphy.Dijkstra("PARIS", "MARSEILLE");
				Afficher(graphy);
    		}
    	}.start();
    }

    public void setProgress(double p1, double p2, double p3)
    {
    	Platform.runLater(() ->
    	{
	    	if(m_progress)
	    	{
		    	if(p1 >= 0)
		    		m_pConstr.setProgress(p1);
		    	if(p2 >= 0)
		    		m_pLiaison.setProgress(p2);
		    	if(p3 >= 0)
		    		m_pSlice.setProgress(p3);
	    	}
    	});
    }

    public void Afficher(Graphe graphy)
    {
    	Platform.runLater(() ->
    	{
    		graphy.AfficherGUI();
    	});
    }


    public static void main(String[] args)
    {
        launch(args);
    }

}
