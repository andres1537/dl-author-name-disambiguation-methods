//implementar o principio da inclusao-exclusao


/**
THIS MODULE IMPLEMENTS OPERATIONS REGARDING RULE CREATION AND MAINTENANCE.
*/

#include "rule.h"

/**
COMPARES TWO RULES. SEVERAL CRITERIA ARE USED. THE FIRST CRITERIA IS THE CRITERION
SPECIFIED IN THE COMAND LINE. IF THERE IS A TIE, OTHER CRITERIA ARE USED UNTIL THE
TIE IS FINALLY BROKEN.
*/
static int rule_cmp(const void* a, const void* b) {
	rule_t* i=(rule_t*)a;
	rule_t* j=(rule_t*)b;
	if((*i).criterion[CRITERION]<(*j).criterion[CRITERION]) return(1);
	if((*i).criterion[CRITERION]>(*j).criterion[CRITERION]) return(-1);
	return(0);
}

/**
GENERATES COMBINATIONS OF ITEMS.
*/
template <class elem_t> bool next_combination(elem_t n_begin, elem_t n_end, elem_t r_begin, elem_t r_end) {
	bool boolmarked=false;
	elem_t r_marked=0;
	elem_t n_it1=n_end;
	--n_it1;
	elem_t tmp_r_end=r_end;
	--tmp_r_end;
	elem_t tmp_r_begin=r_begin;
	--tmp_r_begin;

	for(elem_t r_it1=tmp_r_end; r_it1!=tmp_r_begin ; --r_it1,--n_it1) {
		if(*r_it1==*n_it1 ) {
			if(r_it1!=r_begin) {
				boolmarked=true;
				r_marked=(--r_it1);
				++r_it1;
				continue;
			}
			else return false;
		}
		else {
			if(boolmarked==true) {
				elem_t n_marked=0;
				for(elem_t n_it2=n_begin;n_it2!=n_end;++n_it2) {
					if(*r_marked==*n_it2) {
						n_marked=n_it2;
						break;
					}
				}
				elem_t n_it3=++n_marked;    
				for(elem_t r_it2=r_marked;r_it2!=r_end;++r_it2,++n_it3) {
					*r_it2=*n_it3;
				}
				return true;
			}
			for(elem_t n_it4=n_begin; n_it4!=n_end; ++n_it4) {
				if(*r_it1==*n_it4) {
					*r_it1=*(++n_it4);
					return true;    
				}
			}
		}
	}  
	return true;
}

/**
PRINTS A RULE.
*/
void print_rule(rule_t rule, short rule_type) {
	__START_TIMER__
	for(int i=0;i<rule.size-1;i++) printf("%s-", SYMBOL_TABLE[rule.ant[i]].c_str());
	if(rule_type==PRED_CRITERION) printf("->%s ", SYMBOL_TABLE[CRITERION_ID[rule.label]].c_str());
	else if(rule_type==PRED_CLASS) printf("->%s ", SYMBOL_TABLE[TARGET_ID[rule.label]].c_str());
	printf("count= %d supp= %f conf= %f odds= %f added_value= %f strenght= %f phi= %f cosine= %f chi2= %f ss= %f satisfaction= %f gain= %f relconf= %f YulesQ= %f YulesY= %f certainty= %f laplace= %f novelty= %f", rule.count, rule.criterion[SUPPORT], rule.criterion[CONFIDENCE], rule.criterion[ODDS], rule.criterion[ADDEDVALUE], rule.criterion[STRENGTH], rule.criterion[PHI], rule.criterion[COSINE], rule.criterion[WEIGHTEDCHI2], rule.criterion[SS], rule.criterion[SATISFACTION], rule.criterion[GAIN], rule.criterion[RELACC], rule.criterion[YULESQ], rule.criterion[YULESY], rule.criterion[CERTAINTY], rule.criterion[LAPLACE], rule.criterion[NOVELTY]);
	__FINISH_TIMER__
}

/**
RESETS A RULE FROM THE GLOBAL ARRAY OF RULES. THE SIZE OF THE ARRAY IS AUTOMATICALY REDUCED, THUS
PAY CAUTION WHEN CALLING THIS FUNCTION INSIDE A LOOP.
*/
void reset_rule(int id) {
	__START_TIMER__
	if(id<N_RULES) {
		N_RULES--;
		free(RULES[id].list);
		RULES[id]=RULES[N_RULES];
	}
	__FINISH_TIMER__
}

/**
RESETS ALL RULES IN THE GLOBAL ARRAY OF RULES.
*/
void reset_rules() {
	__START_TIMER__
	for(int i=0;i<N_RULES;i++) {
		RULES[i].label=-1;
		RULES[i].size=0;
		RULES[i].count=0;
		for(int j=0;j<MAX_RULE_SIZE;j++) RULES[i].ant[j]=0;
		for(int j=0;j<MAX_CRITERIA;j++) RULES[i].criterion[j]=0;
		free(RULES[i].list);
	}
	N_RULES=0;
	__FINISH_TIMER__
}

/**
INDUCES RULES SATISFYING SOME SOME CONSTRAINTS, SUCH AS ITEMS, SIZE AND SUPPORT/CONFIDENCE.
CRITERIA ARE COMPUTED BASED ON THE PROJECTED TRAINING DATA, WHICH IS SPECIFIED IN tids/projection_size.
INDUCING A RULE INVOLVES DATA SCANS AND ITEMSET CREATION, THUS, BEFORE GENERATING A RULE,
A CACHE IS ACCESSED IN ORDER TO FIND THE RULE, POSSIBLY AVOIDING REWORK.
AT THE END, ALL INDUCED RULES ARE SORTED AND STORED IN A GLOBAL ARRAY OF RULES.
*/
void induce_rules(int* items, int n_items, int* count_target, int level, int projection_size, int min_count, short rule_type) {
	if(level>n_items) return;
	__START_TIMER__
	struct timeval t1;
	gettimeofday(&t1, NULL);
	double total_time=0;
	int* cb1=(int*)malloc(sizeof(int)*level);
	int* cb2=(int*)malloc(sizeof(int)*(level+1));
	for(int i=0;i<level;i++) cb1[i]=items[i];
	do {
		for(int i=0;i<level;i++) cb2[i]=cb1[i];
		itemset_t ant;
		content_t cont;
		if(rule_type==PRED_CRITERION) cont=get_from_cache(CACHE_CRITERIA, cb1, level);
		else if(rule_type==PRED_CLASS) cont=get_from_cache(CACHE_CLASSES, cb1, level);
		if(cont.status==0) {
			ant=create_itemset(cb1, level);
			cont.ant_count=ant.count;
			cont.evidence=ant.evidence;
		}
		if(rule_type==PRED_CRITERION) {
			for(int i=0;i<MAX_CLASSES;i++) {
				itemset_t cons;
				cons.size=1;
				if(COUNT_CRITERION[i]==0) cont.cons_count[i]=0;
				else {
					cb2[level]=CRITERION_ID[i];
					if(cont.status==0) {
						cons=create_itemset(cb2, level+1);
						cont.cons_count[i]=cons.count;
					}
				}
				if(cont.cons_count[i]>=min_count) {
					rule_t rule;
					rule.count=cont.cons_count[i];
					rule.criterion[SUPPORT]=COMPUTE_SUPPORT;
					rule.criterion[CONFIDENCE]=COMPUTE_CONFIDENCE;
					RULES[N_RULES].count=rule.count;
					RULES[N_RULES].criterion[SUPPORT]=rule.criterion[SUPPORT];
					RULES[N_RULES].criterion[CONFIDENCE]=rule.criterion[CONFIDENCE];
					RULES[N_RULES].id=N_RULES;
					RULES[N_RULES].evidence=cont.evidence;
					RULES[N_RULES].size=level+1;
					RULES[N_RULES].label=i;
					for(int j=0;j<level;j++) RULES[N_RULES].ant[j]=cb1[j];
					if(N_RULES>=MAX_RULES-1) {
						release_itemset(cons);
						break;
					}
					N_RULES++;
				}
				if(cont.status==0) release_itemset(cons);
			}
		}
		else if(rule_type==PRED_CLASS) {
			for(int i=0;i<MAX_CLASSES;i++) {
				itemset_t cons;
				cons.size=1;
				if(COUNT_TARGET[i]==0) {
					cont.cons_count[i]=0;
				}
				else {
					cb2[level]=TARGET_ID[i];
					if(cont.status==0) {
						cons=create_itemset(cb2, level+1);
						cont.cons_count[i]=cons.count;
					}
				}
				if(cont.cons_count[i]>=min_count) {
					rule_t rule;
					rule.count=cont.cons_count[i];
					rule.criterion[CONFIDENCE]=COMPUTE_CONFIDENCE;
					rule.criterion[CONVICTION]=COMPUTE_CONVICTION;
					rule.criterion[GAIN]=COMPUTE_GAIN;
					rule.criterion[ONE]=COMPUTE_ONE;
					rule.criterion[SUPPORT]=COMPUTE_SUPPORT;
					rule.criterion[COSINE]=COMPUTE_COSINE;
					rule.criterion[PHI]=COMPUTE_PHI;
					rule.criterion[NOVELTY]=COMPUTE_NOVELTY;
					rule.criterion[ODDS]=COMPUTE_ODDS;
					rule.criterion[YULESQ]=COMPUTE_YULESQ;
					rule.criterion[YULESY]=COMPUTE_YULESY;
					rule.criterion[KAPPA]=COMPUTE_KAPPA;
					rule.criterion[JMEASURE]=COMPUTE_JMEASURE;
					rule.criterion[INTEREST]=COMPUTE_INTEREST;
					rule.criterion[SHAPIRO]=COMPUTE_SHAPIRO;
					rule.criterion[CERTAINTY]=COMPUTE_CERTAINTY;
					rule.criterion[ADDEDVALUE]=COMPUTE_ADDEDVALUE;
					rule.criterion[STRENGTH]=COMPUTE_STRENGTH;
					rule.criterion[KLOSGEN]=COMPUTE_KLOSGEN;
					rule.criterion[JACCARD]=COMPUTE_JACCARD;
					rule.criterion[WEIGHTEDCHI2]=COMPUTE_WEIGHTEDCHI2;
					rule.criterion[SS]=COMPUTE_SS;
					rule.criterion[WRSENSITIVITY]=COMPUTE_WRSENSITIVITY;
					rule.criterion[WRSPECIFICITY]=COMPUTE_WRSPECIFICITY;
					rule.criterion[RELSENSITIVITY]=COMPUTE_RELSENSITIVITY;
					rule.criterion[RELSPECIFICITY]=COMPUTE_RELSPECIFICITY;
					rule.criterion[WRACC]=COMPUTE_WRACC;
					rule.criterion[RELACC]=COMPUTE_RELACC;
					rule.criterion[RELNREG]=COMPUTE_RELNREG;
					rule.criterion[LAPLACE]=COMPUTE_LAPLACE;
					rule.criterion[SATISFACTION]=COMPUTE_SATISFACTION;
					RULES[N_RULES].count=rule.count;
					for(int j=0;j<MAX_CRITERIA;j++) RULES[N_RULES].criterion[j]=rule.criterion[j];
					RULES[N_RULES].id=N_RULES;
					RULES[N_RULES].evidence=cont.evidence;
					RULES[N_RULES].size=level+1;
					RULES[N_RULES].label=i;
					for(int j=0;j<level;j++) RULES[N_RULES].ant[j]=cb1[j];
					if(N_RULES>=MAX_RULES-1) {
						release_itemset(cons);
						break;
					}
					N_RULES++;
				}
				if(cont.status==0) release_itemset(cons);
			}
		}
		if(cont.status==0) {
			cont.status=1;
			if(rule_type==PRED_CRITERION) insert_into_cache(CACHE_CRITERIA, cb1, level, cont);
			else if(rule_type==PRED_CLASS) insert_into_cache(CACHE_CLASSES, cb1, level, cont);
			release_itemset(ant);
		}
		struct timeval t2;
		gettimeofday(&t2, NULL);
		total_time=((t2.tv_sec-t1.tv_sec)*1000000+t2.tv_usec-t1.tv_usec)/(double)1000000;
	}
	while(N_RULES<MAX_RULES-1 && next_combination(items,items+n_items,cb1,cb1+(level)) && total_time<MAX_TIME_PER_TEST);
	free(cb1);
	free(cb2);
	qsort((rule_t*) RULES, N_RULES, sizeof(rule_t), rule_cmp);
	__FINISH_TIMER__
}
