package controller;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.ListOverViewController;

public class MainApp extends Application {

    private Stage primaryStage;
    private FlowPane rootLayout;
    private Scene scene;
    
    public MainApp() {
    
    }
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Kadoc");
        initRootLayout();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/ListOverView.fxml"));
            rootLayout = (FlowPane) loader.load();
            scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            ListOverViewController listOverViewController = loader.getController();
            listOverViewController.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void display(String s) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle(s);
    	fileChooser.showOpenDialog(this.primaryStage);
    }
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public static void main(String[] args) {
        launch(args);
    }
}