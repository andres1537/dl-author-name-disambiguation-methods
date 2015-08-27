//relacao entre a quantidade de determinismo e acuracia
//a condicao eh necessaria ou suficiente para a ocorrencia da classe

//ideia: na parte de scoring, fazer uma caracterizacao previa, para, por exemplo, encontrar uma funcao que mapeia "confianca" a acuracia realmente observada.
//depois usar essa funcao na parte do scoring. exemplos de funcao poderia ser a linear, a log, ou outras dependendo do resultado obtido na caracterizacao.


//trata-se a colecao como uma linguagem que pode ser comprimida por regras de associacao
//trata-se cada classe como uma linguagem, que eh um subconjunto da linguagem que comprime a colecao
//a melhor compressao, embora nao possa ser calculada (kolmogorov complexity), pode ser aproximada (por exemplo, com a dimensao fractal, ou pela ziv-lempel complexity)
//a melhor compressao para cada classe, eh ultizada para realizar a classificacao
//uma linguagem eh similar a outra se as duas sao sobrepostas de alguma forma. o grau de superposicao eh o grau de similaridade.
//nesse caso, qual eh a complexidade de uma linguagem. sera que essa complexidade da uma ideia na dificuldade da classificacao. sabendo a dificuldade, sabemos estimar a complexidade do modelo a ser induzido.

//como encontrar a melhor metrica, ou a melhor funcao de score, para cada instancia de teste? sera confianca, kulc, cosseno ou outra. quais as caracteristicas do teste que servem para escolher a metrica a ser usada

//modelo baseado em regras eh descritivo e nao generativo. modelos generativos tentam construir o que gerou os dados, e entao com esse modelo pode-se fazer simulacoes.

//regras contraditorias. por exemplo, uma regra a->0(100%) e outra b->1(80%). enfim, como remover a regra b->1. acho que da um teorema assim, "para um dado item a, b, c etc. sempre existe uma regra com 100% de confianca". o ponto eh como ser especifico o suficiente para encontrar tais regras.

//uma teoria eh um conjunto infinito de statements (ou regras)

#include <getopt.h>
#include <stdlib.h>
#include <string.h>

#include "rule.h"
#include "itemset.h"
#include "evidence.h"
#include "timer.h"
#include "cache.h"
#include "core.h"
#include "training.h"
#include "limits.h"
#include "score.h"
#include "test.h"
#include "list.h"

using namespace std;

cache_t CACHE_CRITERIA, CACHE_CLASSES;
itemset_t *ITEMSETS;
rule_t RULES[MAX_RULES];
int META_LEARNING=0, RELATIVE=0, CRITERION=CONFIDENCE, N_RULES=0, N_TESTS=0, N_POINTS=0, N_TRANSACTIONS=0, N_ITEMSETS=0, CAPACITY=100000, N_CRITERIA=1, COUNT_TARGET[MAX_CLASSES], COUNT_CRITERION[MAX_CLASSES], TARGET_ID[MAX_CLASSES], CRITERION_ID[MAX_CLASSES], *TID_CLASSES, MIN_SIZE=1, MAX_SIZE=MAX_RULE_SIZE, MIN_RULES=1;
int MIN_COUNT=1, RANKING_SIZE=1000000;
float MIN_CONF=0.50, MIN_SUPP=0.01, FACTOR=2.00;
char *DELIM=" ";

map<string, int> ITEM_MAP;
map<int, string> SYMBOL_TABLE;
map<string, int> CRITERION_NAME;
map<string, int> CLASS_NAME;
map<string, long long> PROOF;
list_t *TEST, *UNLABELED;


int classify(char* training, char* test, char* unlabeled, int mode) {
	__START_TIMER__
	ITEMSETS=(itemset_t*) malloc(10*MB);
	read_training_set(training);
	read_test_set(test);
	if(mode==LAZY_SUPERVISED_CLASSIFICATION) lazy_supervised_classification();
	else if(mode==LAZY_SEMISUPERVISED_CLASSIFICATION) {
		read_unlabeled_set(unlabeled);
		lazy_semisupervised_classification();
		lazy_supervised_classification();
	}
	else if(mode==LAZY_TRANSDUCTIVE_CLASSIFICATION) lazy_transductive_classification();
	for(int i=0;i<10*MB/(int)sizeof(itemset_t);i++) release_itemset(ITEMSETS[i]);
	free(ITEMSETS);
	__FINISH_TIMER__
	return(0);
}


int main(int argc, char** argv) {
	int c, mode=LAZY_SUPERVISED_CLASSIFICATION;
	char *file_in=NULL, *file_test=NULL, *file_unlabeled=NULL;
	CACHE_CRITERIA.max_size=CACHE_CRITERIA.hits=CACHE_CRITERIA.misses=0;
	CACHE_CLASSES.max_size=CACHE_CLASSES.hits=CACHE_CLASSES.misses=0;
	CACHE_CRITERIA.factor=0.1;
	CACHE_CLASSES.factor=0.1;
	while((c=getopt(argc,argv,"k:f:i:l:g:p:a:c:d:s:x:u:t:n:m:e:h"))!=-1) {
		switch(c) {
			case 'i': file_in=strdup(optarg);
				  break;
			case 't': file_test=strdup(optarg);
				  break;
			case 'u': file_unlabeled=strdup(optarg);
				  break;
			case 'c': MIN_CONF=atof(optarg);
				  break;
			case 'g': RELATIVE=1;
				  MIN_SUPP=atof(optarg);
				  break;
			case 's': MIN_COUNT=atoi(optarg);
				  break;
			case 'd': mode=atoi(optarg);
				  break;
			case 'n': MIN_RULES=atoi(optarg);
				  break;
			case 'm': MAX_SIZE=atoi(optarg);
				  if(MAX_SIZE>MAX_RULE_SIZE) MAX_SIZE=MAX_RULE_SIZE;
				  break;
			case 'p': MIN_SIZE=atoi(optarg);
				  if(MAX_SIZE<1) MIN_SIZE=1;
				  break;
			case 'l': RANKING_SIZE=atoi(optarg);
				  break;
			case 'x': N_CRITERIA=atoi(optarg);
				  break;
			case 'a': CRITERION=atoi(optarg);
				  break;
			case 'e': CACHE_CRITERIA.max_size=atoi(optarg);
				  CACHE_CLASSES.max_size=atoi(optarg);
				  break;
			case 'f': FACTOR=atof(optarg);
				  break;
			case 'k': DELIM=strdup(optarg);
				  break;
			default:  exit(1);
		}
	}
	classify(file_in, file_test, file_unlabeled, mode);
	for(map<string, long long>::iterator it=PROOF.begin();it!=PROOF.end();it++) printf("time: %s %lf\n", (*it).first.c_str(), (*it).second/(double)1000000);
	printf("\n");
	return(0);
}
