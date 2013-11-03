import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocumentDistanceCalc {
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: java DocumentDistanceCalc filename_1 filename_2");
        }

        Path path1 = Paths.get(args[0]);
        Path path2 = Paths.get(args[1]);

        DocumentDistanceCalc documentDistanceCalc = new DocumentDistanceCalc();

        List<Tuple> sortedWordList1 = documentDistanceCalc.calculateWordFreqForFile(path1);
        List<Tuple> sortedWordList2 = documentDistanceCalc.calculateWordFreqForFile(path2);

        Double distance = documentDistanceCalc.vectorAngle(sortedWordList1, sortedWordList2);
        System.out.printf("The distance between the documents is: %.6f (radians)", distance);
    }

    private Double vectorAngle(List<Tuple> list1, List<Tuple> list2) {
        Double numerator = inner_product(list1, list2);
        Double denominator = Math.sqrt(inner_product(list1, list1) * inner_product(list2, list2));
        return Math.acos(numerator/denominator);
    }

    private Double inner_product(List<Tuple> list1, List<Tuple> list2) {
        Double sum = 0.0;
        int i = 0;
        int j = 0;

        while (i < list1.size() && j < list2.size()) {
            // # L1[i:] and L2[j:] yet to be processed
            if (list1.get(i).word.equals(list2.get(j).word)) {
                //# both vectors have this word
                sum += list1.get(i).count * list2.get(j).count;
                i++;
                j++;
            } else if (list1.get(i).word.compareTo(list2.get(j).word) < 0) {
                // # word L1[i][0] is in L1 but not L2
                i++;
            } else {
                // # word L2[j][0] is in L2 but not L1
                j++;
            }
        }

        return sum;
    }

    private List<Tuple> calculateWordFreqForFile(Path path) {
        List<String> lines = readFile(path);
        List<String> words = getWordsFromLineList(lines);
        List<Tuple> freqMapping = countFrequency(words);
        insertionSort(freqMapping);

        System.out.println("File" + path.toString() + ":");
        System.out.println(lines.size() +" lines,");
        System.out.println(words.size() + " words,");
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

        for (String word : line.split(" ")) {
            words.add(word.toLowerCase());
        }

        return words;
    }

    // ##############################################
    // # Operation 3: count frequency of each word ##
    // ##############################################
    private List<Tuple> countFrequency(List<String> words) {
        List<Tuple> freq = new ArrayList<>();

        for (String word : words) {
            Tuple tuple = new Tuple(word);

            int index = freq.indexOf(tuple);

            if (index > -1) {
                freq.get(index).count++;
            } else {
                freq.add(tuple);
            }
        }

        return freq;
    }

    // ###################################################
    // # Operation 4: sort words into alphabetic order ###
    // ###################################################
    private void insertionSort(List<Tuple> words) {
        Collections.sort(words);
    }

    class Tuple implements Comparable<Tuple> {
        String word;
        int count;

        Tuple(String word) {
            this.word = word;
            this.count = 1;
        }

        @Override
        public int compareTo(Tuple o) {
            return word.compareTo(o.word);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Tuple tuple = (Tuple) o;

            return word.equals(tuple.word);

        }

        @Override
        public int hashCode() {
            return word.hashCode();
        }
    }
}
