package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Class;
import model.Question;
import view.ChooseClassDialogController;
/**
 * Open a dialog to choose if the question belong in a class
 * @author Florent
 *
 */
public class ChooseDialog implements Runnable{
	private MainApp mainApp;
	private CountDownLatch countDownLatch;
	private AnchorPane page;
	private Stage dialogStage;
	private Class questionClass;
	
	/**
	 * Constructor of the Runnable
	 * @param mainApp The controller mainApp 
	 * @param aClass The possible class	
	 * @param question The question to put in the class
	 */
	public ChooseDialog(MainApp mainApp,Class aClass)
	{
		this.mainApp = mainApp;
		this.questionClass = aClass;
	}
	
	/**
	 * Allow to use a countDownLatch to synchronize the thread
	 * @param countDownLatch
	 * @return this
	 */
	public ChooseDialog withCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
		return this;
	}
	@Override
	public void run() {
	        try {
	            // Load the fxml file and create a new stage for the popup dialog.
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(ChooseDialog.class.getResource("/view/ChooseClassDialog.fxml"));
	            page = (AnchorPane) loader.load();
	            ChooseClassDialogController controller = loader.getController();
	            controller.initDialog(mainApp, questionClass);
	            // Create the dialog Stage.
	            dialogStage = new Stage();
	            dialogStage.setResizable(false);
	            dialogStage.setTitle("Question in class");
	            dialogStage.initOwner(mainApp.getPrimaryStage());
	            dialogStage.initModality(Modality.APPLICATION_MODAL);
	            dialogStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/view/resources/images/icon.png")));
	            Scene scene = new Scene(page);
	            dialogStage.setScene(scene);
	            // Show the dialog and wait until the user closes it
	            dialogStage.showAndWait();
	            if(controller.isRemoved()) {
		            ArrayList<Question> questions = controller.getSelectedQuestion();
		            for(Question q : questions) {
		            	questionClass.getQuestions().remove(q);
		            }	
	            }
	            if(countDownLatch != null)
	            	countDownLatch.countDown();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}

}
