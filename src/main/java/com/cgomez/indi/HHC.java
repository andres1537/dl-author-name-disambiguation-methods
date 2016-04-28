/*     */ package com.cgomez.indi;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ public class HHC
/*     */ {
/*     */   /* Error */
/*     */   public static void process(Base base, double simTitle, double simVenue)
/*     */     throws Exception
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 19	java/util/ArrayList
/*     */     //   3: dup
/*     */     //   4: invokespecial 21	java/util/ArrayList:<init>	()V
/*     */     //   7: astore 5
/*     */     //   9: new 19	java/util/ArrayList
/*     */     //   12: dup
/*     */     //   13: invokespecial 21	java/util/ArrayList:<init>	()V
/*     */     //   16: astore 6
/*     */     //   18: new 22	java/util/Random
/*     */     //   21: dup
/*     */     //   22: invokespecial 24	java/util/Random:<init>	()V
/*     */     //   25: astore 7
/*     */     //   27: ldc2_w 25
/*     */     //   30: dstore 8
/*     */     //   32: new 19	java/util/ArrayList
/*     */     //   35: dup
/*     */     //   36: aload_0
/*     */     //   37: invokevirtual 27	bdbcomp/Base:getArtigos	()Ljava/util/ArrayList;
/*     */     //   40: invokespecial 33	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
/*     */     //   43: astore 10
/*     */     //   45: goto +84 -> 129
/*     */     //   48: aload 7
/*     */     //   50: aload 10
/*     */     //   52: invokevirtual 36	java/util/ArrayList:size	()I
/*     */     //   55: invokevirtual 40	java/util/Random:nextInt	(I)I
/*     */     //   58: istore 11
/*     */     //   60: aload 10
/*     */     //   62: iload 11
/*     */     //   64: invokevirtual 44	java/util/ArrayList:get	(I)Ljava/lang/Object;
/*     */     //   67: checkcast 48	bdbcomp/Artigo
/*     */     //   70: astore 12
/*     */     //   72: aload 10
/*     */     //   74: iload 11
/*     */     //   76: invokevirtual 50	java/util/ArrayList:remove	(I)Ljava/lang/Object;
/*     */     //   79: pop
/*     */     //   80: aload 12
/*     */     //   82: invokevirtual 53	bdbcomp/Artigo:getAutor	()Ljava/lang/String;
/*     */     //   85: ldc 57
/*     */     //   87: invokevirtual 59	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
/*     */     //   90: astore 13
/*     */     //   92: aload 13
/*     */     //   94: arraylength
/*     */     //   95: iconst_2
/*     */     //   96: if_icmpne +25 -> 121
/*     */     //   99: aload 13
/*     */     //   101: iconst_0
/*     */     //   102: aaload
/*     */     //   103: invokevirtual 65	java/lang/String:length	()I
/*     */     //   106: iconst_1
/*     */     //   107: if_icmpne +14 -> 121
/*     */     //   110: aload 6
/*     */     //   112: aload 12
/*     */     //   114: invokevirtual 68	java/util/ArrayList:add	(Ljava/lang/Object;)Z
/*     */     //   117: pop
/*     */     //   118: goto +11 -> 129
/*     */     //   121: aload 5
/*     */     //   123: aload 12
/*     */     //   125: invokevirtual 68	java/util/ArrayList:add	(Ljava/lang/Object;)Z
/*     */     //   128: pop
/*     */     //   129: aload 10
/*     */     //   131: invokevirtual 36	java/util/ArrayList:size	()I
/*     */     //   134: ifgt -86 -> 48
/*     */     //   137: new 19	java/util/ArrayList
/*     */     //   140: dup
/*     */     //   141: invokespecial 21	java/util/ArrayList:<init>	()V
/*     */     //   144: astore 11
/*     */     //   146: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   149: ldc 78
/*     */     //   151: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   154: aload 11
/*     */     //   156: aload 5
/*     */     //   158: dload 8
/*     */     //   160: invokestatic 86	bdbcomp/HHC:criaClustersIniciais	(Ljava/util/ArrayList;Ljava/util/ArrayList;D)V
/*     */     //   163: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   166: new 90	java/lang/StringBuilder
/*     */     //   169: dup
/*     */     //   170: ldc 92
/*     */     //   172: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   175: aload 11
/*     */     //   177: invokevirtual 36	java/util/ArrayList:size	()I
/*     */     //   180: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   183: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   186: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   189: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   192: ldc 103
/*     */     //   194: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   197: aload 11
/*     */     //   199: aload 6
/*     */     //   201: dload 8
/*     */     //   203: invokestatic 86	bdbcomp/HHC:criaClustersIniciais	(Ljava/util/ArrayList;Ljava/util/ArrayList;D)V
/*     */     //   206: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   209: new 90	java/lang/StringBuilder
/*     */     //   212: dup
/*     */     //   213: ldc 92
/*     */     //   215: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   218: aload 11
/*     */     //   220: invokevirtual 36	java/util/ArrayList:size	()I
/*     */     //   223: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   226: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   229: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   232: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   235: ldc 105
/*     */     //   237: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   240: aload 11
/*     */     //   242: ldc2_w 107
/*     */     //   245: dload 8
/*     */     //   247: invokestatic 109	bdbcomp/HHC:fazFusaoClusterCoauthor	(Ljava/util/ArrayList;DD)V
/*     */     //   250: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   253: new 90	java/lang/StringBuilder
/*     */     //   256: dup
/*     */     //   257: ldc 113
/*     */     //   259: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   262: aload 11
/*     */     //   264: invokevirtual 36	java/util/ArrayList:size	()I
/*     */     //   267: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   270: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   273: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   276: aload 11
/*     */     //   278: invokestatic 115	java/util/Collections:sort	(Ljava/util/List;)V
/*     */     //   281: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   284: new 90	java/lang/StringBuilder
/*     */     //   287: dup
/*     */     //   288: ldc 121
/*     */     //   290: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   293: aload 11
/*     */     //   295: invokevirtual 36	java/util/ArrayList:size	()I
/*     */     //   298: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   301: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   304: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   307: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   310: ldc 123
/*     */     //   312: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   315: aload 11
/*     */     //   317: dload 8
/*     */     //   319: dload_1
/*     */     //   320: dload_3
/*     */     //   321: invokestatic 125	bdbcomp/HHC:fusaoGrupos	(Ljava/util/ArrayList;DDD)V
/*     */     //   324: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   327: new 90	java/lang/StringBuilder
/*     */     //   330: dup
/*     */     //   331: ldc -127
/*     */     //   333: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   336: aload 11
/*     */     //   338: invokevirtual 36	java/util/ArrayList:size	()I
/*     */     //   341: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   344: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   347: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   350: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   353: ldc -125
/*     */     //   355: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   358: aload_0
/*     */     //   359: ldc2_w 25
/*     */     //   362: invokestatic 133	bdbcomp/HHC:obterNomesMenosFrequente	(Lbdbcomp/Base;D)Ljava/util/HashSet;
/*     */     //   365: astore 12
/*     */     //   367: aload 12
/*     */     //   369: ldc -119
/*     */     //   371: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   374: pop
/*     */     //   375: aload 12
/*     */     //   377: ldc -114
/*     */     //   379: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   382: pop
/*     */     //   383: aload 12
/*     */     //   385: ldc -112
/*     */     //   387: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   390: pop
/*     */     //   391: aload 12
/*     */     //   393: ldc -110
/*     */     //   395: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   398: pop
/*     */     //   399: aload 12
/*     */     //   401: ldc -108
/*     */     //   403: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   406: pop
/*     */     //   407: aload 12
/*     */     //   409: ldc -106
/*     */     //   411: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   414: pop
/*     */     //   415: aload 12
/*     */     //   417: ldc -104
/*     */     //   419: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   422: pop
/*     */     //   423: aload 12
/*     */     //   425: ldc -102
/*     */     //   427: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   430: pop
/*     */     //   431: aload 12
/*     */     //   433: ldc -100
/*     */     //   435: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   438: pop
/*     */     //   439: aload 12
/*     */     //   441: ldc -98
/*     */     //   443: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   446: pop
/*     */     //   447: aload 12
/*     */     //   449: ldc -96
/*     */     //   451: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   454: pop
/*     */     //   455: aload 12
/*     */     //   457: ldc -94
/*     */     //   459: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   462: pop
/*     */     //   463: aload 12
/*     */     //   465: ldc -92
/*     */     //   467: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   470: pop
/*     */     //   471: aload 12
/*     */     //   473: ldc -90
/*     */     //   475: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   478: pop
/*     */     //   479: aload 12
/*     */     //   481: ldc -88
/*     */     //   483: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   486: pop
/*     */     //   487: aload 12
/*     */     //   489: ldc -86
/*     */     //   491: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   494: pop
/*     */     //   495: aload 12
/*     */     //   497: ldc -104
/*     */     //   499: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   502: pop
/*     */     //   503: aload 12
/*     */     //   505: ldc -84
/*     */     //   507: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   510: pop
/*     */     //   511: aload 12
/*     */     //   513: ldc -82
/*     */     //   515: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   518: pop
/*     */     //   519: aload 12
/*     */     //   521: ldc -80
/*     */     //   523: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   526: pop
/*     */     //   527: aload 12
/*     */     //   529: ldc -78
/*     */     //   531: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   534: pop
/*     */     //   535: aload 12
/*     */     //   537: ldc -76
/*     */     //   539: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   542: pop
/*     */     //   543: aload 12
/*     */     //   545: ldc -74
/*     */     //   547: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   550: pop
/*     */     //   551: aload 12
/*     */     //   553: ldc -72
/*     */     //   555: invokevirtual 139	java/util/HashSet:add	(Ljava/lang/Object;)Z
/*     */     //   558: pop
/*     */     //   559: aload 11
/*     */     //   561: aload 12
/*     */     //   563: dload 8
/*     */     //   565: invokestatic 186	bdbcomp/HHC:fazFusaoUsandoNomeInComum	(Ljava/util/ArrayList;Ljava/util/HashSet;D)V
/*     */     //   568: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   571: new 90	java/lang/StringBuilder
/*     */     //   574: dup
/*     */     //   575: ldc -66
/*     */     //   577: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   580: aload 11
/*     */     //   582: invokevirtual 36	java/util/ArrayList:size	()I
/*     */     //   585: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   588: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   591: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   594: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   597: ldc -64
/*     */     //   599: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   602: aload 11
/*     */     //   604: iconst_2
/*     */     //   605: invokestatic 194	bdbcomp/HHC:fazFusaoUsandoQtdeSobreNomeMaiorQue	(Ljava/util/ArrayList;I)V
/*     */     //   608: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   611: new 90	java/lang/StringBuilder
/*     */     //   614: dup
/*     */     //   615: ldc -58
/*     */     //   617: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   620: aload 11
/*     */     //   622: invokevirtual 36	java/util/ArrayList:size	()I
/*     */     //   625: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   628: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   631: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   634: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   637: ldc -56
/*     */     //   639: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   642: aload 11
/*     */     //   644: ldc2_w 202
/*     */     //   647: dload 8
/*     */     //   649: invokestatic 109	bdbcomp/HHC:fazFusaoClusterCoauthor	(Ljava/util/ArrayList;DD)V
/*     */     //   652: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   655: new 90	java/lang/StringBuilder
/*     */     //   658: dup
/*     */     //   659: ldc -52
/*     */     //   661: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   664: aload 11
/*     */     //   666: invokevirtual 36	java/util/ArrayList:size	()I
/*     */     //   669: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   672: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   675: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   678: aload 11
/*     */     //   680: invokevirtual 206	java/util/ArrayList:iterator	()Ljava/util/Iterator;
/*     */     //   683: astore 14
/*     */     //   685: goto +20 -> 705
/*     */     //   688: aload 14
/*     */     //   690: invokeinterface 210 1 0
/*     */     //   695: checkcast 216	bdbcomp/Cluster
/*     */     //   698: astore 13
/*     */     //   700: aload 13
/*     */     //   702: invokevirtual 218	bdbcomp/Cluster:setFrequentName	()V
/*     */     //   705: aload 14
/*     */     //   707: invokeinterface 221 1 0
/*     */     //   712: ifne -24 -> 688
/*     */     //   715: aconst_null
/*     */     //   716: astore 13
/*     */     //   718: getstatic 225	bdbcomp/Disambiguate:database	Ljava/lang/String;
/*     */     //   721: getstatic 231	bdbcomp/Disambiguate:user	Ljava/lang/String;
/*     */     //   724: getstatic 234	bdbcomp/Disambiguate:password	Ljava/lang/String;
/*     */     //   727: invokestatic 237	java/sql/DriverManager:getConnection	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/sql/Connection;
/*     */     //   730: astore 13
/*     */     //   732: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   735: ldc -13
/*     */     //   737: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   740: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   743: ldc -11
/*     */     //   745: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   748: aload 13
/*     */     //   750: invokeinterface 247 1 0
/*     */     //   755: astore 14
/*     */     //   757: aload 14
/*     */     //   759: ldc -3
/*     */     //   761: invokeinterface 255 2 0
/*     */     //   766: pop
/*     */     //   767: aload 14
/*     */     //   769: ldc_w 261
/*     */     //   772: invokeinterface 255 2 0
/*     */     //   777: pop
/*     */     //   778: aload 14
/*     */     //   780: ldc_w 263
/*     */     //   783: invokeinterface 255 2 0
/*     */     //   788: pop
/*     */     //   789: aload 14
/*     */     //   791: ldc_w 265
/*     */     //   794: invokeinterface 255 2 0
/*     */     //   799: pop
/*     */     //   800: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   803: ldc_w 267
/*     */     //   806: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   809: aload 11
/*     */     //   811: invokevirtual 206	java/util/ArrayList:iterator	()Ljava/util/Iterator;
/*     */     //   814: astore 16
/*     */     //   816: goto +250 -> 1066
/*     */     //   819: aload 16
/*     */     //   821: invokeinterface 210 1 0
/*     */     //   826: checkcast 216	bdbcomp/Cluster
/*     */     //   829: astore 15
/*     */     //   831: aload 15
/*     */     //   833: invokevirtual 218	bdbcomp/Cluster:setFrequentName	()V
/*     */     //   836: aload 15
/*     */     //   838: invokevirtual 269	bdbcomp/Cluster:getRepresentativeName	()Ljava/lang/String;
/*     */     //   841: astore 17
/*     */     //   843: aload 15
/*     */     //   845: invokevirtual 272	bdbcomp/Cluster:getArticles	()Ljava/util/ArrayList;
/*     */     //   848: invokevirtual 206	java/util/ArrayList:iterator	()Ljava/util/Iterator;
/*     */     //   851: astore 19
/*     */     //   853: goto +131 -> 984
/*     */     //   856: aload 19
/*     */     //   858: invokeinterface 210 1 0
/*     */     //   863: checkcast 48	bdbcomp/Artigo
/*     */     //   866: astore 18
/*     */     //   868: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   871: new 90	java/lang/StringBuilder
/*     */     //   874: dup
/*     */     //   875: ldc_w 275
/*     */     //   878: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   881: aload 18
/*     */     //   883: invokevirtual 277	bdbcomp/Artigo:getOriginalAutor	()Ljava/lang/String;
/*     */     //   886: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   889: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   892: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   895: new 90	java/lang/StringBuilder
/*     */     //   898: dup
/*     */     //   899: ldc_w 283
/*     */     //   902: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   905: aload 18
/*     */     //   907: invokevirtual 285	bdbcomp/Artigo:getNumClasse	()I
/*     */     //   910: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   913: ldc_w 288
/*     */     //   916: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   919: aload 18
/*     */     //   921: invokevirtual 290	bdbcomp/Artigo:getNumArtClasse	()I
/*     */     //   924: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   927: ldc_w 288
/*     */     //   930: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   933: aload 15
/*     */     //   935: invokevirtual 293	bdbcomp/Cluster:getNumber	()I
/*     */     //   938: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   941: ldc_w 288
/*     */     //   944: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   947: aload 18
/*     */     //   949: invokevirtual 290	bdbcomp/Artigo:getNumArtClasse	()I
/*     */     //   952: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   955: ldc_w 296
/*     */     //   958: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   961: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   964: astore 20
/*     */     //   966: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   969: aload 20
/*     */     //   971: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   974: aload 14
/*     */     //   976: aload 20
/*     */     //   978: invokeinterface 298 2 0
/*     */     //   983: pop
/*     */     //   984: aload 19
/*     */     //   986: invokeinterface 221 1 0
/*     */     //   991: ifne -135 -> 856
/*     */     //   994: new 90	java/lang/StringBuilder
/*     */     //   997: dup
/*     */     //   998: ldc_w 302
/*     */     //   1001: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   1004: aload 15
/*     */     //   1006: invokevirtual 293	bdbcomp/Cluster:getNumber	()I
/*     */     //   1009: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   1012: ldc_w 304
/*     */     //   1015: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   1018: aload 17
/*     */     //   1020: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   1023: ldc_w 306
/*     */     //   1026: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   1029: aload 15
/*     */     //   1031: invokevirtual 293	bdbcomp/Cluster:getNumber	()I
/*     */     //   1034: invokevirtual 96	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   1037: ldc_w 308
/*     */     //   1040: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   1043: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   1046: astore 18
/*     */     //   1048: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   1051: aload 18
/*     */     //   1053: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   1056: aload 14
/*     */     //   1058: aload 18
/*     */     //   1060: invokeinterface 298 2 0
/*     */     //   1065: pop
/*     */     //   1066: aload 16
/*     */     //   1068: invokeinterface 221 1 0
/*     */     //   1073: ifne -254 -> 819
/*     */     //   1076: aload 12
/*     */     //   1078: invokevirtual 310	java/util/HashSet:iterator	()Ljava/util/Iterator;
/*     */     //   1081: astore 15
/*     */     //   1083: goto +39 -> 1122
/*     */     //   1086: aload 15
/*     */     //   1088: invokeinterface 210 1 0
/*     */     //   1093: checkcast 60	java/lang/String
/*     */     //   1096: astore 16
/*     */     //   1098: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   1101: new 90	java/lang/StringBuilder
/*     */     //   1104: dup
/*     */     //   1105: ldc_w 311
/*     */     //   1108: invokespecial 94	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   1111: aload 16
/*     */     //   1113: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   1116: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   1119: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   1122: aload 15
/*     */     //   1124: invokeinterface 221 1 0
/*     */     //   1129: ifne -43 -> 1086
/*     */     //   1132: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   1135: ldc_w 313
/*     */     //   1138: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   1141: goto +97 -> 1238
/*     */     //   1144: astore 14
/*     */     //   1146: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   1149: ldc_w 315
/*     */     //   1152: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   1155: aload 14
/*     */     //   1157: invokevirtual 317	java/sql/SQLException:printStackTrace	()V
/*     */     //   1160: aload 13
/*     */     //   1162: invokeinterface 322 1 0
/*     */     //   1167: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   1170: ldc_w 325
/*     */     //   1173: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   1176: goto +97 -> 1273
/*     */     //   1179: astore 22
/*     */     //   1181: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   1184: ldc_w 327
/*     */     //   1187: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   1190: aload 22
/*     */     //   1192: invokevirtual 317	java/sql/SQLException:printStackTrace	()V
/*     */     //   1195: goto +78 -> 1273
/*     */     //   1198: astore 21
/*     */     //   1200: aload 13
/*     */     //   1202: invokeinterface 322 1 0
/*     */     //   1207: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   1210: ldc_w 325
/*     */     //   1213: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   1216: goto +19 -> 1235
/*     */     //   1219: astore 22
/*     */     //   1221: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   1224: ldc_w 327
/*     */     //   1227: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   1230: aload 22
/*     */     //   1232: invokevirtual 317	java/sql/SQLException:printStackTrace	()V
/*     */     //   1235: aload 21
/*     */     //   1237: athrow
/*     */     //   1238: aload 13
/*     */     //   1240: invokeinterface 322 1 0
/*     */     //   1245: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   1248: ldc_w 325
/*     */     //   1251: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   1254: goto +19 -> 1273
/*     */     //   1257: astore 22
/*     */     //   1259: getstatic 72	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   1262: ldc_w 327
/*     */     //   1265: invokevirtual 80	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   1268: aload 22
/*     */     //   1270: invokevirtual 317	java/sql/SQLException:printStackTrace	()V
/*     */     //   1273: return
/*     */     // Line number table:
/*     */     //   Java source line #20	-> byte code offset #0
/*     */     //   Java source line #21	-> byte code offset #9
/*     */     //   Java source line #25	-> byte code offset #18
/*     */     //   Java source line #26	-> byte code offset #27
/*     */     //   Java source line #28	-> byte code offset #32
/*     */     //   Java source line #32	-> byte code offset #45
/*     */     //   Java source line #33	-> byte code offset #48
/*     */     //   Java source line #34	-> byte code offset #60
/*     */     //   Java source line #35	-> byte code offset #72
/*     */     //   Java source line #37	-> byte code offset #80
/*     */     //   Java source line #39	-> byte code offset #92
/*     */     //   Java source line #40	-> byte code offset #110
/*     */     //   Java source line #42	-> byte code offset #121
/*     */     //   Java source line #32	-> byte code offset #129
/*     */     //   Java source line #49	-> byte code offset #137
/*     */     //   Java source line #51	-> byte code offset #146
/*     */     //   Java source line #52	-> byte code offset #154
/*     */     //   Java source line #53	-> byte code offset #163
/*     */     //   Java source line #55	-> byte code offset #189
/*     */     //   Java source line #56	-> byte code offset #197
/*     */     //   Java source line #57	-> byte code offset #206
/*     */     //   Java source line #59	-> byte code offset #232
/*     */     //   Java source line #60	-> byte code offset #235
/*     */     //   Java source line #61	-> byte code offset #240
/*     */     //   Java source line #62	-> byte code offset #250
/*     */     //   Java source line #63	-> byte code offset #276
/*     */     //   Java source line #65	-> byte code offset #281
/*     */     //   Java source line #67	-> byte code offset #307
/*     */     //   Java source line #68	-> byte code offset #315
/*     */     //   Java source line #69	-> byte code offset #324
/*     */     //   Java source line #71	-> byte code offset #350
/*     */     //   Java source line #72	-> byte code offset #353
/*     */     //   Java source line #73	-> byte code offset #358
/*     */     //   Java source line #74	-> byte code offset #367
/*     */     //   Java source line #75	-> byte code offset #375
/*     */     //   Java source line #76	-> byte code offset #383
/*     */     //   Java source line #77	-> byte code offset #391
/*     */     //   Java source line #78	-> byte code offset #399
/*     */     //   Java source line #79	-> byte code offset #407
/*     */     //   Java source line #80	-> byte code offset #415
/*     */     //   Java source line #81	-> byte code offset #423
/*     */     //   Java source line #82	-> byte code offset #431
/*     */     //   Java source line #83	-> byte code offset #439
/*     */     //   Java source line #84	-> byte code offset #447
/*     */     //   Java source line #85	-> byte code offset #455
/*     */     //   Java source line #86	-> byte code offset #463
/*     */     //   Java source line #87	-> byte code offset #471
/*     */     //   Java source line #88	-> byte code offset #479
/*     */     //   Java source line #89	-> byte code offset #487
/*     */     //   Java source line #90	-> byte code offset #495
/*     */     //   Java source line #91	-> byte code offset #503
/*     */     //   Java source line #92	-> byte code offset #511
/*     */     //   Java source line #93	-> byte code offset #519
/*     */     //   Java source line #94	-> byte code offset #527
/*     */     //   Java source line #95	-> byte code offset #535
/*     */     //   Java source line #96	-> byte code offset #543
/*     */     //   Java source line #97	-> byte code offset #551
/*     */     //   Java source line #98	-> byte code offset #559
/*     */     //   Java source line #99	-> byte code offset #568
/*     */     //   Java source line #101	-> byte code offset #594
/*     */     //   Java source line #102	-> byte code offset #597
/*     */     //   Java source line #103	-> byte code offset #602
/*     */     //   Java source line #104	-> byte code offset #608
/*     */     //   Java source line #106	-> byte code offset #634
/*     */     //   Java source line #107	-> byte code offset #637
/*     */     //   Java source line #108	-> byte code offset #642
/*     */     //   Java source line #109	-> byte code offset #652
/*     */     //   Java source line #112	-> byte code offset #678
/*     */     //   Java source line #114	-> byte code offset #700
/*     */     //   Java source line #112	-> byte code offset #705
/*     */     //   Java source line #117	-> byte code offset #715
/*     */     //   Java source line #119	-> byte code offset #718
/*     */     //   Java source line #120	-> byte code offset #721
/*     */     //   Java source line #119	-> byte code offset #727
/*     */     //   Java source line #121	-> byte code offset #732
/*     */     //   Java source line #122	-> byte code offset #740
/*     */     //   Java source line #123	-> byte code offset #748
/*     */     //   Java source line #124	-> byte code offset #757
/*     */     //   Java source line #125	-> byte code offset #767
/*     */     //   Java source line #130	-> byte code offset #778
/*     */     //   Java source line #131	-> byte code offset #789
/*     */     //   Java source line #139	-> byte code offset #800
/*     */     //   Java source line #140	-> byte code offset #809
/*     */     //   Java source line #141	-> byte code offset #831
/*     */     //   Java source line #142	-> byte code offset #836
/*     */     //   Java source line #143	-> byte code offset #843
/*     */     //   Java source line #144	-> byte code offset #868
/*     */     //   Java source line #154	-> byte code offset #895
/*     */     //   Java source line #155	-> byte code offset #905
/*     */     //   Java source line #156	-> byte code offset #927
/*     */     //   Java source line #157	-> byte code offset #947
/*     */     //   Java source line #154	-> byte code offset #961
/*     */     //   Java source line #158	-> byte code offset #966
/*     */     //   Java source line #159	-> byte code offset #974
/*     */     //   Java source line #143	-> byte code offset #984
/*     */     //   Java source line #162	-> byte code offset #994
/*     */     //   Java source line #163	-> byte code offset #1004
/*     */     //   Java source line #164	-> byte code offset #1029
/*     */     //   Java source line #162	-> byte code offset #1043
/*     */     //   Java source line #165	-> byte code offset #1048
/*     */     //   Java source line #166	-> byte code offset #1056
/*     */     //   Java source line #140	-> byte code offset #1066
/*     */     //   Java source line #169	-> byte code offset #1076
/*     */     //   Java source line #170	-> byte code offset #1086
/*     */     //   Java source line #171	-> byte code offset #1098
/*     */     //   Java source line #169	-> byte code offset #1122
/*     */     //   Java source line #174	-> byte code offset #1132
/*     */     //   Java source line #184	-> byte code offset #1144
/*     */     //   Java source line #185	-> byte code offset #1146
/*     */     //   Java source line #186	-> byte code offset #1155
/*     */     //   Java source line #189	-> byte code offset #1160
/*     */     //   Java source line #190	-> byte code offset #1167
/*     */     //   Java source line #191	-> byte code offset #1179
/*     */     //   Java source line #192	-> byte code offset #1181
/*     */     //   Java source line #193	-> byte code offset #1190
/*     */     //   Java source line #187	-> byte code offset #1198
/*     */     //   Java source line #189	-> byte code offset #1200
/*     */     //   Java source line #190	-> byte code offset #1207
/*     */     //   Java source line #191	-> byte code offset #1219
/*     */     //   Java source line #192	-> byte code offset #1221
/*     */     //   Java source line #193	-> byte code offset #1230
/*     */     //   Java source line #195	-> byte code offset #1235
/*     */     //   Java source line #189	-> byte code offset #1238
/*     */     //   Java source line #190	-> byte code offset #1245
/*     */     //   Java source line #191	-> byte code offset #1257
/*     */     //   Java source line #192	-> byte code offset #1259
/*     */     //   Java source line #193	-> byte code offset #1268
/*     */     //   Java source line #197	-> byte code offset #1273
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	1274	0	base	Base
/*     */     //   0	1274	1	simTitle	double
/*     */     //   0	1274	3	simVenue	double
/*     */     //   7	150	5	lstNomeLongo	ArrayList<Artigo>
/*     */     //   16	184	6	lstNomeCurto	ArrayList<Artigo>
/*     */     //   25	24	7	random	java.util.Random
/*     */     //   30	618	8	limComp	double
/*     */     //   43	87	10	lstArtigos	ArrayList<Artigo>
/*     */     //   58	17	11	v	int
/*     */     //   144	666	11	clusters	ArrayList<Cluster>
/*     */     //   70	54	12	a	Artigo
/*     */     //   365	712	12	hNomesInComuns	HashSet<String>
/*     */     //   90	10	13	nome	String[]
/*     */     //   698	3	13	c	Cluster
/*     */     //   716	523	13	conn	java.sql.Connection
/*     */     //   683	23	14	localIterator1	Iterator
/*     */     //   755	302	14	stm	java.sql.Statement
/*     */     //   1144	12	14	e	java.sql.SQLException
/*     */     //   829	201	15	cluster	Cluster
/*     */     //   1081	42	15	iT	Iterator
/*     */     //   814	253	16	localIterator2	Iterator
/*     */     //   1096	16	16	nome	String
/*     */     //   841	178	17	authorName	String
/*     */     //   866	82	18	a	Artigo
/*     */     //   1046	13	18	comando	String
/*     */     //   851	134	19	localIterator3	Iterator
/*     */     //   964	13	20	cmd	String
/*     */     //   1198	38	21	localObject	Object
/*     */     //   1179	12	22	erro	java.sql.SQLException
/*     */     //   1219	12	22	erro	java.sql.SQLException
/*     */     //   1257	12	22	erro	java.sql.SQLException
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   718	1141	1144	java/sql/SQLException
/*     */     //   1160	1176	1179	java/sql/SQLException
/*     */     //   718	1160	1198	finally
/*     */     //   1200	1216	1219	java/sql/SQLException
/*     */     //   1238	1254	1257	java/sql/SQLException
/*     */   }
/*     */   
/*     */   private static void criaClustersIniciais(ArrayList<Cluster> clusters, ArrayList<Artigo> lstNome, double limComp)
/*     */     throws Exception
/*     */   {
/* 202 */     for (Artigo a : lstNome)
/*     */     {
/*     */ 
/* 205 */       boolean achou = false;
/* 206 */       int pos = 0;
/*     */       
/*     */ 
/* 209 */       while ((pos < clusters.size()) && (!achou))
/*     */       {
/* 211 */         if ((a.getCoautores() != null) && 
/* 212 */           (((Cluster)clusters.get(pos)).getCoauthors().size() > 0) && 
/* 213 */           (a.getCoautores().length > 0))
/*     */         {
/*     */ 
/* 216 */           if (!possuiAuthorshipRecordDoMesmoArtigo((Cluster)clusters.get(pos), 
/* 217 */             a))
/*     */           {
/* 219 */             if (Similarity.ComparacaoFragmentos(((Cluster)clusters.get(pos))
/* 220 */               .getRepresentativeName(), a.getAutor(), 
/* 221 */               limComp))
/*     */             {
/*     */ 
/*     */ 
/* 225 */               if (Similarity.ComparacaoFragmentosCoautorGrupo(
/* 226 */                 (Cluster)clusters.get(pos), a, limComp, 1))
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 235 */                 ((Cluster)clusters.get(pos)).add(a);
/* 236 */                 achou = true;
/* 237 */                 System.out.println("agrupou " + 
/* 238 */                   (String)((Cluster)clusters.get(pos)).getAuthors().get(0) + 
/* 239 */                   " com " + a.getAutor());
/*     */               }
/*     */             }
/*     */           }
/*     */         }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 261 */         pos++;
/*     */       }
/*     */       
/* 264 */       if (!achou)
/*     */       {
/* 266 */         Cluster c = new Cluster(0);
/* 267 */         c.add(a);
/* 268 */         clusters.add(c);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void fazFusaoClusterCoauthor(ArrayList<Cluster> clusters, double percCoauthor, double p_lim)
/*     */     throws Exception
/*     */   {
/* 283 */     boolean houveFusao = true;
/*     */     int i;
/* 285 */     for (; houveFusao; 
/*     */         
/*     */ 
/* 288 */         i < clusters.size() - 1)
/*     */     {
/* 286 */       System.out.println(".");
/* 287 */       houveFusao = false;
/* 288 */       i = 0; continue;
/* 289 */       Cluster c1 = (Cluster)clusters.get(i);
/* 290 */       int j = i + 1;
/* 291 */       while (j < clusters.size()) {
/* 292 */         Cluster c2 = (Cluster)clusters.get(j);
/*     */         
/* 294 */         if (!possuiAuthorshipRecordDoMesmoArtigo(c1, c2)) {
/* 295 */           if (Similarity.ComparacaoFragmentos(c1
/* 296 */             .getRepresentativeName(), c2
/* 297 */             .getRepresentativeName(), p_lim))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 303 */             int qtdeCoauthors = 0;
/*     */             
/* 305 */             if (c2.getCoauthors().size() > c1.getCoauthors().size()) {
/* 306 */               qtdeCoauthors = (int)Math.ceil(c1
/* 307 */                 .getCoauthors().size() * 
/* 308 */                 percCoauthor);
/*     */             } else {
/* 310 */               qtdeCoauthors = (int)Math.ceil(c2
/* 311 */                 .getCoauthors().size() * 
/* 312 */                 percCoauthor);
/*     */             }
/*     */             
/*     */ 
/* 316 */             int qtdeCompativeis = 0;
/* 317 */             Iterator localIterator2; for (Iterator localIterator1 = c1.getCoauthors().iterator(); localIterator1.hasNext(); 
/* 318 */                 localIterator2.hasNext())
/*     */             {
/* 317 */               String co1 = (String)localIterator1.next();
/* 318 */               localIterator2 = c2.getCoauthors().iterator(); continue;String co2 = (String)localIterator2.next();
/* 319 */               if (Similarity.ComparacaoFragmentos(co1, 
/* 320 */                 co2, p_lim)) {
/* 321 */                 qtdeCompativeis++;
/*     */               }
/*     */             }
/*     */             
/* 325 */             if ((qtdeCompativeis >= qtdeCoauthors) && 
/* 326 */               (qtdeCompativeis > 1))
/*     */             {
/* 328 */               System.out.println("Fusao por causa dos coautores: agrupou " + 
/* 329 */                 c1.getRepresentativeName() + 
/* 330 */                 " com " + 
/* 331 */                 c2.getRepresentativeName());
/* 332 */               c1.fusao(c2);
/* 333 */               clusters.remove(j);
/* 334 */               houveFusao = true;
/*     */             } else {
/* 336 */               j++;
/*     */             }
/* 338 */           } else { j++;
/*     */           }
/* 340 */         } else j++;
/*     */       }
/* 288 */       i++;
/*     */     }
/*     */   }
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
/*     */   static void fusaoGrupos(ArrayList clusters, double p_lim_compara, double p_lim_sim_tit, double p_lim_sim_local)
/*     */     throws Exception
/*     */   {
/* 349 */     boolean v_teve_grupo_unido = true;
/* 350 */     int v_loop = 0;
/* 351 */     int c_grupo; for (; v_teve_grupo_unido; 
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 356 */         c_grupo < clusters.size() - 1)
/*     */     {
/* 352 */       System.out.print(".");
/* 353 */       v_loop++;
/* 354 */       v_teve_grupo_unido = false;
/*     */       
/* 356 */       c_grupo = 0; continue;
/* 357 */       Cluster v_g1 = (Cluster)clusters.get(c_grupo);
/*     */       
/* 359 */       String v_autor1 = v_g1.getRepresentativeName();
/*     */       
/* 361 */       for (int c_grupo2 = c_grupo + 1; c_grupo2 < clusters.size(); c_grupo2++) {
/* 362 */         Cluster v_g2 = (Cluster)clusters.get(c_grupo2);
/*     */         
/*     */ 
/* 365 */         String v_autor2 = v_g2.getRepresentativeName();
/*     */         
/* 367 */         if ((!possuiAuthorshipRecordDoMesmoArtigo(v_g1, v_g2)) && 
/* 368 */           (Similarity.ComparacaoFragmentos(v_autor1, v_autor2, 
/* 369 */           p_lim_compara)))
/*     */         {
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
/*     */ 
/*     */ 
/* 384 */           if ((Similarity.cosineDistance(Util.toArray(v_g1.getTitle()), Util.toArray(v_g2.getTitle())) > p_lim_sim_tit) || 
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 390 */             (Similarity.cosineDistance(Util.toArray(v_g1.getVenue()), Util.toArray(v_g2.getVenue())) > p_lim_sim_local))
/*     */           {
/*     */ 
/*     */ 
/* 394 */             System.out.println("\nFusao: " + 
/* 395 */               ((Artigo)v_g2.getArticles().get(0))
/* 396 */               .toStringArqDen() + 
/* 397 */               "\n\tEm: " + 
/* 398 */               ((Artigo)v_g1.getArticles().get(0))
/* 399 */               .toStringArqDen());
/* 400 */             for (int c_entradaGrupo = 0; 
/* 401 */                 c_entradaGrupo < v_g2.getArticles().size(); c_entradaGrupo++) {
/* 402 */               v_g1.add((Artigo)v_g2.getArticles().get(
/* 403 */                 c_entradaGrupo));
/*     */             }
/*     */             
/* 406 */             v_teve_grupo_unido = true;
/* 407 */             clusters.remove(c_grupo2);
/* 408 */             c_grupo2--;
/*     */           }
/*     */         }
/*     */       }
/* 356 */       c_grupo++;
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
/*     */ 
/*     */ 
/* 415 */     v_teve_grupo_unido = true;
/* 416 */     System.out.println();
/*     */   }
/*     */   
/*     */   public static boolean possuiAuthorshipRecordDoMesmoArtigo(Cluster c, Artigo a)
/*     */   {
/* 421 */     boolean achou = false;
/* 422 */     int i = 0;
/* 423 */     while ((!achou) && (i < c.getArticles().size())) {
/* 424 */       if (((Artigo)c.getArticles().get(i)).getNumClasse() == a.getNumClasse())
/* 425 */         achou = true;
/* 426 */       i++;
/*     */     }
/* 428 */     return achou;
/*     */   }
/*     */   
/*     */   public static boolean possuiAuthorshipRecordDoMesmoArtigo(Cluster c1, Cluster c2)
/*     */   {
/* 433 */     boolean achou = false;
/* 434 */     int i = 0;
/* 435 */     while ((!achou) && (i < c1.getArticles().size())) {
/* 436 */       int j = 0;
/* 437 */       while ((!achou) && (j < c2.getArticles().size())) {
/* 438 */         if (((Artigo)c1.getArticles().get(i)).getNumClasse() == 
/* 439 */           ((Artigo)c2.getArticles().get(j)).getNumClasse())
/* 440 */           achou = true;
/* 441 */         j++;
/*     */       }
/* 443 */       i++;
/*     */     }
/* 445 */     return achou;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void alteraParaMaiorClasse(Base b)
/*     */   {
/* 452 */     int maxClasse = 0;
/* 453 */     for (Artigo a : b.getArtigos()) {
/* 454 */       if (a.getNumClasse() > maxClasse)
/* 455 */         maxClasse = a.getNumClasse();
/*     */     }
/* 457 */     ArrayList<Grupo> lstGrupo = b.criaGruposAutomaticos();
/* 458 */     Collections.sort(lstGrupo);
/* 459 */     Object hAtribuida = new Hashtable();
/* 460 */     int novoValor = maxClasse + 1;
/*     */     
/* 462 */     for (int k = 0; k < lstGrupo.size(); k++) {
/* 463 */       Hashtable<String, Integer> h_classe = new Hashtable();
/* 464 */       for (Iterator<Artigo> iArt = ((Grupo)lstGrupo.get(k)).getArtigos()
/* 465 */             .iterator(); 
/* 465 */             iArt.hasNext();) {
/* 466 */         Artigo a = (Artigo)iArt.next();
/* 467 */         int classe = a.getNumClasse();
/* 468 */         if (((Hashtable)hAtribuida).get(classe) == null) {
/* 469 */           if (h_classe.get(classe) != null) {
/* 470 */             Integer valor = Integer.valueOf(((Integer)h_classe.get(classe)).intValue() + 1);
/* 471 */             h_classe.remove(classe);
/* 472 */             h_classe.put(classe, valor);
/*     */           }
/*     */           else
/*     */           {
/* 476 */             h_classe.put(classe, new Integer(1));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 483 */       String maiorClasse = "";
/* 484 */       Integer numOcorrencia = Integer.valueOf(0);
/* 485 */       for (Enumeration<String> eClasses = h_classe.keys(); eClasses
/* 486 */             .hasMoreElements();) {
/* 487 */         String key = (String)eClasses.nextElement();
/*     */         
/* 489 */         if (((Integer)h_classe.get(key)).intValue() > numOcorrencia.intValue()) {
/* 490 */           maiorClasse = key;
/* 491 */           numOcorrencia = (Integer)h_classe.get(key);
/*     */         }
/*     */       }
/* 494 */       if (maiorClasse.equals("")) {
/* 495 */         maiorClasse = novoValor;
/* 496 */         novoValor++;
/*     */       }
/*     */       
/* 499 */       for (Iterator<Artigo> iArt = ((Grupo)lstGrupo.get(k)).getArtigos()
/* 500 */             .iterator(); 
/* 500 */             iArt.hasNext();) {
/* 501 */         Artigo a = (Artigo)iArt.next();
/*     */         
/* 503 */         a.setNumClasseRecebida(Integer.parseInt(maiorClasse));
/*     */       }
/* 505 */       ((Hashtable)hAtribuida).put(maiorClasse, maiorClasse);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void fazFusaoUsandoNomeInComum(ArrayList<Cluster> clusters, HashSet<String> hNomesInComuns, double limComp)
/*     */   {
/* 511 */     for (int i = 0; i < clusters.size() - 1; i++) {
/* 512 */       Cluster c1 = (Cluster)clusters.get(i);
/* 513 */       int j = i + 1;
/* 514 */       while (j < clusters.size()) {
/* 515 */         boolean found = false;
/* 516 */         Cluster c2 = (Cluster)clusters.get(j);
/*     */         
/* 518 */         if (!possuiAuthorshipRecordDoMesmoArtigo(c1, c2))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 526 */           String[] nomes1 = c1.getRepresentativeName()
/* 527 */             .split("[ \\.]");
/* 528 */           String[] nomes2 = c2.getRepresentativeName()
/* 529 */             .split("[ \\.]");
/* 530 */           if ((nomes1[0].equals("altigran")) && 
/* 531 */             (nomes2[0].equals("altigran")))
/* 532 */             System.out.println("Verifica se sÃ£o iguais." + 
/* 533 */               ((Artigo)c1.getArticles().get(0)).getAutor() + "\t" + 
/* 534 */               ((Artigo)c2.getArticles().get(0)).getAutor());
/* 535 */           if ((hNomesInComuns.contains(nomes1[0])) && 
/* 536 */             (nomes1[0].equals(nomes2[0])))
/*     */           {
/* 538 */             System.out.println("Primeiro nome igual: " + 
/* 539 */               ((Artigo)c1.getArticles().get(0)).getAutor() + "\t" + 
/* 540 */               ((Artigo)c2.getArticles().get(0)).getAutor());
/* 541 */             found = false;
/* 542 */             int k = 1;
/* 543 */             while ((!found) && (k < nomes1.length)) {
/* 544 */               int l = 1;
/* 545 */               while ((!found) && (l < nomes2.length)) {
/* 546 */                 if (nomes1[k].equals(nomes2[l]))
/* 547 */                   found = true;
/* 548 */                 l++;
/*     */               }
/* 550 */               k++;
/*     */             }
/* 552 */             if (found)
/*     */             {
/* 554 */               System.out.println("Fusao por causa do nome infrequente: agrupou " + 
/* 555 */                 c1.getRepresentativeName() + 
/* 556 */                 " com " + 
/* 557 */                 c2.getRepresentativeName());
/* 558 */               c1.fusao(c2);
/* 559 */               clusters.remove(j);
/*     */             }
/*     */           }
/*     */         }
/* 563 */         if (!found) {
/* 564 */           j++;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static HashSet<String> obterNomesMenosFrequente(Base base, double perc)
/*     */   {
/* 573 */     HashSet<String> hNomes = new HashSet();
/* 574 */     Hashtable<String, Integer> hash = new Hashtable();
/* 575 */     String nome; for (Artigo a : base.getArtigos()) {
/* 576 */       nome = a.getAutor();
/* 577 */       String[] nomes = nome.split("[ \\.\\;\\:\\,\\-]");
/*     */       
/* 579 */       if (nomes.length > 2) { Integer qtde;
/* 580 */         if ((qtde = (Integer)hash.get(nomes[0])) != null) {
/* 581 */           hash.put(nomes[0], new Integer(qtde.intValue() + 1));
/*     */         } else {
/* 583 */           hash.put(nomes[0], new Integer(1));
/*     */         }
/*     */       }
/*     */     }
/* 587 */     ArrayList<Data> list = new ArrayList();
/* 588 */     for (String nome : hash.keySet()) {
/* 589 */       int qtde = ((Integer)hash.get(nome)).intValue();
/* 590 */       list.add(new Data(nome, qtde));
/*     */     }
/* 592 */     Collections.sort(list);
/* 593 */     int numSelected = (int)Math.ceil(list.size() * perc);
/* 594 */     for (int i = list.size() - numSelected; i < list.size(); i++) {
/* 595 */       System.out.println("Nome infrequente: " + ((Data)list.get(i)).getValue() + 
/* 596 */         "\t" + ((Data)list.get(i)).getFrequence());
/* 597 */       hNomes.add(((Data)list.get(i)).getValue());
/*     */     }
/* 599 */     for (Iterator iT = hNomes.iterator(); iT.hasNext();) {
/* 600 */       String nome = (String)iT.next();
/* 601 */       System.out.println("Nome infrequente: " + nome);
/*     */     }
/* 603 */     return hNomes;
/*     */   }
/*     */   
/*     */   public static void fazFusaoUsandoQtdeSobreNomeMaiorQue(ArrayList<Cluster> clusters, int qtde)
/*     */   {
/* 608 */     for (int i = 0; i < clusters.size() - 1; i++) {
/* 609 */       Cluster c1 = (Cluster)clusters.get(i);
/* 610 */       int j = i + 1;
/* 611 */       while (j < clusters.size()) {
/* 612 */         int found = 0;
/* 613 */         Cluster c2 = (Cluster)clusters.get(j);
/*     */         
/* 615 */         if (!possuiAuthorshipRecordDoMesmoArtigo(c1, c2))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 623 */           String[] nomes1 = c1.getRepresentativeName()
/* 624 */             .split("[ \\.]");
/* 625 */           String[] nomes2 = c2.getRepresentativeName()
/* 626 */             .split("[ \\.]");
/* 627 */           if (nomes1.length > nomes2.length)
/*     */           {
/* 629 */             String[] temp = nomes1;
/* 630 */             nomes1 = nomes2;
/* 631 */             nomes2 = temp;
/*     */           }
/*     */           
/* 634 */           if (nomes1[0].equals(nomes2[0]))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 639 */             found = 0;
/* 640 */             int k = 1;
/* 641 */             while (k < nomes1.length) {
/* 642 */               int l = 1;
/*     */               
/* 644 */               while (l < nomes2.length) {
/* 645 */                 if (nomes1[k].equals(nomes2[l]))
/* 646 */                   found++;
/* 647 */                 l++;
/*     */               }
/* 649 */               k++;
/*     */             }
/* 651 */             if (found >= qtde)
/*     */             {
/* 653 */               System.out.println("Fusao por causa da qtde de coauthores em comum: agrupou " + 
/* 654 */                 c1.getRepresentativeName() + 
/* 655 */                 " com " + 
/* 656 */                 c2.getRepresentativeName());
/* 657 */               c1.fusao(c2);
/* 658 */               clusters.remove(j);
/*     */             } else {
/* 660 */               j++;
/*     */             }
/* 662 */           } else { j++;
/*     */           }
/* 664 */         } else { j++;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\HHC.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */