package classification;

import model.Question;

public interface SimilarityMeasure<T> {
	public double getSimiliratyMeasure(T a,T b);
	public int getMostSimilarQuestion(Question question);
}
