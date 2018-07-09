package model;

import java.util.ArrayList;

public class Classes {
	ArrayList<Class> classes;
	public Classes() {
		classes = new ArrayList();
	}
	
	/**
	 * This function is called to know the average number of questions in the classes
	 * @return a double that is the average of questions in the classes 
	 */
	public double getAverageQuestions() {
		int questionsCount = 0;
		for(Class aClass : classes) {
			questionsCount+=aClass.getSizeQuestions();
		}
		return (double)questionsCount/classes.size();
	}
	/**
	 * This function is called to know the average of keywords in the classes
	 * @return a double that is the average of questions in the classes
	 */
	public double getAverageKeywords() {
		int keywordsCount = 0;
		for(Class aClass : classes) {
			keywordsCount += aClass.getKeywords().size();
		}
		return (double)keywordsCount/classes.size();
	}
}
