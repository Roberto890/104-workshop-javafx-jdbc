package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {
	
	private DepartmentService service;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML			//primeiro entidade e depois coluna
					//nesse caso Department e o tipo inteiro
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	//carregar os departamentos na observableList
	// ta na funcao updateTableView
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		//coloca o obj aqui para ir para o departmentFormController
		createDialogForm(obj, "/gui/departmentForm.fxml", parentStage);
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	//iniciando apropriadamente as colunas da tabela
	private void initializeNodes() {
		
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		//deixar a tabela(department view acompanhar a tela)no programa
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service can't be null!");
		}
		
		List<Department> list = service.findAll();
		//instanciando o observable list com os dados do findAll
		obsList = FXCollections.observableArrayList(list);
		//vinculando a observable list e a table view
		tableViewDepartment.setItems(obsList);
	}
	
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			 FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			 Pane pane = loader.load();
			 
			 DepartmentFormController controller = loader.getController();
			 controller.setDepartment(obj);
             controller.setDepartmentService(new DepartmentService());
             controller.subscribeDataChangeListener(this);
			 controller.updateFormData();
			 
			 Stage dialogStage = new Stage();
			 dialogStage.setTitle("Enter Department data");
			 dialogStage.setScene(new Scene(pane));
			 dialogStage.setResizable(false);
			 dialogStage.initOwner(parentStage);
			 //serve para barras a janela embaixo so pode usar quando fechar a janela que abrir
			 dialogStage.initModality(Modality.WINDOW_MODAL);
			 dialogStage.showAndWait();
			 
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
    }
    
    @Override
	public void onDataChanged() {
		updateTableView();
	}
	
}
