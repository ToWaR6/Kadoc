package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Class;
import model.Question;
import view.QuestionViewController;
/**
 * Open a dialog to choose if the question belong in a class
 * @author Florent
 *
 */
public class ChooseDialog implements Runnable{
	private MainApp mainApp;
	private CountDownLatch countDownLatch;
	private Class questionClass;
	private AnchorPane page;
	private Stage dialogStage;
	private HashMap<Pane,QuestionViewController> controllers;
	private GridPane gridPane;
	/**
	 * Constructor of the Runnable
	 * @param mainApp The controller mainApp 
	 * @param aClass The possible class	
	 * @param question The question to put in the class
	 */
	public ChooseDialog(MainApp mainApp,Class aClass)
	{
		controllers = new HashMap();
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
	            // Create the dialog Stage.
	            dialogStage = new Stage();
	            dialogStage.setResizable(false);
	            dialogStage.setTitle("Question in class");
	            dialogStage.initOwner(mainApp.getPrimaryStage());
	            dialogStage.initModality(Modality.APPLICATION_MODAL);
	            dialogStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/view/resources/images/icon.png")));
	            initGridPane();//Initialiser les panel
	            Scene scene = new Scene(page);
	            dialogStage.setScene(scene);
	            // Show the dialog and wait until the user closes it
	            dialogStage.showAndWait();
	            
	            //Process on question selected
	            for(Node n:gridPane.getChildren()) {
	            	if(controllers.get(n).isSelected()){
	            		System.out.println("Remove from class");
	            	}
	            }
	            if(countDownLatch != null)
	            	countDownLatch.countDown();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	private AnchorPane getQuestionPane(Question question) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/QuestionView.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		QuestionViewController controller = loader.getController();
		controller.setQuestion(this.mainApp,question);
		controllers.put(pane,controller);
		return pane;
	}
	private void initGridPane() throws IOException {
		gridPane = new GridPane();
        int widthMax = 3;
        int height = 0;
        Question question;
        Iterator<Question> iteratorQuestion = this.questionClass.getQuestions().iterator();
        while(iteratorQuestion.hasNext()){
        	for(int width = 0; width<widthMax && iteratorQuestion.hasNext();width++) {
        		question = iteratorQuestion.next();
        		AnchorPane questionPane = getQuestionPane(question);
        		gridPane.add(questionPane, width, height);
        	}
        	height++;
        }
        page.getChildren().add(gridPane);
	}

}
