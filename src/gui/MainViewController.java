package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable{

	//Variaveis do menu
	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	
	//teste para clique nas opcoes do menu
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction clicado");
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml");
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
    }
    
    //essa aplicacao grafica te, multitread para nao interromper para nao dar nada de estranho usa synchronized
    private synchronized void loadView(String absoluteName){
        try{
        	// Cria um novo loader usando o string passado como caminho
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox newVBox = loader.load();

            Scene mainScene = Main.getMainScene();	
            //pegando referencia para o vbox
            //mainScene getRoot - pega o primeiro elemento da minha View no caso dessa é o ScrollPane
            //o get content é o que esta dentro do scrollpane no caso o VBox
            VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

            //quardando referencia do menu - pega o primeiro filho do vbox da janela principal no caso o mainMenu
            Node mainMenu = mainVBox.getChildren().get(0);
            //Apaga todos os children do vbox
            mainVBox.getChildren().clear();
            //adicionando os filhos do newVBox
            //adiciona o mainMenu
            mainVBox.getChildren().add(mainMenu);
            //adiciona os filhos do newVBOX
            mainVBox.getChildren().addAll(newVBox.getChildren());

        } catch (IOException e){
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
        
    }

}
