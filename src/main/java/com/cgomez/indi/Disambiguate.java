/*     */ package com.cgomez.indi;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.CharacterCodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class Disambiguate
/*     */ {
/*  14 */   static String user = "root";
/*     */   
/*  16 */   static String password = "SENHA";
/*     */   
/*  18 */   static String database = "jdbc:mysql://localhost/bdbcomp2";
/*     */   
/*     */   /* Error */
/*     */   public static ArrayList<Artigo> createAuthorshipRecords()
/*     */     throws Exception
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 38	java/util/ArrayList
/*     */     //   3: dup
/*     */     //   4: invokespecial 40	java/util/ArrayList:<init>	()V
/*     */     //   7: astore_0
/*     */     //   8: aconst_null
/*     */     //   9: astore_1
/*     */     //   10: iconst_0
/*     */     //   11: istore_2
/*     */     //   12: ldc 41
/*     */     //   14: invokestatic 43	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   17: pop
/*     */     //   18: getstatic 22	bdbcomp/Disambiguate:database	Ljava/lang/String;
/*     */     //   21: getstatic 14	bdbcomp/Disambiguate:user	Ljava/lang/String;
/*     */     //   24: getstatic 18	bdbcomp/Disambiguate:password	Ljava/lang/String;
/*     */     //   27: invokestatic 49	java/sql/DriverManager:getConnection	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/sql/Connection;
/*     */     //   30: astore_1
/*     */     //   31: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   34: ldc 61
/*     */     //   36: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   39: aload_1
/*     */     //   40: sipush 1004
/*     */     //   43: sipush 1007
/*     */     //   46: invokeinterface 69 3 0
/*     */     //   51: astore_3
/*     */     //   52: aload_3
/*     */     //   53: ldc 75
/*     */     //   55: invokeinterface 77 2 0
/*     */     //   60: astore 4
/*     */     //   62: invokestatic 83	bdbcomp/StopList:getInstance	()Lbdbcomp/StopList;
/*     */     //   65: astore 5
/*     */     //   67: iconst_0
/*     */     //   68: istore 6
/*     */     //   70: goto +380 -> 450
/*     */     //   73: iinc 6 1
/*     */     //   76: aload 4
/*     */     //   78: ldc 89
/*     */     //   80: invokeinterface 91 2 0
/*     */     //   85: istore 7
/*     */     //   87: aload 5
/*     */     //   89: aload 4
/*     */     //   91: ldc 97
/*     */     //   93: invokeinterface 99 2 0
/*     */     //   98: invokestatic 103	bdbcomp/Disambiguate:changeHTMLCodeToASC	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   101: invokevirtual 106	bdbcomp/StopList:removeStopWord	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   104: invokestatic 109	bdbcomp/Disambiguate:stem	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   107: astore 8
/*     */     //   109: aload 5
/*     */     //   111: aload 4
/*     */     //   113: ldc 112
/*     */     //   115: invokeinterface 99 2 0
/*     */     //   120: invokestatic 103	bdbcomp/Disambiguate:changeHTMLCodeToASC	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   123: invokevirtual 106	bdbcomp/StopList:removeStopWord	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   126: invokestatic 109	bdbcomp/Disambiguate:stem	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   129: astore 9
/*     */     //   131: aload_1
/*     */     //   132: sipush 1004
/*     */     //   135: sipush 1007
/*     */     //   138: invokeinterface 69 3 0
/*     */     //   143: astore 10
/*     */     //   145: aload 10
/*     */     //   147: new 114	java/lang/StringBuilder
/*     */     //   150: dup
/*     */     //   151: ldc 116
/*     */     //   153: invokespecial 118	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   156: iload 7
/*     */     //   158: invokevirtual 120	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   161: ldc 124
/*     */     //   163: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   166: invokevirtual 129	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   169: invokeinterface 77 2 0
/*     */     //   174: astore 11
/*     */     //   176: iconst_0
/*     */     //   177: istore 12
/*     */     //   179: goto +261 -> 440
/*     */     //   182: aload 11
/*     */     //   184: ldc -123
/*     */     //   186: invokeinterface 91 2 0
/*     */     //   191: istore 13
/*     */     //   193: aload 11
/*     */     //   195: ldc -121
/*     */     //   197: invokeinterface 99 2 0
/*     */     //   202: invokevirtual 137	java/lang/String:trim	()Ljava/lang/String;
/*     */     //   205: astore 14
/*     */     //   207: aload_1
/*     */     //   208: sipush 1004
/*     */     //   211: sipush 1007
/*     */     //   214: invokeinterface 69 3 0
/*     */     //   219: astore 15
/*     */     //   221: aload 15
/*     */     //   223: new 114	java/lang/StringBuilder
/*     */     //   226: dup
/*     */     //   227: ldc -114
/*     */     //   229: invokespecial 118	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   232: iload 7
/*     */     //   234: invokevirtual 120	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   237: ldc -112
/*     */     //   239: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   242: iload 13
/*     */     //   244: invokevirtual 120	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   247: ldc 124
/*     */     //   249: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   252: invokevirtual 129	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   255: invokeinterface 77 2 0
/*     */     //   260: astore 16
/*     */     //   262: new 38	java/util/ArrayList
/*     */     //   265: dup
/*     */     //   266: invokespecial 40	java/util/ArrayList:<init>	()V
/*     */     //   269: astore 17
/*     */     //   271: goto +69 -> 340
/*     */     //   274: aload 16
/*     */     //   276: ldc -121
/*     */     //   278: invokeinterface 99 2 0
/*     */     //   283: invokevirtual 137	java/lang/String:trim	()Ljava/lang/String;
/*     */     //   286: astore 18
/*     */     //   288: aload 18
/*     */     //   290: invokevirtual 146	java/lang/String:length	()I
/*     */     //   293: ifle +14 -> 307
/*     */     //   296: aload 17
/*     */     //   298: aload 18
/*     */     //   300: invokevirtual 150	java/util/ArrayList:add	(Ljava/lang/Object;)Z
/*     */     //   303: pop
/*     */     //   304: goto +36 -> 340
/*     */     //   307: getstatic 154	java/lang/System:err	Ljava/io/PrintStream;
/*     */     //   310: new 114	java/lang/StringBuilder
/*     */     //   313: dup
/*     */     //   314: ldc -99
/*     */     //   316: invokespecial 118	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   319: aload 18
/*     */     //   321: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   324: ldc -97
/*     */     //   326: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   329: iload 7
/*     */     //   331: invokevirtual 120	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   334: invokevirtual 129	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   337: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   340: aload 16
/*     */     //   342: invokeinterface 161 1 0
/*     */     //   347: ifne -73 -> 274
/*     */     //   350: aconst_null
/*     */     //   351: astore 18
/*     */     //   353: aload 17
/*     */     //   355: invokevirtual 165	java/util/ArrayList:size	()I
/*     */     //   358: ifle +47 -> 405
/*     */     //   361: aload 17
/*     */     //   363: invokevirtual 165	java/util/ArrayList:size	()I
/*     */     //   366: anewarray 138	java/lang/String
/*     */     //   369: astore 18
/*     */     //   371: iconst_0
/*     */     //   372: istore 19
/*     */     //   374: goto +21 -> 395
/*     */     //   377: aload 18
/*     */     //   379: iload 19
/*     */     //   381: aload 17
/*     */     //   383: iload 19
/*     */     //   385: invokevirtual 168	java/util/ArrayList:get	(I)Ljava/lang/Object;
/*     */     //   388: checkcast 138	java/lang/String
/*     */     //   391: aastore
/*     */     //   392: iinc 19 1
/*     */     //   395: iload 19
/*     */     //   397: aload 17
/*     */     //   399: invokevirtual 165	java/util/ArrayList:size	()I
/*     */     //   402: if_icmplt -25 -> 377
/*     */     //   405: iinc 12 1
/*     */     //   408: new 172	bdbcomp/Artigo
/*     */     //   411: dup
/*     */     //   412: iload_2
/*     */     //   413: iinc 2 1
/*     */     //   416: iload 7
/*     */     //   418: iload 13
/*     */     //   420: aload 14
/*     */     //   422: aload 18
/*     */     //   424: aload 8
/*     */     //   426: aload 9
/*     */     //   428: invokespecial 174	bdbcomp/Artigo:<init>	(IIILjava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
/*     */     //   431: astore 19
/*     */     //   433: aload_0
/*     */     //   434: aload 19
/*     */     //   436: invokevirtual 150	java/util/ArrayList:add	(Ljava/lang/Object;)Z
/*     */     //   439: pop
/*     */     //   440: aload 11
/*     */     //   442: invokeinterface 161 1 0
/*     */     //   447: ifne -265 -> 182
/*     */     //   450: aload 4
/*     */     //   452: invokeinterface 161 1 0
/*     */     //   457: ifne -384 -> 73
/*     */     //   460: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   463: new 114	java/lang/StringBuilder
/*     */     //   466: dup
/*     */     //   467: ldc -79
/*     */     //   469: invokespecial 118	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   472: iload 6
/*     */     //   474: invokevirtual 120	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   477: invokevirtual 129	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   480: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   483: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   486: ldc -77
/*     */     //   488: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   491: goto +136 -> 627
/*     */     //   494: astore_3
/*     */     //   495: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   498: ldc -75
/*     */     //   500: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   503: aload_3
/*     */     //   504: invokevirtual 183	java/lang/ClassNotFoundException:printStackTrace	()V
/*     */     //   507: aload_1
/*     */     //   508: invokeinterface 188 1 0
/*     */     //   513: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   516: ldc -65
/*     */     //   518: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   521: goto +138 -> 659
/*     */     //   524: astore 21
/*     */     //   526: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   529: ldc -63
/*     */     //   531: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   534: aload 21
/*     */     //   536: invokevirtual 195	java/sql/SQLException:printStackTrace	()V
/*     */     //   539: goto +120 -> 659
/*     */     //   542: astore_3
/*     */     //   543: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   546: ldc -58
/*     */     //   548: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   551: aload_3
/*     */     //   552: invokevirtual 195	java/sql/SQLException:printStackTrace	()V
/*     */     //   555: aload_1
/*     */     //   556: invokeinterface 188 1 0
/*     */     //   561: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   564: ldc -65
/*     */     //   566: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   569: goto +90 -> 659
/*     */     //   572: astore 21
/*     */     //   574: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   577: ldc -63
/*     */     //   579: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   582: aload 21
/*     */     //   584: invokevirtual 195	java/sql/SQLException:printStackTrace	()V
/*     */     //   587: goto +72 -> 659
/*     */     //   590: astore 20
/*     */     //   592: aload_1
/*     */     //   593: invokeinterface 188 1 0
/*     */     //   598: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   601: ldc -65
/*     */     //   603: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   606: goto +18 -> 624
/*     */     //   609: astore 21
/*     */     //   611: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   614: ldc -63
/*     */     //   616: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   619: aload 21
/*     */     //   621: invokevirtual 195	java/sql/SQLException:printStackTrace	()V
/*     */     //   624: aload 20
/*     */     //   626: athrow
/*     */     //   627: aload_1
/*     */     //   628: invokeinterface 188 1 0
/*     */     //   633: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   636: ldc -65
/*     */     //   638: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   641: goto +18 -> 659
/*     */     //   644: astore 21
/*     */     //   646: getstatic 55	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   649: ldc -63
/*     */     //   651: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   654: aload 21
/*     */     //   656: invokevirtual 195	java/sql/SQLException:printStackTrace	()V
/*     */     //   659: aload_0
/*     */     //   660: areturn
/*     */     // Line number table:
/*     */     //   Java source line #27	-> byte code offset #0
/*     */     //   Java source line #28	-> byte code offset #8
/*     */     //   Java source line #29	-> byte code offset #10
/*     */     //   Java source line #32	-> byte code offset #12
/*     */     //   Java source line #33	-> byte code offset #18
/*     */     //   Java source line #34	-> byte code offset #31
/*     */     //   Java source line #35	-> byte code offset #39
/*     */     //   Java source line #36	-> byte code offset #40
/*     */     //   Java source line #37	-> byte code offset #43
/*     */     //   Java source line #35	-> byte code offset #46
/*     */     //   Java source line #52	-> byte code offset #52
/*     */     //   Java source line #53	-> byte code offset #53
/*     */     //   Java source line #52	-> byte code offset #60
/*     */     //   Java source line #60	-> byte code offset #62
/*     */     //   Java source line #61	-> byte code offset #67
/*     */     //   Java source line #62	-> byte code offset #70
/*     */     //   Java source line #63	-> byte code offset #73
/*     */     //   Java source line #64	-> byte code offset #76
/*     */     //   Java source line #68	-> byte code offset #87
/*     */     //   Java source line #69	-> byte code offset #89
/*     */     //   Java source line #70	-> byte code offset #91
/*     */     //   Java source line #69	-> byte code offset #98
/*     */     //   Java source line #68	-> byte code offset #104
/*     */     //   Java source line #74	-> byte code offset #109
/*     */     //   Java source line #75	-> byte code offset #111
/*     */     //   Java source line #76	-> byte code offset #113
/*     */     //   Java source line #75	-> byte code offset #120
/*     */     //   Java source line #74	-> byte code offset #126
/*     */     //   Java source line #82	-> byte code offset #131
/*     */     //   Java source line #83	-> byte code offset #132
/*     */     //   Java source line #84	-> byte code offset #135
/*     */     //   Java source line #82	-> byte code offset #138
/*     */     //   Java source line #85	-> byte code offset #145
/*     */     //   Java source line #86	-> byte code offset #147
/*     */     //   Java source line #89	-> byte code offset #156
/*     */     //   Java source line #86	-> byte code offset #166
/*     */     //   Java source line #85	-> byte code offset #174
/*     */     //   Java source line #91	-> byte code offset #176
/*     */     //   Java source line #92	-> byte code offset #179
/*     */     //   Java source line #93	-> byte code offset #182
/*     */     //   Java source line #94	-> byte code offset #193
/*     */     //   Java source line #100	-> byte code offset #207
/*     */     //   Java source line #101	-> byte code offset #208
/*     */     //   Java source line #102	-> byte code offset #211
/*     */     //   Java source line #100	-> byte code offset #214
/*     */     //   Java source line #103	-> byte code offset #221
/*     */     //   Java source line #104	-> byte code offset #223
/*     */     //   Java source line #106	-> byte code offset #232
/*     */     //   Java source line #107	-> byte code offset #237
/*     */     //   Java source line #108	-> byte code offset #242
/*     */     //   Java source line #104	-> byte code offset #252
/*     */     //   Java source line #103	-> byte code offset #260
/*     */     //   Java source line #109	-> byte code offset #262
/*     */     //   Java source line #111	-> byte code offset #271
/*     */     //   Java source line #112	-> byte code offset #274
/*     */     //   Java source line #113	-> byte code offset #283
/*     */     //   Java source line #112	-> byte code offset #286
/*     */     //   Java source line #114	-> byte code offset #288
/*     */     //   Java source line #115	-> byte code offset #296
/*     */     //   Java source line #117	-> byte code offset #307
/*     */     //   Java source line #118	-> byte code offset #324
/*     */     //   Java source line #117	-> byte code offset #337
/*     */     //   Java source line #111	-> byte code offset #340
/*     */     //   Java source line #120	-> byte code offset #350
/*     */     //   Java source line #122	-> byte code offset #353
/*     */     //   Java source line #123	-> byte code offset #361
/*     */     //   Java source line #125	-> byte code offset #371
/*     */     //   Java source line #126	-> byte code offset #377
/*     */     //   Java source line #125	-> byte code offset #392
/*     */     //   Java source line #130	-> byte code offset #405
/*     */     //   Java source line #131	-> byte code offset #408
/*     */     //   Java source line #132	-> byte code offset #418
/*     */     //   Java source line #131	-> byte code offset #428
/*     */     //   Java source line #134	-> byte code offset #433
/*     */     //   Java source line #92	-> byte code offset #440
/*     */     //   Java source line #62	-> byte code offset #450
/*     */     //   Java source line #139	-> byte code offset #460
/*     */     //   Java source line #141	-> byte code offset #483
/*     */     //   Java source line #143	-> byte code offset #494
/*     */     //   Java source line #145	-> byte code offset #495
/*     */     //   Java source line #146	-> byte code offset #503
/*     */     //   Java source line #153	-> byte code offset #507
/*     */     //   Java source line #154	-> byte code offset #513
/*     */     //   Java source line #155	-> byte code offset #524
/*     */     //   Java source line #156	-> byte code offset #526
/*     */     //   Java source line #157	-> byte code offset #534
/*     */     //   Java source line #147	-> byte code offset #542
/*     */     //   Java source line #149	-> byte code offset #543
/*     */     //   Java source line #150	-> byte code offset #551
/*     */     //   Java source line #153	-> byte code offset #555
/*     */     //   Java source line #154	-> byte code offset #561
/*     */     //   Java source line #155	-> byte code offset #572
/*     */     //   Java source line #156	-> byte code offset #574
/*     */     //   Java source line #157	-> byte code offset #582
/*     */     //   Java source line #151	-> byte code offset #590
/*     */     //   Java source line #153	-> byte code offset #592
/*     */     //   Java source line #154	-> byte code offset #598
/*     */     //   Java source line #155	-> byte code offset #609
/*     */     //   Java source line #156	-> byte code offset #611
/*     */     //   Java source line #157	-> byte code offset #619
/*     */     //   Java source line #159	-> byte code offset #624
/*     */     //   Java source line #153	-> byte code offset #627
/*     */     //   Java source line #154	-> byte code offset #633
/*     */     //   Java source line #155	-> byte code offset #644
/*     */     //   Java source line #156	-> byte code offset #646
/*     */     //   Java source line #157	-> byte code offset #654
/*     */     //   Java source line #161	-> byte code offset #659
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   7	653	0	listArticles	ArrayList<Artigo>
/*     */     //   9	619	1	conn	java.sql.Connection
/*     */     //   11	402	2	numberOfRecords	int
/*     */     //   51	2	3	stmWork	java.sql.Statement
/*     */     //   494	10	3	e	ClassNotFoundException
/*     */     //   542	10	3	e	java.sql.SQLException
/*     */     //   60	391	4	rsWork	java.sql.ResultSet
/*     */     //   65	45	5	stoplist	StopList
/*     */     //   68	405	6	countWorks	int
/*     */     //   85	332	7	workId	int
/*     */     //   107	318	8	title	String
/*     */     //   129	298	9	venue	String
/*     */     //   143	3	10	stmAuthor	java.sql.Statement
/*     */     //   174	267	11	rsAuthor	java.sql.ResultSet
/*     */     //   177	229	12	numberOfAuthors	int
/*     */     //   191	228	13	authorId	int
/*     */     //   205	216	14	creator	String
/*     */     //   219	3	15	stmCoauthor	java.sql.Statement
/*     */     //   260	81	16	rsCoauthor	java.sql.ResultSet
/*     */     //   269	129	17	listCoauthor	ArrayList<String>
/*     */     //   286	34	18	coauthor	String
/*     */     //   351	72	18	coauthors	String[]
/*     */     //   372	24	19	i	int
/*     */     //   431	4	19	artigo	Artigo
/*     */     //   590	35	20	localObject	Object
/*     */     //   524	11	21	erro	java.sql.SQLException
/*     */     //   572	11	21	erro	java.sql.SQLException
/*     */     //   609	11	21	erro	java.sql.SQLException
/*     */     //   644	11	21	erro	java.sql.SQLException
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   12	491	494	java/lang/ClassNotFoundException
/*     */     //   507	521	524	java/sql/SQLException
/*     */     //   12	491	542	java/sql/SQLException
/*     */     //   555	569	572	java/sql/SQLException
/*     */     //   12	507	590	finally
/*     */     //   542	555	590	finally
/*     */     //   592	606	609	java/sql/SQLException
/*     */     //   627	641	644	java/sql/SQLException
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 171 */     ArrayList<Artigo> listArticles = createAuthorshipRecords();
/* 172 */     System.out.println("Printing the authorship records.");
/* 173 */     for (Artigo a : listArticles) {
/* 174 */       convert(a);
/* 175 */       System.out.println(a.toStringArqDen());
/*     */     }
/* 177 */     System.out.println("Ending of the printing.");
/*     */     
/*     */ 
/* 180 */     Base base = new Base();
/* 181 */     base.getArtigos().addAll(listArticles);
/* 182 */     double simVenue = 0.3D;
/* 183 */     double simTitle = 0.3D;
/* 184 */     System.out.println("Starting disambiguation.");
/* 185 */     HHC.process(base, simTitle, simVenue);
/* 186 */     System.out.println("Ending of the disambiguation.");
/*     */     
/* 188 */     for (Artigo a : listArticles) {
/* 189 */       System.out.println(a.toStringArqDen() + "\t" + 
/* 190 */         a.getNumClasseRecebida());
/*     */     }
/*     */   }
/*     */   
/*     */   public static String convertText(String string) throws CharacterCodingException
/*     */   {
/* 196 */     String result = changeHTMLCodeToASC(string);
/*     */     
/* 198 */     Charset charset = Charset.forName("UTF-8");
/* 199 */     CharsetDecoder decoder = charset.newDecoder();
/* 200 */     CharsetEncoder encoder = charset.newEncoder();
/*     */     
/* 202 */     ByteBuffer bb = encoder.encode(CharBuffer.wrap(string));
/* 203 */     CharBuffer cb = decoder.decode(bb);
/* 204 */     result = cb.toString();
/* 205 */     return result;
/*     */   }
/*     */   
/*     */   public static String changeHTMLCodeToASC(String str) {
/* 209 */     int[] html = { 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 
/* 210 */       203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 
/* 211 */       215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 
/* 212 */       227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 
/* 213 */       239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 
/* 214 */       251, 252, 253, 254, 255, 256 };
/* 215 */     char[] asc = { 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'c', 'e', 'e', 'e', 
/* 216 */       'e', 'i', 'i', 'i', 'i', 'd', 'n', 'o', 'o', 'o', 'o', 'o', 
/* 217 */       'x', '0', 'u', 'u', 'u', 'u', 'y', 'p', 'b', 'a', 'a', 'a', 
/* 218 */       'a', 'a', 'a', 'a', 'c', 'e', 'e', 'e', 'e', 'i', 'i', 'i', 
/* 219 */       'i', 'a', 'n', 'o', 'o', 'o', 'o', 'o', ':', '0', 'u', 'u', 
/* 220 */       'u', 'u', 'y', 'b', 'y', 'a' };
/*     */     
/* 222 */     if (str != null)
/*     */     {
/*     */ 
/* 225 */       for (int i = 0; i < html.length; i++) {
/* 226 */         str = str.replace("&#" + html[i] + ";", asc[i]);
/*     */       }
/* 228 */       str = str.replaceAll("[\\.;,:\\!,\\?\\'\\\"][ ]", " ");
/* 229 */       str = str.replaceAll("[\\.;,:\\!,\\?\\'\\\"]", " ");
/* 230 */       return str.toLowerCase();
/*     */     }
/* 232 */     return str;
/*     */   }
/*     */   
/*     */   public static void convert(Artigo a) throws Exception {
/* 236 */     a.setAutor(changeHTMLCodeToASC(a.getAutor()));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 241 */     if (a.getCoautores() != null) {
/* 242 */       for (int i = 0; i < a.getCoautores().length; i++) {
/* 243 */         a.getCoautores()[i] = changeHTMLCodeToASC(a.getCoautores()[i]);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String stem(String str)
/*     */   {
/* 260 */     if (str != null) {
/* 261 */       Stemmer stemmer = new Stemmer();
/* 262 */       String[] strTemp = str.split("[ ,.:]");
/* 263 */       String strResult = "";
/* 264 */       for (int i = 0; i < strTemp.length; i++) {
/* 265 */         stemmer.add(strTemp[i].toCharArray(), strTemp[i].length());
/* 266 */         stemmer.stem();
/* 267 */         strResult = strResult + stemmer.toString() + " ";
/*     */       }
/* 269 */       return strResult;
/*     */     }
/* 271 */     return str;
/*     */   }
/*     */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Disambiguate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */