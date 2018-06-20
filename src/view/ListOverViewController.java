package view;

import controller.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;


public class ListOverViewController {
	@FXML
	private ProgressBar progressBar;
	
	private MainApp mainApp;
	private double progress;
	
	@FXML
	private void initialize() {
		this.progress = 0;
	}
	
	public void setMainApp(MainApp mainApp) {
		System.out.println(this);
		this.mainApp = mainApp;
		if(this.mainApp!=null)
			System.out.println("this.mainApp est init");
	}
	@FXML
	private void handleProgressBar() {
		progress+=0.1;
		progressBar.setProgress(progress);
		this.mainApp.display("Travail terminé");
	}
}
