package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	// faz uma dependencia do Seller para fazer o inserts e updates
	private Seller entity;

	// dependencia com o Seller service
	private SellerService service;

	// dependencia com o department
	private DepartmentService departmentService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	// criar o obs list para pegar os departamentos
	private ObservableList<Department> obsList;

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		try {
			// pega os dados que estao nos campos e coloca no entity
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			// fecha a janela depois do update
			Utils.currentStage(event).close();

		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}

	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Seller obj = new Seller();

		// instancia o erro pela classe validationexception model.exception
		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		// verifica se o nome do vendedor é nulo ou vazio
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());

		// verifica se o email do vendedor é nulo ou vazio
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());

		// Assim que pega o valor que esta no datepicker usando instant
		if (dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		}else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		// verifica se o salario do vendedor é nulo ou vazio
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSalary", "Field can't be empty");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue());
		
		// se tiver 1 ou mais erros devolve a exceção
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;

	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		// fecha a janela depois do update
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rs) {
		initializeNodes();

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 60);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");

		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Seller was null");
		}

		// muda pra string pq o txtId ï¿½ string e nao int
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}

		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}

	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null!");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		//é o if else enxuto se o fild for verdadeiro da o erro se nao deixa em branco
		labelErrorName.setText(fields.contains("name") ? errors.get("name") : "");
		
		labelErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");
		
		labelErrorBirthDate.setText(fields.contains("birthDate") ? errors.get("birthDate") : "");
		
		labelErrorBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary") : "");
		
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
