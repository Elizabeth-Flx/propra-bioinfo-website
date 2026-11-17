package AlignmentAlgorithms;

import java.io.File;
import java.util.*;

public class GeneticAlgorithm {


    ArrayList<Entity> generation;
    ArrayList<String[]> trainingAlignments = new ArrayList<>();


    private class Entity implements Comparable<Entity> {

        public SubstitutionMatrix sm;
        public int go;
        public int ge;

        public double score;
        public Entity(SubstitutionMatrix subMatrix, int go, int ge) {
            sm = subMatrix;
            this.go = go;
            this.ge = ge;
        }

        public void breed(Entity parent, ArrayList<Entity> nextGen, double maxChange, double changeChance) {
            SplittableRandom rand = new SplittableRandom();

            SubstitutionMatrix newSub;
            int newGo, newGe;

            for (int i = 0; i < 10; i++) {
                newSub = SubstitutionMatrix.breedMatrixes(this.sm, parent.sm, maxChange, changeChance);
                //newGo = (int)((this.go + parent.go)/2.0 + .5);
                //newGe = (int)((this.ge + parent.ge)/2.0 + .5);
                //if (rand.nextDouble() < changeChance) newGo += (int) (maxChange/100 * rand.nextDouble(-1, 1) + 0.5);
                //if (rand.nextDouble() < changeChance) newGe += (int) (maxChange/100 * rand.nextDouble(-1, 1) + 0.5);

                //if (newGo >= 0) newGo = -1;
                //if (newGe >= 0) newGe = -1;

                nextGen.add(new Entity(newSub, parent.go, parent.ge));
            }
        }
        @Override
        public int compareTo(Entity o) {
            //if (this.score == o.score) return 0;
            Double thisScore = this.score;
            Double oScore = o.score;
            return thisScore.compareTo(oScore);
        }

        public void printEntity() {
            System.out.println("ENTITY");
            System.out.println(Arrays.deepToString(sm.getSubMatrix()));
            System.out.println("Gap open: " + go + "     " + "Gap extend: " + ge);
        }
    }
    public void initEntities(String baseMatrixPath, double maxChange, double changeChance, int go, int ge) {

        SubstitutionMatrix baseMatrix = new SubstitutionMatrix(baseMatrixPath);
        generation = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            generation.add(new Entity(baseMatrix.getMutation(maxChange, changeChance), go, ge));
        }
    }

    public ArrayList<Entity> breedBest(double maxChange, double changeChance) {
        ArrayList<Entity> nextGen = new ArrayList<>();
        Collections.sort(generation);
        System.out.println("Best generation score: " + generation.get(0).score);
        for (int i = 0; i < 20; i += 2) {
            //System.out.println(generation.get(i).score);
            //System.out.println(generation.get(i+1).score);
            generation.get(i).breed(generation.get(i+1), nextGen, maxChange, changeChance);
        }
        return nextGen;
    }

    public void loadTrainingData(String seqlibPath, String trainingPath) {
        try {
            SeqIdToSeq sequences = new SeqIdToSeq(new File(seqlibPath));
            Scanner myReader = new Scanner(new File(trainingPath));
            while (myReader.hasNextLine()) {

                String data = myReader.nextLine();
                String[] ids = data.split(" ");

                String seq1Train = sequences.getSeq(ids[0]);
                String seq2Train = sequences.getSeq(ids[1]);

                trainingAlignments.add(new String[] {seq1Train, seq2Train});
            }
        } catch (Exception e) {
            System.out.println("PROblem");

        }
    }

    public void calcEntityScore(Entity e) {

        double sumMSE = 0;

        for (String[] alignment : trainingAlignments) {
            Gotoh g = new Gotoh(alignment[0].replace("-",""),
                                alignment[1].replace("-",""),
                                "", "", e.sm, e.go, e.ge);
            Alignment candidate = g.alignFreeShift();
            Alignment reference = new Alignment(alignment[0], alignment[1], "", "", 0);
            sumMSE += Metrics.calcMSE(reference, candidate);

        }
        sumMSE /= trainingAlignments.size();
        e.score = sumMSE;
    }

    public void runGeneration(double maxChange, double changeChance) {

        for (int i = 0; i < generation.size(); i++) {
            if (i % 20 == 0) {
                System.out.println(i + "%");
            }
            calcEntityScore(generation.get(i));
        }
        System.out.println("Start Breeding");
        ArrayList<Entity> nextGen = breedBest(maxChange, changeChance);
        System.out.println("Breeding Done");
        generation = nextGen;
    }


    public static void main(String[] args) {
        double maxChange    = 80;
        double changeChance = 0.1;

        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.initEntities("F:\\other_files\\MATRICES\\dayhoff.mat", maxChange, changeChance, -12, -1);

        ga.loadTrainingData("F:\\other_files\\sequencelib.txt", "F:\\other_files\\trainingPairs.txt");

        System.out.println(ga.trainingAlignments.size());

        //ga.trainingAlignments = new ArrayList<String[]>( ga.trainingAlignments.subList(0, 100));

        int generationCount = 0;

        while (true) {
            System.out.println("Generation " + generationCount);
            ga.generation.get(0).printEntity(); // Print best entity of generation
            System.out.println("\nStarting training...");
            ga.runGeneration(maxChange, changeChance);
            System.out.println("Training complete!");
            generationCount++;
        }
    }











}