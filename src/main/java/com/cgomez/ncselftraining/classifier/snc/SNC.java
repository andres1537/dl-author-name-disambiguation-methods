package com.cgomez.ncselftraining.classifier.snc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.cgomez.ncselftraining.classifier.Citation;
import com.cgomez.ncselftraining.classifier.Term;
import com.cgomez.ncselftraining.classifier.nc.NC;
import com.cgomez.ncselftraining.hhc.Similarity;
import com.cgomez.ncselftraining.po.Artigo;
import com.cgomez.ncselftraining.po.Cluster;
import com.cgomez.ncselftraining.po.TermOcurrence;

public class SNC {

    public static String methodName = "SNC";

    public double limComp = 0.3;
    public double phi = 0.05;

    private ArrayList<Artigo> artigos;
    private HashMap<Integer, com.cgomez.ncselftraining.classifier.Cluster> clusters;
    private HashMap<String, Term> terms;

    public SNC() {
        initializeModel();
    }

    public SNC(double phi) {
        initializeModel();
        this.phi = phi;
    }

    public final void initializeModel() {
        artigos = new ArrayList<>();
        clusters = new HashMap<>();
        terms = new HashMap<>();
    }

    public LinkedList<Citation> test(String dataset) throws FileNotFoundException, IOException, InterruptedException {
        LinkedList<Citation> result = new LinkedList<>();
        HashMap<Integer, Citation> citations = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
            String row = br.readLine();
            while (row != null) {
                Artigo instance = getTestInstance(row);
                Citation citation = getCitationInstance(row);
                citations.put(citation.id, citation);
                artigos.add(instance);
                row = br.readLine();
            }
        }

        ArrayList<Artigo> lstNomeLongo = new ArrayList<>();
        ArrayList<Artigo> lstNomeCurto = new ArrayList<>();

        // seleciona os artigos aleatoriamente para definir a ordem do agrupamento
        Random r = new Random(1551);
        ArrayList<Artigo> lstArtigos = new ArrayList<>(artigos);

        // Monta lista de artigos com nomes curtos e longos
        while (lstArtigos.size() > 0) {
            int v = r.nextInt(lstArtigos.size());
            Artigo a = lstArtigos.get(v);
            lstArtigos.remove(v);
            String[] nome = a.getAutor().split("[ ]");

            if (nome.length == 2 && nome[0].length() == 1) {
                lstNomeCurto.add(a);
            } else {
                lstNomeLongo.add(a);
            }
        }

        ArrayList<Cluster> clustersHHC = new ArrayList<>();
        criaClustersIniciaisCommonNames(clustersHHC, lstNomeLongo);
        criaClustersIniciaisCommonNames(clustersHHC, lstNomeCurto);

        Collections.sort(clustersHHC);
        int pos = 0;
        for (Cluster c : clustersHHC) {
            c.setArtigosClasseRecebida(pos++);
        }
        HashMap<Integer, Cluster> selected = cluteresIniciaisMaisDissimilaresSemAuthorSimilar(clustersHHC);
        // HashMap<Integer, Cluster> selected = cluteresIniciaisMaisDissimilares(clustersHHC);

        LinkedList<Citation> nctrain = new LinkedList<>();
        LinkedList<Citation> nctest = new LinkedList<>();

        for (Cluster c : clustersHHC) {
            Cluster s = selected.get(c.getClusterPredicted());
            if (s == null) {
                for (Artigo a : c.getArticles()) {
                    Citation cit = citations.get(a.getNumArtigo());
                    if (cit == null) {
                        System.err.println(cit);
                        break;
                    }
                    nctest.add(cit);
                }

            } else {
                for (Artigo a : c.getArticles()) {
                    Citation cit = citations.get(a.getNumArtigo());
                    if (cit == null) {
                        System.err.println(cit);
                        break;
                    }
                    Citation rcit = new Citation(cit.id, cit.classId);

                    int idCluster = a.getNumClasseRecebida();
                    com.cgomez.ncselftraining.classifier.Cluster g = clusters.get(idCluster);
                    if (g == null) {
                        g = new com.cgomez.ncselftraining.classifier.Cluster(idCluster, false);
                        clusters.put(idCluster, g);
                    }

                    cit.classId = g.id;
                    nctrain.add(cit);

                    rcit.predictedAuthor = g;
                    result.add(rcit);
                }
            }
        }

        NC nc = new NC(0, 0, 0, 0, 0, 0, 0, true);
        nc.train(nctrain);
        nc.searchParameters(10, new Random(1313));
        nctest = nc.test(nctest);

        for (Citation c : nctest) {
            int idCluster = c.predictedAuthor.id;
            com.cgomez.ncselftraining.classifier.Cluster g = clusters.get(idCluster);
            if (g == null) {
                g = new com.cgomez.ncselftraining.classifier.Cluster(idCluster, false);
                clusters.put(idCluster, g);
            }

            c.predictedAuthor = g;
            result.add(c);
        }

        return result;
    }

    public ArrayList<TermOcurrence> criaListaTermos(boolean separado) {
        ArrayList<TermOcurrence> listaTermos = new ArrayList<>();

        Iterator<Artigo> iArtigos = artigos.iterator();
        while (iArtigos.hasNext()) {
            Artigo artigo = iArtigos.next();
            listaTermos.addAll(artigo.getListaTermos(separado));
        }

        Collections.sort(listaTermos);

        int i = 0;
        while (i < listaTermos.size() - 1) {
            if (listaTermos.get(i).getTerm().equals(listaTermos.get(i + 1).getTerm())) {
                listaTermos.get(i).setOcurrence(listaTermos.get(i).getOcurrence() + 1);
                listaTermos.remove(i + 1);
            } else
                i++;
        }

        return listaTermos;
    }

    private double[][] montaMatrizSimilaridade(ArrayList<Cluster> clusters) {

        double[][] matriz = new double[clusters.size()][clusters.size()];
        ArrayList<TermOcurrence> listaTermos = criaListaTermos(true);

        // set the centroid of all clusters
        for (Cluster c : clusters) {
            c.setCentroide(listaTermos, artigos.size());
        }

        // calculate the similarity
        for (int i = 0; i < matriz.length; i++) {
            for (int j = i; j < matriz.length; j++) {
                matriz[i][j] = clusters.get(i).cosine(clusters.get(j));
            }
        }

        return matriz;
    }

    public HashMap<Integer, Cluster> cluteresIniciaisMaisDissimilaresSemAuthorSimilar(ArrayList<Cluster> clusters) {
        HashMap<Integer, Cluster> h = new HashMap<>();

        int i = 0;
        int limite = clusters.size();

        double[][] matriz = montaMatrizSimilaridade(clusters);

        while (i < limite) {

            boolean adiciona = true;
            for (Integer j : h.keySet()) {
                // System.out.println(i +" x "+ j +" = "+ matriz[j][i]);
                if (Similarity.ComparacaoFragmentos(clusters.get(i).getArticles().get(0).getAutor(), clusters.get(j).getArticles().get(0).getAutor(),
                        limComp)) {
                    adiciona = false;
                    break;
                }
            }

            if (adiciona) {
                h.put(i, clusters.get(i));
            }

            i++;
        }

        return h;
    }

    public HashMap<Integer, Cluster> cluteresIniciaisMaisDissimilares(ArrayList<Cluster> clustersHHC) {
        HashMap<Integer, Cluster> h = new HashMap<>(clustersHHC.size());

        int i = 0;
        int limite = clustersHHC.size();

        while (i < limite) {

            boolean adiciona = true;

            Cluster a = clustersHHC.get(i);
            for (Integer v : h.keySet()) {
                Cluster b = h.get(v);
                if (Similarity.ComparacaoFragmentos(a.getArticles().get(0).getAutor(), b.getArticles().get(0).getAutor(), limComp)) {
                    adiciona = false;
                }
            }

            if (adiciona) {
                h.put(i, a);
            }

            i++;
        }

        return h;
    }

    private void criaClustersIniciaisCommonNames(ArrayList<Cluster> clusters, ArrayList<Artigo> lstNome) throws FileNotFoundException, IOException {

        HashSet<String> h = new HashSet<>();
        try (BufferedReader f = new BufferedReader(new FileReader("src/main/resources/ncselftraining/commonnames.txt"))) {
            String line;
            while ((line = f.readLine()) != null) {
                h.add(line);
            }
        }

        for (Artigo a : lstNome) {
            boolean achou = false;
            int pos = 0;

            while (pos < clusters.size() && !achou) {
                // verifica se o nome do autor e do coautor sao compativeis
                if (a.getCoautores() != null && clusters.get(pos).getCoauthors().size() > 0 && a.getCoautores().length > 0) {

                    if (Similarity.ComparacaoFragmentos(clusters.get(pos).getAuthors().get(0), a.getAutor(), limComp)) {
                        String[] coA = a.getCoautoresIFFL();
                        ArrayList<String> coC = clusters.get(pos).getCoauthors();
                        double count = 0;
                        boolean found;
                        int j = 0;

                        while (j < coA.length) {
                            String name = coA[j];
                            found = false;
                            int i = 0;

                            while (!found && i < coC.size()) {

                                if (Similarity.ComparacaoFragmentos(name, coC.get(i), limComp)) {
                                    String[] surname = name.split(" ");
                                    if (!h.contains(surname[surname.length - 1])) {
                                        count += 1.0;
                                        found = true;
                                    } else {
                                        count += 0.5;
                                        found = true;
                                    }
                                }
                                i++;
                            }
                            j++;
                        }

                        if (count >= 1) {
                            achou = true;
                            clusters.get(pos).add(a);
                        }
                    }
                }

                pos++;
            }

            if (!achou) {
                Cluster c = new Cluster();
                c.add(a);
                clusters.add(c);
            }
        }
    }

    private Artigo getTestInstance(String row) {
        if (row.contains("CLASS=")) {
            return null;
        } else {
            return getTestInstanceNCFormat(row);
        }
    }

    private Artigo getTestInstanceNCFormat(String row) {
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        // System.out.println(row);
        String[] pieces = row.split(";");

        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);

        LinkedList<String> listCoauthors = new LinkedList<>();
        String[] aux = pieces[3].split(",");
        for (String coauthor : aux) {
            if (coauthor.length() < 1) {
                continue;
            }

            listCoauthors.add(coauthor);
        }

        String[] coauthors = new String[listCoauthors.size()];
        coauthors = listCoauthors.toArray(coauthors);

        Artigo artigo = new Artigo(citationId, classId, citationId, pieces[2], coauthors, pieces[4], pieces[5]);

        // System.out.println(row);
        // System.out.println(artigo);

        return artigo;
    }

    private Citation getCitationInstance(String row) {
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        // System.out.println(row);
        HashMap<String, Boolean> aux = new HashMap<>();
        String[] pieces = row.split(";");

        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);
        Citation citation = new Citation(citationId, classId);

        String termId;
        String[] coauthors = pieces[3].split(",");
        for (String coauthor : coauthors) {
            termId = "c=" + coauthor;
            if (aux.containsKey(termId) || coauthor.length() < 1) {
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null) {
                term = new Term(coauthor, 0);
                terms.put(termId, term);
            }
            citation.coauthors.add(term);
        }

        if (pieces[2].length() > 0) {
            String[] name = pieces[2].split("[ ]");
            if (name.length > 2 || name[0].length() > 1) {
                termId = "c=" + pieces[2];
                if (!aux.containsKey(termId)) {
                    Term term = terms.get(termId);
                    if (term == null) {
                        term = new Term(pieces[2], 0);
                        terms.put(termId, term);
                    }
                    citation.coauthors.add(term);
                }
            }
        }

        String[] titleTerms = pieces[4].split(" ");
        for (String t : titleTerms) {
            termId = "t=" + t;
            if (aux.containsKey(termId) || t.length() < 1) {
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null) {
                term = new Term(t, 0);
                terms.put(termId, term);
            }
            citation.title.add(term);
        }

        String[] venueTerms = pieces[5].split(" ");
        for (String t : venueTerms) {
            termId = "v=" + t;
            if (aux.containsKey(termId) || t.length() < 1) {
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null) {
                term = new Term(t, 0);
                terms.put(termId, term);
            }
            citation.publicationVenue.add(term);
        }

        // System.out.println(citation);
        return citation;
    }
}
