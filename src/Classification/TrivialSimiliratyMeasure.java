package Classification;

import java.util.ArrayList;
import java.util.HashMap;

import model.Question;

public class TrivialSimiliratyMeasure implements SimilarityMeasure<ArrayList<String>>{
	private HashMap<Integer,ArrayList<String>> questionsKeywords;
	private double rate;
	

	public TrivialSimiliratyMeasure(HashMap<Integer, ArrayList<String>> questionsKeywords, double rate) {
		this.questionsKeywords = questionsKeywords;
		this.rate = rate;
	}

	@Override
	public double getSimiliratyMeasure(ArrayList<String> a, ArrayList<String> b) {
		double score = 0;
		for (String word : a) {
			if (b.contains(word)) {
				score++;
			}
		}
		return score / Integer.max(a.size(), b.size());
	}

	@Override
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
