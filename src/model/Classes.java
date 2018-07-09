package model;

import java.util.ArrayList;
/**
 * This class is used to get statistic of a set of Class
 * @author Dell'omo
 *
 */
public class Classes {
	ArrayList<Class> classes;
	public Classes() {
		classes = new ArrayList();
	}
	/**
	 * Constructor to initialize the classes with a already create ArrayList of classes
	 * @param classes, a ArrayList of Class
	 */
	public Classes(ArrayList<Class> classes) {
		this.classes = classes;
	}
	
	/**
	 * Fired to add a Class to the ArrayList of classes 
	 * @param aClass, a class containing keywords
	 * @return The caller
	 */
	public Classes addClass(Class aClass) {
		classes.add(aClass);
		return this;
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
	/**
	 * This function is called to get the class with the maximum number of keywords
	 * @return a class with the largest number of keyword, if there is no class return null
	 */
	public Class getClassWithMaximumKeyword() {
		int maximumKeywords = 0;
		Class res = null;
		for(Class aClass : classes) {
			if(aClass.getKeywords().size()>maximumKeywords) {
				maximumKeywords = aClass.getKeywords().size();
				res = aClass;
			}
		}
		return res;
	}
}
