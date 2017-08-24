import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jdk.management.resource.internal.TotalResourceContext;


public class Model {

	ArrayList<ArrayList<String>> eng_sentences;
	ArrayList<ArrayList<String>> for_sentences;
	ArrayList<String> eng_words;
	ArrayList<String> for_words;
	//ArrayList<ArrayList<String>> possibilities;
	ArrayList<ArrayList<Integer>> poss_index;
	Double[][] t,count_ef;
	int lf,le;
	int CONVERGE=70;
	Double[] total_f, total_s;
	Model(Corpus eng, Corpus foreign)
	{
		eng_sentences=new ArrayList<ArrayList<String>>();
		for_sentences=new ArrayList<ArrayList<String>>();
		for(ArrayList<String> sent: foreign.train_sentences)
		{
			ArrayList<String> p=new ArrayList<>();
			for(String word: sent)
			{
				p.add(new String(word));
			}
			for_sentences.add(p);
		}
		System.out.println(for_sentences.size());
		for(ArrayList<String> sent: eng.train_sentences)
		{
			ArrayList<String> p=new ArrayList<>();
			p.add("NULL");
			for(String word: sent)
			{
				p.add(new String(word));
			}
			eng_sentences.add(p);
		}
		System.out.println(eng_sentences.size());
	}
	public void initialize()
	{
		eng_words=Set_of_words(eng_sentences);
		for_words=Set_of_words(for_sentences);
		//System.out.println(eng_words);
		//System.out.println(for_words);
		get_word_possibilities();
		initialize_uniform_probability();
		count_ef=new Double[lf][le];
		total_f=new Double[lf];
		total_s=new Double[le];
	}
	private void set_counts_zero()
	{
		for(int i=0;i<lf;i++)
		{
			ArrayList<Double> p=new ArrayList<>();
			for(int j=0;j<le;j++)
				count_ef[i][j]=0.0;
			total_f[i]=0.0;
		}
	}
	private void initialize_uniform_probability() {
		// TODO Auto-generated method stub
		lf=for_words.size();
		le=eng_words.size();
		t=new Double[lf][le];
		for(int i=0;i<lf;i++)
			for(int j=0;j<le;j++)
				t[i][j]=0.0;
		for(int i=0;i<lf;i++)
		{
			Double prob=0.0;
			int poss=(poss_index.get(i).size());
			
			ArrayList<Double> p=new ArrayList<>();
			if(poss>0)
				prob=1.0/poss;
			for(int j:poss_index.get(i))
				t[i][j]=prob;
			
		}
		
	}
	public void get_word_possibilities() {
		//possibilities=new ArrayList<ArrayList<String>>();
		poss_index=new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<for_words.size();i++)
		{
			//possibilities.add(new ArrayList<String>());
			poss_index.add(new ArrayList<Integer>());
		}
		int i=0;
		for(ArrayList<String> sent: for_sentences)
		{
			ArrayList<String> poss = eng_sentences.get(i);
			//System.out.println(poss);
			ArrayList<Integer> indices=new ArrayList<>();
			for(String word:poss)
			{
				int j=eng_words.indexOf(word);
				indices.add(j);
					
			}
			
			for(String f_word:sent)
			{
				int index=for_words.indexOf(f_word);
				ArrayList<Integer> v = poss_index.get(index);
				for(int j: indices)
				{
					if(!v.contains(j))
					{
						v.add(j);
					}
				}
				poss_index.set(index, v);
			}
			i++;
		}
		//System.out.println(possibilities);
		//System.out.println(poss_index);
	}
	public void EM_algorithm()
	{
		boolean converged = false;
		int cvgd = 0;
		int no_sentences=eng_sentences.size();
		int e=0;
		
		while (!converged)
		{
			set_counts_zero();
			total_s=new Double[le];
			for(int i=0;i<le;i++)
				total_s[i]=0.0;
			for(int i=0;i<no_sentences;i++)
			{
				
				for(String word: eng_sentences.get(i))
				{
					e=eng_words.indexOf(word);
					
					for(String f_word: for_sentences.get(i))
					{
						int f=for_words.indexOf(f_word);
						
						if(!poss_index.get(f).contains(e))
							continue;
						Double v = total_s[e];
						
						total_s[e]+=t[f][e];
						
					}
					for(String f_word: for_sentences.get(i))
					{
						int f=for_words.indexOf(f_word);
						
						if(!poss_index.get(f).contains(e))
							continue;
						count_ef[f][e]+= t[f][e] / total_s[e];
						total_f[f] += t[f][e] / total_s[e];
					}
				}
			}
			for(int i=0;i<lf;i++)
			{
				for(int j=0;j<le;j++)
				{
					//t_old[i][j]=t[i][j];
					t[i][j] = count_ef[i][j] / total_f[i];
					
				}
			}
			/*int change=0;
			for(int i=0;i<lf;i++)
			{
				for(int j=0;j<le;j++)
				{
					if((t_old[i][j]-t[i][j])!=0.0)
					{
						change=1;
						break;
					}
				}
			}
			if(change==0)
			{
				System.out.println("Converged at "+cvgd);
				converged=true;
			}*/
			if(cvgd==CONVERGE)
			{
				converged=true;
				System.out.println("Converge stopped");
			}

			cvgd++;
		}
	}
	private ArrayList<String> Set_of_words(
			ArrayList<ArrayList<String>> set_sentences) {
		// TODO Auto-generated method stub
		ArrayList<String> set_words = new ArrayList<String>();
		for(ArrayList<String> p:set_sentences)
		{
			for(String word: p)
			{
				if(!set_words.contains(word))
				{
					set_words.add(new String(word));
					
				}
			}
		}
		return set_words;
	}
	public void print_model()
	{
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File("model.txt")));
			for(int i=0;i<lf;i++)
			{
				String f=for_words.get(i);
				System.out.println("Foreign word is: "+f);
				bw.write("Foreign word is: "+f+"\n");
				int j=0;
				for(int e_i: poss_index.get(i))
				{
					String e=eng_words.get(e_i);
					Double prob=t[i][poss_index.get(i).get(j)];
					System.out.println("P("+e+"|"+f+") = "+prob);
					bw.write("P("+e+"|"+f+") = "+prob+"\n");
					j++;
				}
				
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	public void predict(List<ArrayList<String>> test_sentences)
	{
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File("output_test.txt")));
			for(ArrayList<String> sent: test_sentences)
			{
				String op="";
				for(String word: sent)
				{
					if(for_words.contains(word))
					{
						int index=for_words.indexOf(word);
						Double max_prob=0.0;
						int max=-1;
						for(int j=0;j<le;j++ )
						{
							if(t[index][j]>max_prob)
							{
								max_prob=t[index][j];
								max=j;
							}
						}
						op+=eng_words.get(max)+" ";
					}
					else
						op+=" -- ";
				}
				System.out.println(op);
				bw.write(op+"\n");
					
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void print_ref(List<ArrayList<String>> test_sentences)
	{
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File("ref_output_test.txt")));
			for(ArrayList<String> sent: test_sentences)
			{
				String op="";
				for(String word: sent)
				{
					op+=word;
					op+=" ";
				}
				System.out.println(op);
				bw.write(op+"\n");
					
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void split_data(Corpus c1,Corpus c2)
	{
		int test_no=c1.train_sentences.size()*(100-c1.train_to_test)/100;
		
		c1.test_sentences=new ArrayList<ArrayList<String>>();
		c2.test_sentences=new ArrayList<ArrayList<String>>();
		
		for(int j=1;j<=test_no;j++)
		{
			int random = (int )(Math.random() *c1.train_sentences.size() );
			c1.test_sentences.add(c1.train_sentences.remove(random));
			c2.test_sentences.add(c2.train_sentences.remove(random));
		}
		
	}
	public static void main(String[] args)
	{
		Corpus french=new Corpus();
		System.out.println("Started foerigm corpus");
		//french.setFname("/home/user/Documents/Course_Books/NLP/Project/fr-en/europarl-v7.fr-en.fr");
		french.setFname("/home/user/Documents/Course_Books/NLP/Project/small2");
		french.read_data();
		french.setTrain_to_test(80);
		//french.split_data();
		
		System.out.println("Started english corpus");
		Corpus eng=new Corpus();
		//eng.setFname("/home/user/Documents/Course_Books/NLP/Project/fr-en/europarl-v7.fr-en.en");
		eng.setFname("/home/user/Documents/Course_Books/NLP/Project/small");
		eng.read_data();
		eng.setTrain_to_test(80);
		//eng.split_data();
		split_data(french,eng);
		System.out.println("Started model creation");
		Model m=new Model(eng, french);
		System.out.println("Started model initialization");
		m.initialize();
		System.out.println("Started em algorithm");
		m.EM_algorithm();
		System.out.println("Started printing");
		m.print_model();
		m.predict(french.test_sentences);
		//m.predict("/home/user/Documents/Course_Books/NLP/Project/test");
		m.print_ref(eng.test_sentences);
	}
	private void predict1(String fname)  {
		// TODO Auto-generated method stub
		try {
		BufferedReader br=new BufferedReader(new FileReader(new File(fname)));
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("op.txt")));
		String line;
		
			while((line = br.readLine())!=null){
			String op="";
			for(String word: line.split("\\s+"))
			{
				if(for_words.contains(word))
				{
					int index=for_words.indexOf(word);
					Double max_prob=0.0;
					int max=-1;
					for(int j=0;j<le;j++ )
					{
						if(t[index][j]>max_prob)
						{
							max_prob=t[index][j];
							max=j;
						}
					}
					op+=eng_words.get(max)+" ";
				}
				else
					op+=" -- ";
			}
			bw.write(op+"\n");
			System.out.println(op);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
