package classification;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import model.Question;

/**
 * Calculate tfidf score for StackOverflow's questions
 * 
 * @author Crauser
 *
 */
public class TFIDF {

	private HashMap<Integer, HashMap<String, Double>> mapScore;
	private String[] whiteList; // all single word keyword + multi word keyword (with space replaced by underscore)
	private String[] multiWordTermArray; // multi word term (with space between words)
	
	//CONSTRUCTOR
	public TFIDF (String[] whiteList, String[] multiWordTermArray) {
		this.whiteList = whiteList;
		this.multiWordTermArray = multiWordTermArray;
		this.mapScore = new HashMap<Integer, HashMap<String, Double>>();
	}
	
	//GETTER & SETTER
	public HashMap<Integer, HashMap<String, Double>> getMapScore() {
		return mapScore;
	}

	//FONCTION
	
	/**
	 * Transform multi-word terms in questions' title and body to Single-word term by adding '_' between them
	 * 
	 * @param mapQuestion
	 */
	public void transformMultiWordTermToSingleWordTerm (HashMap<Integer, Question> mapQuestion) {
		for (Question question : mapQuestion.values()) {
			for (String multiWord : this.multiWordTermArray) {
				String singleWord = multiWord.replaceAll(" ", "_");
				question.setTitle(StringUtils.replaceIgnoreCase(question.getTitle(), multiWord, singleWord));
				question.setBody(StringUtils.replaceIgnoreCase(question.getBody(), multiWord, singleWord));
			}
		}
	}
	
	/**
	 * Transform questions with title, body, etc. in list of keywords present in the white list
	 * 
	 * @param mapQuestion
	 * @return HashMap of questions, id of the question and list of keyword of this question
	 */
	public HashMap<Integer, String[]> transformQuestionToListOfKeyword (HashMap<Integer, Question> mapQuestion) {
		HashMap<Integer, String[]> questionsKeyword = new HashMap<Integer, String[]>();
		ArrayList<String> tmpListKeyword = new ArrayList<String>();
		String[] splitText;
		for (int id : mapQuestion.keySet()) {
			//concat lower case question's title and body and split it by all that is not a letter, a number, a dash or an underscore (for the multiword transformed)
			splitText = (mapQuestion.get(id).getTitle()+mapQuestion.get(id).getBody()).toLowerCase().split("[^A-z0-9-_]+");
			for (String splitWord : splitText) {
				for (String whiteListWord : this.whiteList) {
					if (splitWord.equals(whiteListWord)) {
						tmpListKeyword.add(whiteListWord);
						break;
					}
				}
			}
		questionsKeyword.put(id, tmpListKeyword.toArray(new String[0]));
		tmpListKeyword.clear();
		}
		
		return questionsKeyword;
	}
	
	/**
	 * Calculate the term frequency a keyword in a question's title and body
	 * 
	 * @param keyword
	 * @param question
	 * @return the occurrence of the word in the question's title and body
	 */
	public int tf (String keyword, Question question) {
		String titleNbody = (question.getTitle()+question.getBody()).toLowerCase();
		return StringUtils.countMatches(titleNbody, keyword);
	}
	
	/**
	 * Calculate the inverse document frequency of each keyword in a list of keyword
	 * 
	 * @param questionKeyword
	 * @param mapQuestion
	 * @return HashMap of word with idf score
	 */
	public HashMap<String, Double> idf (String[] keywords, HashMap<Integer, Question> mapQuestion) {
		HashMap<String, Double> idfScore = new HashMap<String, Double>();
		int nbQuestionPresent;
		double score;
		for (String word : keywords) {
			nbQuestionPresent = 0;
			for (Question question : mapQuestion.values()) {
				if ((question.getTitle()+question.getBody()).toLowerCase().contains(word)) {
					nbQuestionPresent++;
				}
				
			}
			double div = (double)mapQuestion.size() / (double)nbQuestionPresent;
			score = Math.log(div);
			idfScore.put(word, score);
		}
		return idfScore;
	}
	
	/**
	 * Calculate the tf idf of each keyword from each question
	 * 
	 * @param mapQuestion
	 * @return HashMap < idQuestion, HashMap < word, score tf idf > >
	 */
	public HashMap<Integer, HashMap<String, Double>> calculate (HashMap<Integer, Question> mapQuestion) {
		transformMultiWordTermToSingleWordTerm(mapQuestion);
		HashMap<Integer, String[]> questionsKeyword = transformQuestionToListOfKeyword(mapQuestion);
		Set<String> tmpUniqueKeyword = new HashSet<String>();
		//idf score
		for (String[] keywords : questionsKeyword.values()) {
			for (String word : keywords) {
				tmpUniqueKeyword.add(word);
			}
		}
		String[] allKeywords = tmpUniqueKeyword.toArray(new String[0]);
		tmpUniqueKeyword.clear();
		HashMap<String, Double> idfScore = idf(allKeywords, mapQuestion);
		
		//tf idf score
		int tfScore;
		double score;
		for (int id : mapQuestion.keySet()) {
			HashMap<String, Double> tfidfScore = new HashMap<String, Double>(); 
			for (String word : questionsKeyword.get(id)) {
				tfScore = tf(word, mapQuestion.get(id));
				if (tfScore > 0) {
					score = tfScore * idfScore.get(word);
					tfidfScore.put(word, score);
				}
			}
			
			this.mapScore.put(id, tfidfScore);
		}
		return this.mapScore;
	}
	
	/**
	 * Generate a file .arff for Weka
	 * 
	 * @param pathFile
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void generateArffFile (String pathFile) throws FileNotFoundException, UnsupportedEncodingException {
		HashMap<String, Integer> allkeywords = new HashMap<String, Integer>();
		Arrays.sort(this.whiteList);
		
		PrintWriter writer = new PrintWriter(pathFile, "UTF-8");
		writer.println("@RELATION keywordsTFIDF\n");
		int cpt = 0;
		
		for (String str : this.whiteList) {
			writer.println("@ATTRIBUTE "+str+" NUMERIC");
			allkeywords.put(str, cpt++);
		}
		writer.println("\n@DATA");
		
		String[] keywordArray;
		for (HashMap<String, Double> map : this.mapScore.values()) {
			writer.print("{");
			cpt = 0;
			keywordArray = new String[map.keySet().size()];
			for (String str : map.keySet()) {
				keywordArray[cpt++] = str;
			}
			Arrays.sort(keywordArray);
			int size = 0;
			for (String str : keywordArray) {
				writer.print(allkeywords.get(str)+" "+ String.format(Locale.US, "%.3f", map.get(str)));
				if (size++ < keywordArray.length - 1) {
					writer.print(", ");
				}
			}
			writer.println("}");
		}
		writer.close();
	}
	
}
