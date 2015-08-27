package com.cgomez.sand.classifier.sand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.cgomez.sand.classifier.Citation;
import com.cgomez.sand.classifier.Evaluate;
import com.cgomez.sand.classifier.Instance;
import com.cgomez.sand.classifier.sland.SLAND;
import com.cgomez.sand.hhc.Similarity;
import com.cgomez.sand.po.Artigo;
import com.cgomez.sand.po.Cluster;
import com.cgomez.sand.po.Parametros;
import com.cgomez.sand.po.TermOcurrence;

public class SAND {

    double phi = 0.1;
    double limComp = 0.3;
    private ArrayList<Artigo> artigos;

    public SAND(double phi) {
        this.phi = phi;
        artigos = new ArrayList<>();
    }

    public void initializeModel() {
        artigos = new ArrayList<>();
    }

    public LinkedList<Instance> test(String dataset) throws FileNotFoundException, IOException, InterruptedException {
        LinkedList<Instance> test = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
            String row = br.readLine();
            while (row != null) {
                Artigo instance = getTestInstance(row);
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

        ArrayList<Cluster> clusters = new ArrayList<>();
        criaClustersIniciaisCommonNames(clusters, lstNomeLongo);
        criaClustersIniciaisCommonNames(clusters, lstNomeCurto);

        fazFusaoClusterCoauthor(clusters, 0.5);
        Collections.sort(clusters);

        int pos = 0;
        for (Cluster c : clusters) {
            c.setArtigosClasseRecebida(pos++);
        }

        fazFusaoSLANDPorCluster("", clusters);

        for (Artigo a : artigos) {
            test.add(new Citation(a.getNumArtigo(), a.getNumClasse(), a.getNumClasseRecebida()));
        }

        return test;
    }

    private void criaClustersIniciaisCommonNames(ArrayList<Cluster> clusters, ArrayList<Artigo> lstNome) throws FileNotFoundException, IOException {

        HashSet<String> h = new HashSet<>();
        try (BufferedReader f = new BufferedReader(new FileReader("src/main/resources/sand/commonnames.txt"))) {
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
                                        count += 1;
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

    private void fazFusaoClusterCoauthor(ArrayList<Cluster> clusters, double percCoauthor) {
        boolean houveFusao = true;

        while (houveFusao) {
            houveFusao = false;
            for (int i = 0; i < clusters.size() - 1; i++) {
                Cluster c1 = clusters.get(i);
                int j = i + 1;
                while (j < clusters.size()) {
                    Cluster c2 = clusters.get(j);

                    if (c1.similarityAuthorNames(c2, limComp)) {
                        int qtdeCoauthors;
                        if (c2.getCoauthors().size() > c1.getCoauthors().size()) {
                            qtdeCoauthors = (int) Math.ceil(c1.getCoauthors().size() * percCoauthor);
                        } else {
                            qtdeCoauthors = (int) Math.ceil(c2.getCoauthors().size() * percCoauthor);
                        }
                        int qtdeCompativeis = 0; // qtde de compativeis
                        for (String co1 : c1.getCoauthors()) {
                            for (String co2 : c2.getCoauthors()) {
                                if (Similarity.ComparacaoFragmentos(co1, co2, limComp)) {
                                    qtdeCompativeis++;
                                }
                            }
                        }
                        if (qtdeCompativeis >= qtdeCoauthors && qtdeCompativeis > 1) {
                            c1.fusao(c2);
                            clusters.remove(j);
                            houveFusao = true;
                        } else
                            j++;
                    } else
                        j++;
                }
            }
        }
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

    private void atualizaClasseRecebida(int id, int classeRecebida) {
        Iterator<Artigo> iArt = artigos.iterator();
        while (iArt.hasNext()) {
            Artigo a = iArt.next();
            if (a.getNumArtigo() == id) {
                a.setNumClasseRecebida(classeRecebida);
                return;
            }
        }
        System.err.println("Artigo de numero " + id + " nao foi encontrado");
    }

    private Artigo getArtigo(int id) {
        Iterator<Artigo> iArt = artigos.iterator();
        while (iArt.hasNext()) {
            Artigo a = iArt.next();
            if (a.getNumArtigo() == id) {
                return a;
            }
        }
        System.err.println("Artigo de numero " + id + " nao foi encontrado (2)");
        return null;
    }

    private Parametros fazFusaoSLANDPorCluster(String author, ArrayList<Cluster> clusters) throws IOException, InterruptedException {

        Parametros param = new Parametros("0", "0");

        Collections.sort(clusters);
        int pos = 0;
        for (Cluster c : clusters) {
            c.setArtigosClasseRecebida(pos++);
        }

        double[][] matrizSim = montaMatrizSimilaridade(clusters);

        String nomeArq = SLAND.temp_path + "/dataset_lac";
        HashMap<String, String> h = cluteresIniciaisMaisDissimilaresSemAuthorSimilar(clusters, matrizSim);

        int qtdeClusterTreino = h.size();
        System.out.println("Numero de clusters: " + clusters.size() + "\tSelecionados: " + h.size());

        int iCluster;

        while (qtdeClusterTreino < clusters.size()) {

            // Separa em treino e teste
            try (FileWriter fileTreino = new FileWriter(nomeArq + ".treino")) {
                for (String v : h.keySet()) {
                    iCluster = Integer.parseInt(v);
                    Cluster clusterTreino = clusters.get(iCluster);
                    for (int iArt = 0; iArt < clusterTreino.getArticles().size(); iArt++) {
                        Artigo a = clusterTreino.getArticles().get(iArt);
                        String strLand = a.toStringLand(iCluster + "");
                        fileTreino.write(strLand + "\n");
                    }
                }
            }

            iCluster = 0;
            while (h.get(iCluster + "") != null && iCluster < clusters.size()) {
                iCluster++;
            }

            if (iCluster < clusters.size()) {

                try (FileWriter fileTeste = new FileWriter(nomeArq + ".teste")) {
                    for (int iArt = 0; iArt < clusters.get(iCluster).getArticles().size(); iArt++) {
                        Artigo a = clusters.get(iCluster).getArticles().get(iArt);
                        String strLand = a.toStringLand(iCluster + "");
                        fileTeste.write(strLand + "\n");
                    }
                }

                // treina e testa
                if (param.custo.equals("0")) {
                    param = CrossValidation.procura(nomeArq + ".treino", author);
                }

                double deltaMin = Double.parseDouble(param.custo);

                SLAND sland = new SLAND(Double.parseDouble(param.custo), Integer.parseInt(param.gama));
                sland.train(nomeArq + ".treino");
                Evaluate eval = sland.test(nomeArq + ".teste");

                for (Instance citation : eval.getSet()) {
                    atualizaClasseRecebida(citation.id, citation.predictedClassId);
                }

                // Verifica qual foi a classe com maior classificação
                int maiorClasse = -1, qtdeMaiorClasse = 0, segMaior = -1, segQtdeMaior = 0;
                HashMap<String, Integer> h_Qtde = new HashMap<>();
                for (int iArt = 0; iArt < clusters.get(iCluster).getArticles().size(); iArt++) {
                    Artigo a = clusters.get(iCluster).getArticles().get(iArt);
                    if (h_Qtde.containsKey(a.getNumClasseRecebida() + "")) {
                        Integer valor = h_Qtde.get(a.getNumClasseRecebida() + "");
                        valor = valor + 1;
                        h_Qtde.put(a.getNumClasseRecebida() + "", valor);
                    } else {
                        h_Qtde.put(a.getNumClasseRecebida() + "", 1);
                    }
                }

                Iterator<String> iKey = h_Qtde.keySet().iterator();
                while (iKey.hasNext()) {
                    String key = iKey.next();
                    int valor = h_Qtde.get(key);
                    if (qtdeMaiorClasse < valor) {
                        segQtdeMaior = qtdeMaiorClasse;
                        segMaior = maiorClasse;
                        qtdeMaiorClasse = valor;
                        maiorClasse = Integer.parseInt(key);
                    } else if (segQtdeMaior < valor) {
                        segQtdeMaior = valor;
                        segMaior = Integer.parseInt(key);
                    }
                }

                boolean fundido = false;
                boolean teste = h.containsKey(maiorClasse + "") ? (((segQtdeMaior > 0) ? (double) qtdeMaiorClasse / (double) segQtdeMaior > deltaMin
                        : true) ? clusters.get(maiorClasse).similarityAuthorNames(clusters.get(iCluster), limComp) : false) : false;

                if (teste) {
                    if (h.get(maiorClasse + "") != null) {
                        clusters.get(maiorClasse).fusao(clusters.get(iCluster));
                        clusters.remove(iCluster);
                        fundido = true;
                    }
                }

                // se nao fundiu
                if (!fundido) {
                    clusters.get(iCluster).restauraClasseRecebida();
                    qtdeClusterTreino++;
                    h.put(iCluster + "", iCluster + "");
                } else { // atualiza hash de treino
                    HashMap<String, String> hTemp = new HashMap<>();
                    for (String v : h.keySet()) {
                        int value = Integer.parseInt(v);
                        if (value > iCluster) {
                            hTemp.put((value - 1) + "", (value - 1) + "");
                        } else
                            hTemp.put(v, v);
                    }
                    h = hTemp;
                }

            } else {

                try (FileWriter fileTeste = new FileWriter(nomeArq + ".teste")) {
                    while (iCluster < clusters.size()) {
                        if (h.get(iCluster + "") == null) {
                            for (int iArt = 0; iArt < clusters.get(iCluster).getArticles().size(); iArt++) {
                                Artigo a = clusters.get(iCluster).getArticles().get(iArt);
                                String strLand = a.toStringLand(iCluster + "");
                                fileTeste.write(strLand + "\n");
                            }
                        }
                        iCluster++;
                    }
                }

                // treina e testa
                param = CrossValidation.procura(nomeArq + ".treino", author);

                com.cgomez.sand.classifier.sland.SLAND sland = new com.cgomez.sand.classifier.sland.SLAND(Double.parseDouble(param.custo),
                        Integer.parseInt(param.gama));
                sland.train(nomeArq + ".treino");
                Evaluate eval = sland.test(nomeArq + ".teste");

                for (Instance citation : eval.getSet()) {
                    atualizaClasseRecebida(citation.id, citation.predictedClassId);

                    Artigo art = getArtigo(citation.id);

                    if (citation.predictedClassId != citation.classId) {

                        if (com.cgomez.sand.hhc.Similarity.ComparacaoFragmentos(clusters.get(citation.classId).getArticles().get(0).getAutor(),
                                art.getAutor(), limComp)) {
                            atualizaClasseRecebida(citation.id, citation.predictedClassId);
                        } else {
                            atualizaClasseRecebida(citation.id, citation.classId);
                        }
                    } else {
                        atualizaClasseRecebida(citation.id, citation.predictedClassId);
                    }
                }

                qtdeClusterTreino = iCluster;
            }
        }

        return param;
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

    public HashMap<String, String> cluteresIniciaisMaisDissimilaresSemAuthorSimilar(ArrayList<Cluster> clusters, double[][] matriz) {
        HashMap<String, String> h = new HashMap<>();

        int i = 0;
        int limite = clusters.size();

        while (i < limite) {

            boolean adiciona = true;
            for (String v : h.keySet()) {
                int j = Integer.parseInt(h.get(v));
                if (matriz[j][i] >= phi
                        && Similarity.ComparacaoFragmentos(clusters.get(i).getArticles().get(0).getAutor(), clusters.get(j).getArticles().get(0)
                                .getAutor(), limComp)) {
                    adiciona = false;
                    break;
                }
            }

            if (adiciona) {
                h.put(i + "", i + "");
            }

            i++;
        }

        return h;
    }
}
