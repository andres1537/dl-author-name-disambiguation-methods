package com.cgomez.nb.classifier.nb;

import com.cgomez.nb.classifier.Evaluate;
import com.cgomez.nb.classifier.Classifier;
import com.cgomez.nb.classifier.Instance;
import com.cgomez.nb.classifier.Citation;
import com.cgomez.nb.classifier.Group;
import com.cgomez.nb.classifier.Term;
import com.cgomez.nb.classifier.Type;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import com.cgomez.nb.tools.Count;

/**
 *
 * @author Alan Filipe
 */
public class NBDisambiguator extends Classifier {
    
    private HashMap<Integer, Group> groups;
    private HashMap<Group, Double> groupPr;
    private HashMap<String, Term> terms;
    private HashMap<String, Term> coauthors;
    private LinkedList<Group> listGroups;
    private LinkedList<Citation> citations;
    private NaiveBayes<Group> nbTitle, nbVenue;
    private double prNoCA = 0;
    private double prCA = 0;
    
    private final HashMap<String, Type> types;
    
    public NBDisambiguator(){
        types = new HashMap<>(6);
        types.put("c", new Type("coauthor", 0));
        types.put("t", new Type("title", 0));
        types.put("v", new Type("venue", 0));
    }
    
    @Override
    public void train(String dataset) throws FileNotFoundException, IOException {
        terms = new HashMap<>();
        coauthors = new HashMap<>();
        groups = new HashMap<>();
        groupPr = new HashMap<>();
        listGroups = new LinkedList<>();
        citations = new LinkedList<>();
        nbTitle = new NaiveBayes<>();
        nbVenue = new NaiveBayes<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
            String row = br.readLine();
            while (row != null){
                addTrainInstance(row);
                row = br.readLine();
            }
        }
        
        for (Group g: listGroups){
            for (Citation c: g.citations){
                nbTitle.addDoc(g, c.title);
            }
        }
        
        for (Group g: listGroups){
            for (Citation c: g.citations){
                nbVenue.addDoc(g, c.publicationVenue);
            }
        }
        
        for (Group g: listGroups){
            groupPr.put(g, getProbabilityHasCoauthorSeen(g));
        }
    }
    
    private void addTrainInstance(String row){
        if (row.contains("CLASS=")){
            addTrainInstanceLANDFormat(row);
        } else {
            addTrainInstanceNCFormat(row);
        }
    }
    
    private void addTrainInstanceNCFormat(String row){
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        HashMap<String, Boolean> aux = new HashMap<>();
        String[] pieces = row.split(";");
        
        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);
        
        Group group = groups.get(classId);
        if (group == null){
            group = new Group(classId, true);
            groups.put(classId, group);
            listGroups.add(group);
        }
        
        Citation citation = new Citation(citationId, classId);
        group.citations.add(citation);
        citations.add(citation);
        
        String termId;
        /* Author
        if (pieces[2].length() > 0){
            String[] name = pieces[2].split("[ ]");
            if (name.length > 2 || name[0].length() > 1){
                String withOutLastName = "";
                for (int i=0; i<name.length-1;i++){ 
                    if (withOutLastName.length() == 0){
                        withOutLastName = name[i];
                    } else {
                        withOutLastName = withOutLastName +"_"+ name[i];
                    }
                }
                
                termId = "a="+ withOutLastName;
                if (! aux.containsKey(termId)){
                    Term term = terms.get(termId);
                    if (term == null){
                        term = new Term(termId, 0);
                        terms.put(termId, term);
                    }
                    term.freq++;

                    Count f = term.groupFreq.get(group);
                    if (f == null){
                        f = new Count();
                        term.groupFreq.put(group, f);
                        //group.coauthors.add(term);
                    }
                    f.count++;

                    citation.author = term;
                    //citation.coauthors.add(term);
                    //if (! coauthors.containsKey(termId)){
                    //    coauthors.put(termId, term);
                    //}
                }
            }
        }
        */
        
        String[] citCoauthors = pieces[3].split(",");
        for (String coauthor: citCoauthors){
            termId = "c="+ coauthor.replaceAll("[ ]", "_");
            if (aux.containsKey(termId) || coauthor.length() < 1){
                continue;
            }
            aux.put(termId, true);
            
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(termId, 0);
                terms.put(termId, term);
            }
            term.freq++;

            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.coauthors.add(term);
            }
            f.count++;

            citation.coauthors.add(term);
            if (! coauthors.containsKey(termId)){
                coauthors.put(termId, term);
            }
        }
        
        String[] titleTerms = pieces[4].split(" ");
        for (String t: titleTerms){
            termId = "t="+ t;
            if (aux.containsKey(termId) || t.length() < 1){
                continue;
            }
            aux.put(termId, true);
            
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(termId, 0);
                terms.put(termId, term);
            }
            term.freq++;

            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.title.add(term);
            }
            f.count++;

            citation.title.add(term);
        }

        String[] venueTerms = pieces[5].split(" ");
        for (String t: venueTerms){
            termId = "v="+ t;
            if (aux.containsKey(termId) || t.length() < 1){
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null){
                term = new Term(termId, 0);
                terms.put(termId, term);
            }
            term.freq++;

            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.publicationVenue.add(term);
            }
            f.count++;

            citation.publicationVenue.add(term);
        }
        
        //System.out.println(citation);
    }
    
    private void addTrainInstanceLANDFormat(String row){
        String[] pieces = row.split(" ");
        Integer classId = Integer.parseInt(pieces[1].replace("CLASS=", ""));
        Group group = groups.get(classId);
        if (group == null){
            group = new Group(classId, true);
            groups.put(classId, group);
            listGroups.add(group);
        }
        
        Citation instance = new Citation(
                Integer.parseInt(pieces[0]), 
                classId);
        group.citations.add(instance);
        citations.add(instance);
        
        HashMap<String, Byte> aux = new HashMap<>((int) (pieces.length / 0.75));
        for (int i=2; i<pieces.length; i++){
            if (pieces[i].length() <= 2){
                continue;
            }
            String termId = pieces[i];
            Byte find = aux.get(termId);
            if (find != null){
                continue;
            }
            aux.put(termId, new Byte((byte) 1));
            
            String att = termId.substring(0, 1);
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(termId, 0, types.get(att));
                terms.put(termId, term);
            }
            term.freq++;
            
            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                switch (att) {
                    case "c":
                        group.coauthors.add(term);
                        break;
                    case "t":
                        group.title.add(term);
                        break;
                    case "v":
                        group.publicationVenue.add(term);
                        break;
                }
            }
            f.count++;
            
            switch (att) {
                case "c":
                    instance.coauthors.add(term);
                    if (! coauthors.containsKey(termId)){
                        coauthors.put(termId, term);
                    }
                    break;
                case "t":
                    instance.title.add(term);
                    break;
                case "v":
                    instance.publicationVenue.add(term);
                    break;
            }
        }
        
        //System.out.println(row);
        //System.out.println(instance);
    }
    
    private Citation getTestInstance(String row){
        if (row.contains("CLASS=")){
            return getTestInstanceLANDFormat(row);
        } else {
            return getTestInstanceNCFormat(row);
        }
    }
    
    private Citation getTestInstanceNCFormat(String row){
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        HashMap<String, Boolean> aux = new HashMap<>();
        String[] pieces = row.split(";");
        
        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);
        
        Citation citation = new Citation(citationId, classId);
        
        String termId;
        /* Author
        if (pieces[2].length() > 0){
            String[] name = pieces[2].split("[ ]");
            if (name.length > 2 || name[0].length() > 1){
                String withOutLastName = "";
                for (int i=0; i<name.length-1;i++){ 
                    if (withOutLastName.length() == 0){
                        withOutLastName = name[i];
                    } else {
                        withOutLastName = withOutLastName +"_"+ name[i];
                    }
                }
                
                termId = "c="+ withOutLastName;
                if (! aux.containsKey(termId)){
                    Term term = terms.get(termId);
                    if (term != null){
                        citation.coauthors.add(term);
                        citation.author = term;
                    }
                }
            }
        }
        */
        
        String[] citCoauthors = pieces[3].split(",");
        for (String coauthor: citCoauthors){
            termId = "c="+ coauthor.replaceAll("[ ]", "_");
            if (aux.containsKey(termId) || coauthor.length() < 1){
                continue;
            }
            aux.put(termId, true);
            
            Term term = terms.get(termId);
            if (term == null){
                continue;
            }

            citation.coauthors.add(term);
        }
        
        String[] titleTerms = pieces[4].split(" ");
        for (String t: titleTerms){
            termId = "t="+ t;
            if (aux.containsKey(termId) || t.length() < 1){
                continue;
            }
            aux.put(termId, true);
            
            Term term = terms.get(termId);
            if (term == null){
                continue;
            }
            citation.title.add(term);
        }

        String[] venueTerms = pieces[5].split(" ");
        for (String t: venueTerms){
            termId = "v="+ t;
            if (aux.containsKey(termId) || t.length() < 1){
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null){
                continue;
            }

            citation.publicationVenue.add(term);
        }
        
        //System.out.println(citation);
        return citation;
    }
    
    private Citation getTestInstanceLANDFormat(String row){
        String[] pieces = row.split(" ");
        Integer classId = Integer.parseInt(pieces[1].replace("CLASS=", ""));

        Citation instance = new Citation(
                Integer.parseInt(pieces[0]), 
                classId);
        
        HashMap<String, Byte> aux = new HashMap<>((int) (pieces.length / 0.75));
        
        for (int i=2; i<pieces.length; i++){
            if (pieces[i].length() <= 2){
                continue;
            }
            String termId = pieces[i];
            Byte find = aux.get(termId);
            if (find != null){
                continue;
            }
            aux.put(termId, (byte) 1);
            
            String att = termId.substring(0, 1);
            Term term = terms.get(termId);
            if (term == null){
                continue;
            }
            
            switch (att) {
                case "c":
                    instance.coauthors.add(term);
                    break;
                case "t":
                    instance.title.add(term);
                    break;
                case "v":
                    instance.publicationVenue.add(term);
                    break;
            }
        }
        return instance;
    }
    
    @Override
    public Evaluate test(String dataset) throws FileNotFoundException, IOException {
        LinkedList<Instance> list = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
            String row = br.readLine();
            
            while (row != null){
                Citation c = getTestInstance(row);
                list.add(c);
                
                ArrayList<Result<Group>> results = new ArrayList<>();
                results.add(nbTitle.classify(c.title));
                results.add(nbVenue.classify(c.publicationVenue));
                results.add(classifyUsingCoauthors(c));
                
                Result<Group> finalResult = results.get(0).combine(results);
                c.predictedClassId = finalResult.getBestFit().getCClass().id;
                //System.out.println(c.id +"\t"+ c.predictedClassId);
                row = br.readLine();
            }
        }
        
        Evaluate eval = new Evaluate(list);
        return eval;
    }

    private double getProbabilityNoCoauthor(Group g){
        double numCitations = g.citations.size();
        if (numCitations < 1){
            return 0;
        }
        
        double n = 1.0;
        for (Citation c: g.citations){
            int numAuthors = c.coauthors.size();
            //for (Term t: c.coauthors){
                //if (t.term.substring(0, 1).equals("a")){
                //    numAuthors--;
                //    break;
                //}
            //}
            
            if (numAuthors == 0){
                n++;
            }
        }
        
        return n / numCitations;
    }
    
    private double getProbabilityHasCoauthor(Group g){
        if (g.citations.size() == 0){
            return 0;
        }
        
        return 1 - prNoCA;
    }
    
    public double getProbabilityHasCoauthorSeen(Group g){		
        double n = 0;
        double nSeen = 0;
        
        for (Term t: g.coauthors){
            //System.out.println(t.term);
            //if (t.term.substring(0, 1).equals("a")){
            //    continue;
            //}
            
            int count = t.groupFreq.get(g).count;
            n += count;
            if (count > 1){
                nSeen = nSeen + count;
            }
        }
        
        if (n == 0){
            return 0;
        }

        return nSeen / n;
    }
    
    public double getProbabilityHasCoauthorSeen(Group g, Term t){
		double total = 0;
		double n = 0;
		for (Citation c: g.citations){
			for (Term author: c.coauthors){
                //if (t.term.substring(0, 1).equals("a")){
                //    continue;
                //}
                
				total++;
				if (author.equals(t)){
					n++;
				}
			}
		}

		if (total == 0){
			return 0;
		}
		
		return n / total;
	}
    
    public double getProbabilityHasCoauthorUnSeen(Group g, Term t, double numCoauthors){
		if (numCoauthors == 0){
			return 0;
		}
        
        double num = 0;
		for (Citation c: g.citations){
			num += c.coauthors.size();
		}
		
		return 1.0 / Math.pow(numCoauthors - num, 2);
	}
    
    private Result<Group> classifyUsingCoauthors(Citation citation) {
        Result<Group> result = new Result<>();

        double numAuthors = citation.coauthors.size();
        String author = "";
        for (Term t: citation.coauthors){
            if (! t.term.substring(3, 4).equals("_")){
                author = t.term;
                numAuthors--;
                break;
            }
        }
        
        double total = 0;
        for (Group g : listGroups){
            total = total + g.citations.size();
        }

        for (Group g : listGroups){
            prNoCA = getProbabilityNoCoauthor(g);
            prCA = getProbabilityHasCoauthor(g);
            
            double sum = total / g.citations.size();
            if (numAuthors == 0){
                sum *= prNoCA;
            } else {
                for (Term t: citation.coauthors){
                    if (t.term.equals(author)){
                        continue;
                    }
                    
                    double r = (prCA * groupPr.get(g) * getProbabilityHasCoauthorSeen(g, t))
                               +
                               (prCA * (1 - groupPr.get(g)) * getProbabilityHasCoauthorUnSeen(g, t, coauthors.size()));
                    sum *= r;
                    
                    /*
					System.out.println("coauthor : " + t.term);
					System.out.println("r : " + r);
					System.out.println("sum : " + sum);
					System.out.println("probabilityHasCoauthor : " + prCA);
					System.out.println("probabilityHasCoauthorSeen : " + groupPr.get(g));
					System.out.println("probabilityHasCoauthorSeen : " + getProbabilityHasCoauthorSeen(g, t));
					System.out.println("probabilityHasCoauthorUnseen : " + (1 - groupPr.get(g)));
					System.out.println("probabilityHasCoauthorUnseen : " + getProbabilityHasCoauthorUnSeen(g, t, coauthors.size()))
                    */
					
                }
            }
            
            result.add(new ResultItem<>(g, sum));
        }

        return result;
    }
    
    public void searchParameters(String prefix, int numFolds, Random random){}
}