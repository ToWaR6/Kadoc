package classification;

import java.util.ArrayList;
import java.util.HashMap;

import model.Question;

/**
 * Compare questions and get scores of the number of keyword in common in each pair of question
 * 
 * @author Crauser
 *
 */
public class TrivialSimiliratyMeasure {
	private HashMap<Integer,ArrayList<String>> questionsKeywords;
	private double rate;
	

	public TrivialSimiliratyMeasure(HashMap<Integer, ArrayList<String>> questionsKeywords, double rate) {
		this.questionsKeywords = questionsKeywords;
		this.rate = rate;
	}

	/**
	 * Get the similarity of two array of keyword
	 * 
	 * @param a
	 * @param b
	 * @return number of keyword in common divided by the size of the biggest array 
	 */
	public double getSimiliratyMeasure(ArrayList<String> a, ArrayList<String> b) {
		double score = 0;
		for (String word : a) {
			if (b.contains(word)) {
				score++;
			}
		}
		return score / Integer.max(a.size(), b.size());
	}

	/**
	 * Get the most similar question
	 * 
	 * @param question
	 * @return id of the most similar question, or -1 if the score is lower than the minimum rate 
	 */
	public int getMostSimilarQuestion(Question question) {
		int baseQuestionId = question.getId();
		ArrayList<String> baseKeywords = questionsKeywords.get(baseQuestionId);
		double scoreMax = Double.MIN_VALUE;
		int id = -1;
		double scoreTmp;
		for (int idQuestion : questionsKeywords.keySet()) {
			if (baseQuestionId != idQuestion) {
				scoreTmp = getSimiliratyMeasure(baseKeywords, questionsKeywords.get(idQuestion));
				if (scoreTmp == 1) {
					return idQuestion;
				} else if (scoreTmp > scoreMax) {
					id = idQuestion;
					scoreMax = scoreTmp;
				}
			}
		}
		if (scoreMax >= rate) {
			return id;
		} else {
			return -1;
		}
	}
	

}
