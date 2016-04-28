/*     */ package com.cgomez.indi;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Indi
/*     */ {
/*     */   public static int classificaNovosAutores(Map<Integer, Cluster> clusterAutores, Base base, ArrayList<Artigo> lstArtigosNovos, double simVenue, double simTitle, double p_lim, int qtde, int maiorClasse, double incremento, int numArq, int numBase)
/*     */     throws Exception
/*     */   {
/*  21 */     for (Artigo artigoNovo : lstArtigosNovos) {
/*  22 */       boolean achou = false;
/*  23 */       Iterator<Cluster> it = clusterAutores.values().iterator();
/*  24 */       while ((it.hasNext()) && (!
/*  25 */         achou)) {
/*  26 */         Cluster clusterSimilar = (Cluster)it.next();
/*     */         
/*     */ 
/*  29 */         if (Similarity.ComparacaoFragmentos(clusterSimilar
/*  30 */           .getRepresentativeName(), artigoNovo.getAutor(), p_lim)) {
/*  31 */           boolean heuristicaCoautores = false;
/*  32 */           boolean heuristicaTitulo = false;
/*     */           
/*     */ 
/*  35 */           heuristicaCoautores = ComparacaoFragmentosCoautorGrupo(
/*  36 */             clusterSimilar, artigoNovo, p_lim, qtde);
/*  37 */           if (heuristicaCoautores)
/*     */           {
/*     */ 
/*  40 */             heuristicaTitulo = fusaoGrupos(clusterSimilar, 
/*  41 */               artigoNovo, simTitle, simVenue);
/*     */ 
/*     */ 
/*     */           }
/*  45 */           else if ((artigoNovo.getCoautores() == null) || 
/*  46 */             (clusterSimilar.getCoauthors().isEmpty())) {
/*  47 */             heuristicaCoautores = true;
/*  48 */             heuristicaTitulo = fusaoGrupos(clusterSimilar, 
/*  49 */               artigoNovo, simTitle + incremento, simVenue + 
/*  50 */               incremento);
/*     */           }
/*     */           
/*  53 */           if ((heuristicaCoautores) && (heuristicaTitulo)) {
/*  54 */             achou = true;
/*  55 */             artigoNovo.setNumClasseRecebida(clusterSimilar
/*  56 */               .getNumber());
/*     */             
/*  58 */             Cluster c = (Cluster)clusterAutores.get(
/*  59 */               Integer.valueOf(clusterSimilar.getNumber()));
/*  60 */             c.add(artigoNovo);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*  65 */       if (!achou) {
/*  66 */         artigoNovo.setNumClasseRecebida(maiorClasse);
/*  67 */         maiorClasse++;
/*     */         
/*  69 */         Cluster clusterTemp = new Cluster(artigoNovo
/*  70 */           .getNumClasseRecebida());
/*  71 */         clusterTemp.add(artigoNovo);
/*  72 */         clusterAutores.put(Integer.valueOf(artigoNovo.getNumClasseRecebida()), 
/*  73 */           clusterTemp);
/*     */       }
/*     */       
/*  76 */       base.getArtigos().add(artigoNovo);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  89 */     return maiorClasse;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static boolean fusaoGrupos(Cluster cluster, Artigo artigoNovo, double p_lim_sim_tit, double p_lim_sim_local)
/*     */     throws Exception
/*     */   {
/*  99 */     if ((Similarity.cosineDistance(Util.toArray(cluster.getTitle()), Util.toArray(artigoNovo.getTitulo())) > p_lim_sim_tit) || 
/*     */     
/* 101 */       (Similarity.cosineDistance(Util.toArray(cluster.getVenue()), Util.toArray(artigoNovo.getVeiculoPublicacao())) > p_lim_sim_local)) {
/* 102 */       return true;
/*     */     }
/* 104 */     return false;
/*     */   }
/*     */   
/*     */   public static String limpaNome(String autor)
/*     */   {
/* 109 */     autor = Disambiguate.changeHTMLCodeToASC(autor);
/* 110 */     autor = autor.replaceAll("-", " ");
/* 111 */     autor = autor.replaceAll("'|'", "");
/* 112 */     autor = autor.replaceAll("&apos;", "");
/* 113 */     autor = autor.replaceAll("&apos", "");
/* 114 */     autor = autor.replaceAll("&#180", "");
/* 115 */     autor = autor.replaceAll("&#146", "");
/* 116 */     autor = autor.replaceAll(" jr", " ");
/* 117 */     autor = autor.replaceAll("[ ]junior", " ");
/*     */     
/* 119 */     autor = autor.replaceAll(" filho", " ");
/* 120 */     autor = autor.trim();
/* 121 */     return autor;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean ComparacaoFragmentosCoautorGrupo(Cluster grupo, Artigo artigoNovo, double p_lim, int qtde)
/*     */     throws Exception
/*     */   {
/* 128 */     int k = 0;
/*     */     
/* 130 */     if ((artigoNovo.getCoautores() == null) || 
/* 131 */       (grupo.coauthorsToArray() == null)) {
/* 132 */       return false;
/*     */     }
/* 134 */     for (int j = 0; j < artigoNovo.getCoautores().length; j++) {
/* 135 */       boolean achou = false;
/* 136 */       for (int i = 0; i < grupo.coauthorsToArray().length; i++) {
/* 137 */         if ((grupo.coauthorsToArray()[i] != null) && 
/* 138 */           (artigoNovo.getCoautores()[j] != null) && 
/* 139 */           (Similarity.ComparacaoFragmentos(grupo
/* 140 */           .coauthorsToArray()[i], 
/* 141 */           artigoNovo.getCoautores()[j], p_lim)))
/* 142 */           if (!achou) {
/* 143 */             k++;
/* 144 */             achou = true;
/*     */           }
/*     */       }
/*     */     }
/* 148 */     if (k >= qtde) {
/* 149 */       return true;
/*     */     }
/* 151 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void main2(String[] args)
/*     */     throws Exception
/*     */   {
/* 158 */     double limComp = 0.3D;
/* 159 */     int maiorClasse = 90000;
/* 160 */     int qtde = 1;
/* 161 */     double simTitle = 0.1D;
/* 162 */     double simVenue = 0.9D;
/* 163 */     double incremento = 0.2D;
/* 164 */     Leitura leitura = new Leitura();
/*     */     
/*     */ 
/* 167 */     for (int colecao = 5; colecao <= 10; colecao = 5)
/*     */     {
/* 169 */       for (int j = 1; j <= 5; j++) {
/* 170 */         maiorClasse = 90000;
/* 171 */         ArrayList<Artigo> artigos = new ArrayList();
/* 172 */         Map<Integer, Cluster> clusterAutores = new HashMap();
/* 173 */         leitura.leituraBase("synt_bases_0" + colecao + "_080/base_" + j + "/base0.txt", artigos, 
/* 174 */           clusterAutores);
/* 175 */         Base base = new Base();
/* 176 */         base.getArtigos().addAll(artigos);
/*     */         
/* 178 */         for (int i = 1; i <= 10; i++) {
/* 179 */           ArrayList<Artigo> lstArtigosNovos = new ArrayList();
/* 180 */           leitura.leituraBase("synt_bases_0" + colecao + "_080/base_" + j + "/base" + i + ".txt", 
/* 181 */             lstArtigosNovos);
/* 182 */           long inicio = System.currentTimeMillis();
/* 183 */           maiorClasse = classificaNovosAutores(clusterAutores, base, 
/* 184 */             lstArtigosNovos, simVenue, simTitle, limComp, qtde, 
/* 185 */             maiorClasse, incremento, i, j);
/* 186 */           long fim = System.currentTimeMillis();
/* 187 */           System.out.println("Colecao " + colecao + "\tBase " + j + "\tArquivo " + i + "\tDuracao " + (fim - inicio));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/* 195 */     double limComp = 0.3D;
/* 196 */     int maiorClasse = 90000;
/* 197 */     int qtde = 1;
/* 198 */     double simTitle = 0.1D;
/* 199 */     double simVenue = 0.9D;
/* 200 */     double incremento = 0.2D;
/* 201 */     int begin = 0;int end = 10;
/* 202 */     String directory = "./";
/* 203 */     Leitura leitura = new Leitura();
/*     */     
/* 205 */     if ((args.length != 3) && (args.length != 5)) {
/* 206 */       System.err.println("Erro: invalid number of parameters.");
/* 207 */       System.err.println("Run: java -jar indi.jar <directory> <begin> <end> <simTitle> <simVenue>");
/* 208 */       System.err.println("<directory> - folder that contains files with the records.");
/* 209 */       System.err.println("<begin> - initial year");
/* 210 */       System.err.println("<end> - final year");
/* 211 */       System.err.println("<simTitle> - similarity threshold used to compare the work titles - default 0.1");
/* 212 */       System.err.println("<sinVenue> - similarity threshold used to compare the publication venue titles - default 0.9");
/* 213 */       System.err.println("For instance, if the folder 'collection' contains the files \n'Base_2000.txt', 'Base_2001.txt', 'Base_2002.txt' and 'Base_2003.txt', you must run the program as\n 'java -jar indi.jar collection 2000 2003 0.1 0.9'.");
/* 214 */       System.exit(1);
/*     */     }
/* 216 */     else if (args.length == 1) {
/* 217 */       directory = args[0];
/*     */     }
/* 219 */     else if (args.length == 3) {
/* 220 */       directory = args[0];
/* 221 */       begin = Integer.parseInt(args[1]);
/* 222 */       end = Integer.parseInt(args[2]);
/*     */     }
/*     */     else {
/* 225 */       directory = args[0];
/* 226 */       begin = Integer.parseInt(args[1]);
/* 227 */       end = Integer.parseInt(args[2]);
/* 228 */       simTitle = Double.parseDouble(args[3]);
/* 229 */       simVenue = Double.parseDouble(args[4]);
/*     */     }
/*     */     
/* 232 */     maiorClasse = 0;
/*     */     
/* 234 */     Map<Integer, Cluster> clusterAutores = new HashMap();
/*     */     
/* 236 */     Base base = new Base();
/*     */     ArrayList<Artigo> lstArtigosNovos;
/* 238 */     for (int i = begin; i <= end; i++) {
/* 239 */       lstArtigosNovos = new ArrayList();
/* 240 */       leitura.leituraBase(directory + "/Base_" + i + ".txt", lstArtigosNovos);
/* 241 */       long inicio = System.currentTimeMillis();
/* 242 */       maiorClasse = classificaNovosAutores(clusterAutores, base, 
/* 243 */         lstArtigosNovos, simVenue, simTitle, limComp, qtde, 
/* 244 */         maiorClasse, incremento, i, 0);
/* 245 */       long fim = System.currentTimeMillis();
/* 246 */       System.out.println("Collection: " + directory + "\tFile: Base_" + i + ".txt\tTime: " + (fim - inicio));
/*     */     }
/* 248 */     for (Artigo a : base.getArtigos()) {
/* 249 */       System.out.println(a.getNumClasseRecebida() + "_" + a.toStringArqDen());
/*     */     }
/*     */     
/* 252 */     ArrayList<Grupo> gruposManuais = base.criaGruposManuais();
/* 253 */     ArrayList<Grupo> gruposAutomaticos = base.criaGruposAutomaticos();
/* 254 */     int N = base.getArtigos().size();
/* 255 */     double pmg = GrupoAmbiguo.PMG(gruposAutomaticos, gruposManuais, N);
/* 256 */     double pma = GrupoAmbiguo.PMA(gruposAutomaticos, gruposManuais, N);
/* 257 */     double k = GrupoAmbiguo.K(pmg, pma);
/* 258 */     System.out.println("\tInc=" + incremento + "\tSimTitle=" + simTitle + "\tSimVenue=" + simVenue + 
/* 259 */       "\t" + pmg + "\t" + pma + "\t" + k);
/*     */   }
/*     */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Indi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */