import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DocumentDistanceCalc {
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: java DocumentDistanceCalc filename_1 filename_2");
        }

        Path path1 = Paths.get(args[0]);
        Path path2 = Paths.get(args[1]);

        DocumentDistanceCalc documentDistanceCalc = new DocumentDistanceCalc();

        Map<String, Integer> wordMap1 = documentDistanceCalc.calculateWordFreqForFile(path1);
        Map<String, Integer> wordMap2 = documentDistanceCalc.calculateWordFreqForFile(path2);

        Double distance = documentDistanceCalc.vectorAngle(wordMap1, wordMap2);
        System.out.printf("The distance between the documents is: %.6f (radians)", distance);
    }

    private Double vectorAngle(Map<String, Integer> map1, Map<String, Integer> map2) {
        Double numerator = inner_product(map1, map2);
        Double denominator = Math.sqrt(inner_product(map1, map1) * inner_product(map2, map2));
        return Math.acos(numerator/denominator);
    }

    private Double inner_product(Map<String, Integer> map1, Map<String, Integer> map2) {
        Double sum = 0.0;

        for (String key : map1.keySet()) {
            if (map2.containsKey(key)) {
                sum += map1.get(key) * map2.get(key);
            }
        }

        return sum;
    }

    private Map<String, Integer> calculateWordFreqForFile(Path path) {
        List<String> lines = readFile(path);
        List<String> words = getWordsFromLineList(lines);
        Map<String, Integer> freqMapping = countFrequency(words);

        System.out.print("File" + path.toString() + ":");
        System.out.print(lines.size() +" lines,");
        System.out.print(words.size() + " words,");
        System.out.println(freqMapping.size() + " distinct words");

        return freqMapping;
    }

    // ##################################
    // # Operation 1: read a text file ##
    // ##################################
    private List<String> readFile(Path path) {
        try {
            return Files.readAllLines(path, Charset.defaultCharset());
        } catch (IOException e) {
            System.err.println("Error opening or reading input file:" + path.toString());
            e.printStackTrace();
        }

        return null;
    }

    // #################################################
    // # Operation 2: split the text lines into words ##
    // #################################################
    private List<String> getWordsFromLineList(List<String> lines) {
        List<String> wordList = new ArrayList<>();

        for (String line : lines) {
            List<String> words_in_line = getWordsFromString(line);
            wordList.addAll(words_in_line);
        }

        return wordList;
    }

    private List<String> getWordsFromString(String line) {
        List<String> words = new ArrayList<>();

        for (String word : line.split("[^a-zA-Z0-9]+")) {
            words.add(word.toLowerCase());
        }

        return words;
    }

    // ##############################################
    // # Operation 3: count frequency of each word ##
    // ##############################################
    private Map<String, Integer> countFrequency(List<String> words) {
        Map<String, Integer> freq = new HashMap<>();

        for (String word : words) {
            if (freq.containsKey(word)) {
                freq.put(word, freq.get(word) + 1);
            } else {
                freq.put(word, 1);
            }
        }

        return freq;
    }
}
