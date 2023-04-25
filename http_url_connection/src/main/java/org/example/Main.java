package org.example;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String Args[]) {



        try {
            String apiString = "";
            httpGetter getter = new httpGetter(new URL("https://content.guardianapis.com/search?api-key=c9d442dd-66ec-43a8-aa3d-26047fa8780e"));
            apiString = getter.getHttpString();
            jsonParser parser = new jsonParser(apiString);
            article[] articles = parser.getArticles();
            htmlDownloader downloader = new htmlDownloader();
            String[] fileNames = new String[articles.length];
            int counter = 0;
            String pageContent = "";
            for (article  article : articles) {
                fileNames[counter] = downloader.download(article.getWebUrl().toString());
                Scanner reader = new Scanner(new File(fileNames[counter]));
                while(reader.hasNextLine()){
                    pageContent += reader.nextLine();
                }
                Tokenizer tokenizer = new Tokenizer(pageContent);
                //tokenizer.printTokens();
                System.out.print("The more frequent key found in the "+counter+" article is: ");
                tokenizer.printFirst();
                counter++;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(JSONException e){
            e.printStackTrace();
        }


    }
}
