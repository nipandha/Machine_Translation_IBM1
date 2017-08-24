import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Corpus {

	String fname,train_file,test_file;

	List<ArrayList<String>> test_sentences,train_sentences;
	int train_to_test;
	public int getTrain_to_test() {
		return train_to_test;
	}

	public void setTrain_to_test(int train_to_test) {
		this.train_to_test = train_to_test;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}
	public void read_data()
	{
		train_sentences=new ArrayList<ArrayList<String>>();
		try {
			BufferedReader br=new BufferedReader(new FileReader(new File(fname)));
			String line;
			int count=0;
			ArrayList<String> current_sentence=new ArrayList<String>();
			List<String> next_line_caps=new ArrayList<String>();
			while((line=br.readLine())!=null)
			{
				current_sentence=new ArrayList<>();
				if(line=="")
					continue;
				String[] words = line.split("\\s+");
				for(String word: words)
				{
					current_sentence.add(word);
				}
				train_sentences.add(current_sentence);
			}
			//System.out.println("No of sents is "+train_sentences.size());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void read_data1()
	{
		train_sentences=new ArrayList<ArrayList<String>>();
		
		String[] abbrev_before_caps={"Mr.","Mrs.","Ms.","Dr."};
		try {
			BufferedReader br=new BufferedReader(new FileReader(new File(fname)));
			String line;
			int count=0;
			ArrayList<String> current_sentence=new ArrayList<String>();
			List<String> next_line_caps=new ArrayList<String>();
			while((line=br.readLine())!=null)
			{
				if(line=="")
					continue;
				
				String[] words = line.split("\\s+");
				int w_index=0,n=words.length;
				
				for(String word: words)
				{
					word = word.replace(",", "");
					word = word.replace(":", "");
					if (word.length()>0) {
						
						if (w_index == 0) {
							if (next_line_caps.size() > 0) {
								char nextWordStart = word.charAt(0);
								String word_prev = next_line_caps.get(0);

								if (Character.isUpperCase(nextWordStart)) {

									if (word_prev.endsWith(".")
											|| word_prev.endsWith("?")) {
										String punct = Character.toString(word_prev
												.charAt(word_prev.length() - 1));
										word_prev = removeLastOccurence(word_prev);
										current_sentence.add(word_prev);
										//current_sentence.add(punct);
									} else
										current_sentence.add(word_prev);

									train_sentences.add(current_sentence);

									current_sentence = new ArrayList<String>();

								} else
									current_sentence.addAll(next_line_caps);
								next_line_caps.clear();
							}
						}
						if ((w_index == n - 1)) {
							next_line_caps.add(word);

						} else if ((word.endsWith(".") || word.endsWith("?"))
								&& ((Character.isUpperCase(words[w_index + 1]
										.charAt(0))))
								&& (!Arrays.asList(abbrev_before_caps)
										.contains(word))) {

							String punct = Character.toString(word.charAt(word
									.length() - 1));
							word = removeLastOccurence(word);
							current_sentence.add(word);
							//current_sentence.add(punct);
							train_sentences.add(current_sentence);

							current_sentence = new ArrayList<String>();
						} else
							current_sentence.add(word);
						w_index++;
					}
				}
			}
			if(next_line_caps.size()>0)
			{
				String word1=next_line_caps.get(0);
				if (word1.endsWith(".")||word1.endsWith("?")) {
					String punct = Character.toString(word1
							.charAt(word1.length() - 1));
					word1 = removeLastOccurence(word1);
					current_sentence.add(word1);
					//current_sentence.add(punct);
				}
				else
					current_sentence.add(word1);
				
				train_sentences.add(current_sentence);
			}
			System.out.println(train_sentences.size());
		} catch (Exception e) {
					e.printStackTrace();
		} 
	}
	private String removeLastOccurence(String word) {
		char[] chars = word.toCharArray();
		String w="";
		for(int i=0;i<chars.length-1;i++)
			w+=chars[i];
		// TODO Auto-generated method stub
		return w;
	}

	public static void main(String[] args)
	{
		Corpus french=new Corpus();
		french.setFname("/home/user/Documents/Course_Books/NLP/Project/small");
		french.read_data();
		french.setTrain_to_test(70);
		french.split_data();
	}

	public void split_data() {
		// TODO Auto-generated method stub
		int test_no=train_sentences.size()*(100-train_to_test)/100;
		
		test_sentences=new ArrayList<ArrayList<String>>();
		
		
		for(int j=1;j<=test_no;j++)
		{
			int random = (int )(Math.random() *train_sentences.size() );
			test_sentences.add(train_sentences.remove(random));
			
		}
		//System.out.println(train_sentences);
		
	}
}
