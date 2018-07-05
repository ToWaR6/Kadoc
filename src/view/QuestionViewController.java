package view;

import controller.MainApp;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import model.Question;

/**
 * Controller for an anchor pane that display a question
 * @author Dell'omo
 */
public class QuestionViewController{
	@FXML
	private Label title;
	@FXML
	private Label body;
	@FXML
	private GridPane gridPane;
	
	private MainApp mainApp;
	private Question question;
	private HostServices hostServices ;
	private boolean selected = false;
	public void setQuestion(MainApp mainApp, Question question){
		this.mainApp = mainApp;
		this.question = question;
		this.title.setText(question.getTitle());
		this.body.setText(question.getBody());
	}
	
	/**
	 * Called when the user clicks on the see more button.
	 */
	@FXML
	private void handleSeeMore() {
		String url = "https://stackoverflow.com/questions/"+question.getId();
	    mainApp.getHostServices().showDocument(url);
	}
	/**
	 * Called when the user click on the pane
	 */
	@FXML
	private void selectPane() {
		if(!selected) {
			BorderStroke borderStroke = new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT);
			this.gridPane.setBorder(new Border(borderStroke));
			selected=true;
		}else {
			this.gridPane.setBorder(null);
			selected=false;
		}
	}
	
	public boolean isSelected() {
		return selected;
	}
}