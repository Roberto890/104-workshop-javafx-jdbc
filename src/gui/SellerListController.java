package gui;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;

	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML // primeiro entidade e depois coluna
			// nesse caso Seller e o tipo inteiro
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<Seller, String> tableColumnName;

	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;

	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;

	@FXML
	private Button btNew;

	// carregar os departamentos na observableList
	// ta na funcao updateTableView
	private ObservableList<Seller> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		// coloca o obj aqui para ir para o SellerFormController
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	// iniciando apropriadamente as colunas da tabela
	private void initializeNodes() {

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// deixar a tabela(Seller view acompanhar a tela)no programa
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service can't be null!");
		}

		List<Seller> list = service.findAll();
		// instanciando o observable list com os dados do findAll
		obsList = FXCollections.observableArrayList(list);
		// vinculando a observable list e a table view
		tableViewSeller.setItems(obsList);
		// codigo para acresentar um novo botao com o texto edit em cada linha na tabela
		// e abre o formulario de edi��o com os dados preenchidos
		initEditButtons();
		//abre um novo botao para deletar o departamento
		initRemoveButtons();
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//
//			SellerFormController controller = loader.getController();
//			controller.setSeller(obj);
//			controller.setSellerService(new SellerService());
//			controller.subscribeDataChangeListener(this);
//			controller.updateFormData();
//
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Enter Seller data");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false);
//			dialogStage.initOwner(parentStage);
//			// serve para barras a janela embaixo so pode usar quando fechar a janela que
//			// abrir
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.showAndWait();
//
//		} catch (IOException e) {
//			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}

		});
	}
	

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			} try {
				service.remove(obj);
				updateTableView();
			} catch (DbException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
		
	}
}