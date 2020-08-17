package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {
	
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
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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
	
}
