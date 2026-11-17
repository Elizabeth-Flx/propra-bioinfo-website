package AlignmentAlgorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class SeqIdToSeq {

    public HashMap<String, String> idKeySeqVal= new HashMap<>();

    public SeqIdToSeq(File idAndSeq){
        try {
            Scanner myReader = new Scanner(idAndSeq);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();

                String[] lineElements = line.split(":");
                String seqID = lineElements[0];
                String sequence = lineElements[1];

                idKeySeqVal.put(seqID, sequence);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();}
    }

    public String getSeq(String id){
        return idKeySeqVal.get(id);
    }



}