package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
import model.services.DepartmentService;
import model.services.SellerService;

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
		loadView("/gui/SellerList.fxml",(SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
									//expressao lambda para o updateview
		loadView("/gui/DepartmentList.fxml",(DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemAboutAction() {
								//expressaoa lamda zerada pq a about nao faz nada
		loadView("/gui/About.fxml", x -> {});
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
    }
    
    //essa aplicacao grafica te, multitread para nao interromper para nao dar nada de estranho usa synchronized
    private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction){
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
            
            //ativando a expressaoa lambda passada
            //como que esta T � pra ser generico entaoa ceita o about,department variaveis etc
            T controller = loader.getController();
            initializingAction.accept(controller);
            
        } catch (IOException e){
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
        
    }
    
//    private synchronized void loadView2(String absoluteName){
//        try{
//        	// Cria um novo loader usando o string passado como caminho
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//            VBox newVBox = loader.load();
//            Scene mainScene = Main.getMainScene();
//            VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
//            Node mainMenu = mainVBox.getChildren().get(0);
//            mainVBox.getChildren().clear();            
//            mainVBox.getChildren().add(mainMenu);       
//            mainVBox.getChildren().addAll(newVBox.getChildren());
//            
//            //o loader tambem tem como pegar o controller da view
//            DepartmentListController controller = loader.getController();
//            controller.setDepartmentService(new DepartmentService());
//            controller.updateTableView();
//            
//        } catch (IOException e){
//            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
//        }
//        
//    }


}
