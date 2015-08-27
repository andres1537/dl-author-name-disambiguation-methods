
package com.cgomez.hhc.classifier.hhc;

import com.cgomez.hhc.classifier.Citation;
import com.cgomez.hhc.hhc.Similarity;
import com.cgomez.hhc.hhc.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import com.cgomez.hhc.po.Artigo;
import com.cgomez.hhc.po.Cluster;

public class HHC {
    
    public static String methodName = "HHC";
    
    public double simTitle = 0;
    public double simVenue = 0;
    double limComp = 0.3;
    private ArrayList<Artigo> artigos;
    
    public HHC(double simTitle, double simVenue){
        this.simTitle = simTitle;
        this.simVenue = simVenue;
        artigos = new ArrayList<>();
    }
    
    public void initializeModel(){
        artigos = new ArrayList<>();
    }
    
    public LinkedList<Citation> test(String dataset) throws FileNotFoundException, IOException {
        LinkedList<Citation> test = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
            String row = br.readLine();
            while (row != null){
                Artigo instance = getTestInstance(row);
                artigos.add(instance);
                row = br.readLine();
            }
        }
        
        com.cgomez.hhc.po.Cluster.setNumberOfClusters(0);
        ArrayList <Artigo>lstNomeLongo = new ArrayList<>(); 
        ArrayList <Artigo>lstNomeCurto = new ArrayList<>();

        // seleciona os artigos aleatoriamente para definir a ordem do agrupamento
        Random random = new Random(1551);
 
        ArrayList<Artigo> lstArtigos = new ArrayList<>(artigos);

        while (lstArtigos.size() > 0) {
            int v = random.nextInt(lstArtigos.size());
            Artigo a = lstArtigos.get(v);
            lstArtigos.remove(v);

            String [] nome = a.getAutor().split("[ ]");
            if (nome.length == 2 && nome[0].length()==1){
                lstNomeCurto.add(a);
            } else {
                lstNomeLongo.add(a);
            }
        }

        ArrayList<Cluster> clusters = new ArrayList<>();
        criaClustersIniciais(clusters, lstNomeLongo);
        criaClustersIniciais(clusters, lstNomeCurto);

        fusaoGrupos(clusters);
        
        for (Artigo a: artigos){
            test.add(new Citation(a.getNumArtigo(), a.getNumClasse(), a.getNumClasseRecebida()));
        }
        
        return test;
    }
    
    private void criaClustersIniciais(ArrayList<Cluster> clusters, ArrayList<Artigo> lstNome) {
		try {
            for (Artigo a: lstNome){
                boolean achou = false;
                int pos = 0;
                
                while (pos < clusters.size() && ! achou) {
                    if (a.getCoautores() != null && clusters.get(pos).getCoauthors().size() > 0 && a.getCoautores().length > 0) {
                        if (Similarity.ComparacaoFragmentosTodosAutores(clusters.get(pos), a, limComp)
                            && Similarity.ComparacaoFragmentosCoautorGrupo(clusters.get(pos), a, limComp, 1)) {

                            clusters.get(pos).add(a);
                            achou = true;
                        }
                    }
                    pos++;
                }

                if (! achou) {
                    Cluster c = new Cluster();
                    c.add(a);
                    clusters.add(c);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}
    
    private Artigo getTestInstance(String row){
        if (row.contains("CLASS=")){
            return null;
        } else {
            return getTestInstanceNCFormat(row);
        }
    }
    
    private Artigo getTestInstanceNCFormat(String row){
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        //System.out.println(row);
        String[] pieces = row.split(";");
        
        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);
        
        String[] authorNames = pieces[2].split(" ");
        String shortAuthorName;
        if (authorNames.length == 1){
            shortAuthorName = pieces[2];
        } else {
            shortAuthorName = authorNames[0].charAt(0) + authorNames[authorNames.length - 1];
        }
        
        LinkedList<String> listCoauthors = new LinkedList<>();
        String[] aux = pieces[3].split(",");
        for (String coauthor: aux){
            if (coauthor.length() < 1){
                continue;
            }
            
            String[] coauthorNames = coauthor.split(" ");
            String shortCoAuthorName;
            if (authorNames.length == 1){
                shortCoAuthorName = coauthor;
            } else {
                shortCoAuthorName = coauthorNames[0].charAt(0) + coauthorNames[coauthorNames.length - 1];
            }
            
            if (shortCoAuthorName.equals(shortAuthorName)){
                //continue;
            }
            
            listCoauthors.add(coauthor);
        }
        String[] coauthors = new String[listCoauthors.size()];
        coauthors = listCoauthors.toArray(coauthors);
        
        Artigo artigo = new Artigo(citationId, classId, citationId, 
					pieces[2], coauthors, pieces[4], pieces[5]);
        
        //System.out.println(row);
        //System.out.println(artigo);
        
        return artigo;
    }
    
    private void fusaoGrupos(ArrayList<Cluster> clusters) {
        boolean v_teve_grupo_unido = true;
        
        try {
            while (v_teve_grupo_unido){
                v_teve_grupo_unido = false;

                for (int c_grupo = 0; c_grupo < clusters.size(); ++c_grupo) {
                    Cluster v_g1 = (Cluster) clusters.get(c_grupo);

                    for (int c_grupo2 = c_grupo + 1; c_grupo2 < clusters.size(); ++c_grupo2) {
                        Cluster v_g2 = (Cluster) clusters.get(c_grupo2);

                        if (Similarity.ComparacaoFragmentosTodosAutores(v_g1, v_g2, limComp)) {
                            if ( 
                                (Similarity.cosineDistance(Util.toArray(v_g1.getTitle()), Util.toArray(v_g2.getTitle()))) > simTitle					
                                ||
                                (Similarity.cosineDistance(Util.toArray(v_g1.getVenue()), Util.toArray(v_g2.getVenue()))) > simVenue
                                ) {

                                for (int c_entradaGrupo = 0; c_entradaGrupo < v_g2.getArticles().size(); c_entradaGrupo++) {
                                    v_g1.add(v_g2.getArticles().get(c_entradaGrupo));
                                }

                                v_teve_grupo_unido = true;
                                clusters.remove(c_grupo2);
                                c_grupo2--;
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
