import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ExtractCorpus {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//zip();
		shorten_files("/home/user/Documents/Course_Books/NLP/Project/eng.txt",
				"/home/user/Documents/Course_Books/NLP/Project/eng_half.txt");
	}

	public static void shorten_files(String filename,String op_filename)
	{
		
	}
	public static void zip() throws FileNotFoundException, IOException {
		BufferedReader br=new BufferedReader(new FileReader(new File("/home/user/Documents/Course_Books/NLP/Project/HW4/HW4/eng-ger.txt")));
		BufferedWriter bw1=new BufferedWriter(new FileWriter(new File("/home/user/Documents/Course_Books/NLP/Project/eng.txt")));
		BufferedWriter bw2=new BufferedWriter(new FileWriter(new File("/home/user/Documents/Course_Books/NLP/Project/ger.txt")));
		String line;
		int cnt=0;
		while((line=br.readLine())!=null)
		{
			if((cnt%3)==0)
			{
				bw1.write(line+"\n");
			}
			else if((cnt%3)==1)
			{
				bw2.write(line+"\n");
			}
			cnt++;
		}
		bw1.close();
		bw2.close();
	}

}
