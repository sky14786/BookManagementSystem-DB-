package application.kh.bms.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.kh.bms.controller.BookSearchController;
import application.kh.bms.controller.RentalController;
import application.kh.bms.model.dao.InformationManager;
import application.kh.bms.model.service.BookModelService;
import application.kh.bms.model.service.UserService;
//import application.kh.bms.model.dao.LoadSave;
import application.kh.bms.model.vo.BookModel;
import application.kh.bms.model.vo.BookTable;
import application.kh.bms.model.vo.SelectedBook;
import application.kh.bms.model.vo.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MainSearchView implements Initializable {

	@FXML
	private Button btnSelect; // 조회
	@FXML
	private Button btnAll; // 전체보기
	@FXML
	private Button lookDetailBtn; // 상세보기
	@FXML
	private Button rentalListBtn;// 대여목록
	@FXML
	private Button btnGoUserSearch; // 회원관리버튼
	@FXML
	private Button btnGoAdminMain; // 도서관리버튼
	@FXML
	private Button btnUserDelete;
	@FXML
	private Button btnLogin; // 뒤로가기
	@FXML
	private Button btnNowRental; // 바로대여버튼

	@FXML
	private TextField tfWord;
	@FXML
	private Label lblNullCheck;

	@FXML
	private ComboBox<String> comboBox; // 구분선택

	@FXML
	private TableView<BookTable> tableView;
	@FXML
	private TableColumn<BookTable, String> codeCol; // 번호
	@FXML
	private TableColumn<BookTable, String> nameCol; // 도서명
	@FXML
	private TableColumn<BookTable, String> authorCol; // 저자
	@FXML
	private TableColumn<BookTable, String> pubCol; // 출판사
	@FXML
	private TableColumn<BookTable, String> cateCol; // 장르
	@FXML
	private TableColumn<BookTable, Boolean> rentalCol;// 대여여부

	private RentalController rentalController = new RentalController();

	static BookTable model = new BookTable();
	private DetailPageView detailPageView = new DetailPageView();

	private BookSearchController bookSearchController = new BookSearchController();
	private List<BookTable> books = new ArrayList<BookTable>();

	public int row = -1;

	private ObservableList<String> comboList = FXCollections.observableArrayList("도서명", "저자", "출판사", "장르");

	private String tfsel = ""; // 텍스트필드 저장용
	private String combosel = ""; // 콤보박스 저장용

	private BookModelService bs = new BookModelService();
	private UserService us = new UserService();
	private InformationManager im = InformationManager.getInformationManager();
	private List<BookModel> temp = bs.selectAll();
	private List<User> temp2 = us.selectAll();
	public ObservableList<BookTable> bookList = FXCollections.observableArrayList();

	private String userId = im.getNowUser().getId();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnGoUserSearch.setVisible(false);
		btnGoAdminMain.setVisible(false);

		if (InformationManager.getInformationManager().getNowUser().isAdminCheck()) {
			btnGoUserSearch.setVisible(true);
			btnGoAdminMain.setVisible(true);

		}

		codeCol.setCellValueFactory(new PropertyValueFactory<BookTable, String>("code"));
		nameCol.setCellValueFactory(new PropertyValueFactory<BookTable, String>("bookName"));
		authorCol.setCellValueFactory(new PropertyValueFactory<BookTable, String>("author"));
		pubCol.setCellValueFactory(new PropertyValueFactory<BookTable, String>("publishingHouse"));
		cateCol.setCellValueFactory(new PropertyValueFactory<BookTable, String>("category"));
		rentalCol.setCellValueFactory(new PropertyValueFactory<BookTable, Boolean>("rental"));

		books = bookSearchController.bookTableLoad();

		for (int i = 0; i < books.size(); i++) {
			BookTable temp = books.get(i);
			bookList.add(books.get(i));
		}
		tableView.setItems(bookList);

		comboBox.setItems(comboList);

		lookDetailBtn.setDisable(true);

		tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BookTable>() {
			public void changed(ObservableValue<? extends BookTable> observable, BookTable oldValue,
					BookTable newValue) {
				lookDetailBtn.setDisable(false);
				model = tableView.getSelectionModel().getSelectedItem();

				if (model.getRental().getValue()) {
					btnNowRental.setDisable(true);
				} else {
					btnNowRental.setDisable(false);
				}
			}
		});

	}

	public void viewAll() {

		bookList.clear();
		for (int i = 0; i < books.size(); i++) {
			bookList.add(books.get(i));
		}
		tableView.setItems(bookList);

		tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BookTable>() {
			public void changed(ObservableValue<? extends BookTable> observable, BookTable oldValue,
					BookTable newValue) {
				lookDetailBtn.setDisable(false);
				model = tableView.getSelectionModel().getSelectedItem();

				System.out.println("선택된 Item의 Index" + tableView.getSelectionModel().getSelectedIndex());
				System.out.println("선택된 행의 도서코드 : " + model.getCode());
			}
		});

	}

	public void selectSearch() {
		ArrayList<BookTable> tempBooks = new ArrayList<BookTable>();
		tfsel = tfWord.getText();
		combosel = comboBox.getValue();

		if (tfsel.isEmpty() || combosel == null) {
			lblNullCheck.setText("검색조건을 입력해주세요");
		} else {

			int j = 0;
			switch (combosel) {
			case "도서명":
				for (j = 0; j < books.size(); j++) {
					if (books.get(j).getBookName().contains(tfsel)) {
						tempBooks.add(books.get(j));
					}
				}
				break;
			case "저자":
				for (j = 0; j < books.size(); j++) {
					if (books.get(j).getAuthor().contains(tfsel)) {
						;
						tempBooks.add(books.get(j));
					}
				}
				break;
			case "출판사":
				for (j = 0; j < books.size(); j++) {
					if (books.get(j).getPublishingHouse().contains(tfsel)) {
						tempBooks.add(books.get(j));
					}

				}
				break;
			case "장르":
				for (j = 0; j < books.size(); j++) {
					if (books.get(j).getCategory().contains(tfsel)) {
						tempBooks.add(books.get(j));
					}
				}
				break;
			}
			bookList.clear();
			for (int i = 0; i < tempBooks.size(); i++) {
				bookList.add(tempBooks.get(i));
			}
			tableView.setItems(bookList);
		}
	}

	@FXML
	public void changeToRentalList() {
		try {
			Stage newStage = new Stage();
			Parent root = FXMLLoader
					.load(getClass().getClassLoader().getResource("application/kh/bms/view/RentalList.fxml"));
			Scene scene = new Scene(root);
			newStage.setScene(scene);
			newStage.setTitle("대여목록");
			newStage.show();

			Stage primaryStage = (Stage) rentalListBtn.getScene().getWindow();
			primaryStage.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void changeToDetailPage() {

		try {

			InformationManager.getInformationManager().setNowBook(bs.oneBookSelect(model.getCode()));

			Stage newStage = new Stage();
			Parent root = FXMLLoader
					.load(getClass().getClassLoader().getResource("application/kh/bms/view/DetailPage.fxml"));
			Scene scene = new Scene(root);
			newStage.setScene(scene);
			newStage.setTitle("도서상세보기");
			newStage.show();

			Stage primaryStage = (Stage) lookDetailBtn.getScene().getWindow();
			primaryStage.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void GoUserSearch() {

		try {
			Stage newStage = new Stage();
			Parent root = FXMLLoader
					.load(getClass().getClassLoader().getResource("application/kh/bms/view/userSearch.fxml"));
			Scene scene = new Scene(root);
			newStage.setScene(scene);
			newStage.setTitle("회원관리");
			newStage.show();

			Stage primaryStage = (Stage) btnGoUserSearch.getScene().getWindow();
			primaryStage.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void GoAdminMain() {

		try {
			Stage newStage = new Stage();
			Parent root = FXMLLoader
					.load(getClass().getClassLoader().getResource("application/kh/bms/view/AdminSearch.fxml"));
			Scene scene = new Scene(root);
			newStage.setScene(scene);
			newStage.setTitle("도서관리");
			newStage.show();

			Stage primaryStage = (Stage) btnGoAdminMain.getScene().getWindow();
			primaryStage.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void UserDelete() {
		try {
			Stage newStage = new Stage();
			Parent root = FXMLLoader
					.load(getClass().getClassLoader().getResource("application/kh/bms/view/UserDelete.fxml"));
			Scene scene = new Scene(root);
			newStage.setScene(scene);
			newStage.setTitle("회원관리");
			newStage.show();

			Stage primaryStage = (Stage) btnUserDelete.getScene().getWindow();
			primaryStage.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void GoLogin() {

		try {
			Stage newStage = new Stage();
			Parent root = FXMLLoader
					.load(getClass().getClassLoader().getResource("application/kh/bms/view/Login.fxml"));
			Scene scene = new Scene(root);
			newStage.setScene(scene);
			newStage.setTitle("로그인");
			newStage.show();

			Stage primaryStage = (Stage) btnLogin.getScene().getWindow();
			primaryStage.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void nowRental() {
		System.out.println("바로대여되나?");
		System.out.println(model.getCode());
		rentalController.addRetalBook(model.getCode());

		rentalController.bookRentalUpdate("1", model.getCode());

		bookList.clear();
		books = bookSearchController.bookTableLoad();
		for (int i = 0; i < books.size(); i++) {
			bookList.add(books.get(i));
		}
		tableView.setItems(bookList);

	}

}
