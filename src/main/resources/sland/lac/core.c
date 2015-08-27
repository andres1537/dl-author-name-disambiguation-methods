/**
THIS MODULE IMPLEMENTS OPERATIONS REGARDING CLASS PREDICTION.
*/

#include "core.h"

short mark_target[MAX_CLASSES];
int projection_size=0, count_target[MAX_CLASSES];
float avg_size=0;

static int item_cmp(const void* a, const void* b) {
	int* i=(int*)a;
	int* j=(int*)b;
	if(ITEMSETS[*i].count>ITEMSETS[*j].count) return(1);
	if(ITEMSETS[*i].count<ITEMSETS[*j].count) return(-1);
	return(0);
}

/**
PRINTS ALL STATISTICS ASSOCIATED WITH A PREDICTION.
*/
void print_statistics(int n_tests, int true_label, long id, prediction_t* prediction, evaluation_t evaluator) {
	//int position;
	//for(position=0;;position++) {
	//	if(prediction[CRITERION].score.ordered_labels[position]==true_label) break;
	//}
	float ranking=0;
	for(int i=0;i<MAX_CLASSES;i++) ranking+=i*prediction[CRITERION].score.points[i];
	float* rel=(float*)malloc(sizeof(float)*MAX_CLASSES);
	for(int i=0;i<MAX_CLASSES;i++) rel[i]=count_target[i]/(float)projection_size;
	//printf("%d-%d %f%% id= %ld label= %d correct= %d %d/%d", n_tests, N_TESTS, 100*n_tests/(float)N_TESTS, id, true_label, prediction[CRITERION].correct, position+1, MAX_CLASSES);
	printf("%d-%d %f%% id= %ld label= %d correct= %d", n_tests, N_TESTS, 100*n_tests/(float)N_TESTS, id, true_label, prediction[CRITERION].correct);
	//for(int i=0;i<N_CRITERIA;i++) printf(" prediction[%d]= %d", i, prediction[i].label);
	printf(" prediction[%d]= %d", CRITERION, prediction[CRITERION].label);
	printf(" ranking= %f rules= %d projection= %f avg_size= %f ", ranking, N_RULES, projection_size/(float)N_TRANSACTIONS, avg_size);
	for(int i=0;i<MAX_CLASSES;i++) {
//		printf("Rel[%d]= %f Score[%d]= ", i, rel[i], i);
		//for(int j=0;j<N_CRITERIA;j++) printf("%f ", prediction[j].score.points[i]);
//		printf("%f ", prediction[CRITERION].score.points[i]);
//		printf("Prec[%d]= %f Rec[%d]= %f F1[%d]= %f    ", i, evaluator.precision[i], i, evaluator.recall[i], i, evaluator.f1[i]);
	}
	printf("MF1= %f Acc= %f ", evaluator.mf1, evaluator.acc);
	printf("Cache= %f %f\n", CACHE_CRITERIA.content.size()/(float)CACHE_CRITERIA.max_size, CACHE_CLASSES.content.size()/(float)CACHE_CLASSES.max_size);
	free(rel);
}

int get_THE_criterion(int* instance, int instance_size) {
	prediction_t prediction;
	prediction.label=CONFIDENCE;
	int min_count=1, init=1, size=1, n_items=0;
	int* items=(int*) malloc(sizeof(int)*instance_size);
	for(int i=0;i<instance_size;i++) {
		if(instance[i]<N_ITEMSETS) items[n_items++]=instance[i];
	}
	qsort((int*) items, n_items, sizeof(int), item_cmp);
	reset_rules();
	while(size<MAX_SIZE) {
		if(init) {
			init=0;
			int* tids=(int*)malloc(sizeof(int)*N_TRANSACTIONS);
			projection_size=project_training(items, n_items, tids);
			free(tids);
		}
		if(RELATIVE) min_count=(int)(MIN_SUPP*projection_size);
		induce_rules(items, n_items, count_target, size, projection_size, min_count, PRED_CRITERION);
		prediction.score=get_TOTAL_score(CONFIDENCE);
		size++;
		if(prediction.score.points[prediction.score.ordered_labels[0]]>=FACTOR*prediction.score.points[prediction.score.ordered_labels[1]] || size==MAX_SIZE || size>n_items) {
			if(N_RULES>0) prediction.label=prediction.score.ordered_labels[0];
			else prediction.label=CONFIDENCE;
			break;
		}
		free(prediction.score.ordered_labels);
		free(prediction.score.points);
	}
	free(items);
	reset_rules();
	return(prediction.label);
}

/**
INDUCES A RULE SET. THE RULE SET KEEPS GROWING UNTIL A CLASS IS SUFFICIENTLY BETTER THAN ANOTHER.
THEN, PERFORMS THE PREDICTION.
*/
prediction_t get_THE_prediction(int* instance, int instance_size, int true_label, int criterion, long int id) {
	prediction_t prediction;
	int n_rules[2]={0,0}, min_count=MIN_COUNT, init=1, size=MIN_SIZE, n_items=0, default_prediction=0;
	int* items=(int*) malloc(sizeof(int)*instance_size);
	for(int i=0;i<instance_size;i++) {
		if(instance[i]<N_ITEMSETS) items[n_items++]=instance[i];
	}
	qsort((int*) items, n_items, sizeof(int), item_cmp);
	reset_rules();
	while(size<MAX_SIZE) {
		if(init) {
			init=0;
			int* tids=(int*)malloc(sizeof(int)*N_TRANSACTIONS);
			projection_size=project_training(items, n_items, tids);
			for(int i=0;i<MAX_CLASSES;i++) count_target[i]=0;
			for(int i=0;i<projection_size;i++) count_target[TID_CLASSES[tids[i]]]++;
			free(tids);
			for(int i=1;i<MAX_CLASSES;i++) {
				if(count_target[i]>=count_target[default_prediction]) default_prediction=i;
			}
		}
		if(RELATIVE) min_count=(int)(MIN_SUPP*projection_size);
		induce_rules(items, n_items, count_target, size, projection_size, min_count, PRED_CLASS);
		n_rules[0]=n_rules[1];
		n_rules[1]=N_RULES;
		prediction.score=get_TOTAL_score(criterion);
// comentarios Anderson
//printf("    probabilities size: %d criterion: %d label: %d", size, CRITERION, true_label);
//for(int i=0;i<MAX_CLASSES;i++) printf(" %d %f", prediction.score.ordered_labels[i], prediction.score.points[prediction.score.ordered_labels[i]]);
// comentari anderson
//printf(" factor: %f rules: %d\n", prediction.score.points[prediction.score.ordered_labels[0]]/(float)prediction.score.points[prediction.score.ordered_labels[1]], N_RULES);

		size++;
		if(prediction.score.points[prediction.score.ordered_labels[0]]>=MIN_CONF || size==MAX_SIZE || size>n_items || n_rules[0]==n_rules[1]) {
		//eh tudo uma questao de saber quando deve-se parar
		//if(prediction.score.ordered_labels[0]==true_label || size==MAX_SIZE || size>n_items || n_rules[0]==n_rules[1]) {
			prediction.label=prediction.score.ordered_labels[0];
			break;
		}
		free(prediction.score.ordered_labels);
		free(prediction.score.points);
	}
	free(items);
	//if(N_RULES==0) prediction.label=default_prediction;

int k=0;
for(int i=0;i<N_RULES;i++) {
	//printf("rule:   ");
	//print_rule(RULES[i], PRED_CLASS);
	//printf("\n");
	if(RULES[i].evidence=OUTLINK) k++;
}

	//if(N_RULES<MIN_RULES) {
	if(k<MIN_RULES) {
printf("new group detected: %ld \n",id);
		if(COUNT_TARGET[true_label]==0 && mark_target[true_label]==0) {
			mark_target[true_label]=1;
			prediction.label=true_label;
		}
		else prediction.label=default_prediction;
		for(int i=0;i<MAX_CLASSES;i++) {
			prediction.score.points[i]=0;
			prediction.score.ordered_labels[0]=i;
		}
		prediction.score.points[prediction.label]=1.00;
		int x=prediction.score.ordered_labels[0];
		prediction.score.ordered_labels[prediction.label]=x;
		prediction.score.ordered_labels[0]=prediction.label;
// anderson - cleaning the cache
		map<set<int>, content_t>::iterator begin=CACHE_CRITERIA.content.begin();
		CACHE_CRITERIA.content.erase(begin, CACHE_CRITERIA.content.end());
		begin=CACHE_CLASSES.content.begin();
		CACHE_CLASSES.content.erase(begin, CACHE_CLASSES.content.end());
	}
	avg_size=0;
	for(int i=0;i<N_RULES;i++) avg_size+=RULES[i].size;
	if(N_RULES>0) avg_size/=N_RULES;
	prediction.correct=(prediction.label==true_label);
	return(prediction);
}

int lazy_supervised_classification() {
	__START_TIMER__
	evaluation_t evaluator;
	prediction_t prediction[MAX_CRITERIA];
	for(int i=0;i<MAX_CLASSES;i++) mark_target[i]=0;
	initialize_evaluation(&evaluator);
	int n_tests=1;
	list_t* test=TEST;
	while(test) {
		if(META_LEARNING) CRITERION=get_THE_criterion(test->instance, test->size);
		for(int i=0;i<N_CRITERIA;i++) prediction[i]=get_THE_prediction(test->instance, test->size, test->label, i, test->id);
		update_evaluation(&evaluator, prediction[CRITERION].label, test->label);
printf("%d %f ", CRITERION, prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[0]]);
		print_statistics(n_tests, test->label, test->id, prediction, evaluator);
		n_tests++;
		list_t* x=test->next;
		free(test->instance);
		free(test);
		test=x;
	}
	free(TID_CLASSES);
	printf("\n");
	for(int i=0;i<MAX_CLASSES;i++) printf("CLASS(%d)= %d Prec= %f Rec= %f F1= %f   ", i, evaluator.true_labels[i], evaluator.precision[i], evaluator.recall[i], evaluator.f1[i]);
	printf("Acc= %f  MF1= %f   *    hits= %ld %ld misses= %ld %ld\n", evaluator.acc, evaluator.mf1, CACHE_CLASSES.hits, CACHE_CRITERIA.hits, CACHE_CLASSES.misses, CACHE_CRITERIA.misses);
	finalize_evaluation(&evaluator);
	__FINISH_TIMER__
	return(0);
}

int lazy_transductive_classification() {
	__START_TIMER__
// anderson - usar cache	CACHE_CRITERIA.max_size=CACHE_CLASSES.max_size=0;
	evaluation_t evaluator;
	prediction_t prediction[MAX_CRITERIA];
	for(int i=0;i<MAX_CLASSES;i++) mark_target[i]=0;
	initialize_evaluation(&evaluator);
	int abstain=0, n_tests=1, remaining_tests=N_TESTS, locked=1;
	float min_conf=MIN_CONF;
	list_t* test=TEST;
	while(test) {
		reset_rules();
		//for(int i=0;i<N_CRITERIA;i++) prediction[i]=get_THE_prediction(test->instance, test->size, test->label, i);
		prediction[CRITERION]=get_THE_prediction(test->instance, test->size, test->label, CRITERION, test->id);
		if(!locked || prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[0]]>min_conf || prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[0]]/(float)prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[1]]>=FACTOR) {
		//if(!locked || prediction[CRITERION].label==test->label) {
			update_evaluation(&evaluator, prediction[CRITERION].label, test->label);
//printf("%d %f ", CRITERION, prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[0]]);
			print_statistics(n_tests, test->label, test->id, prediction, evaluator);
			if(locked) {
				N_TRANSACTIONS++;
				COUNT_TARGET[prediction[CRITERION].label]++;
				TID_CLASSES[N_TRANSACTIONS]=prediction[CRITERION].label;
				if(COUNT_TARGET[prediction[CRITERION].label]==1) {
					ITEMSETS[TARGET_ID[prediction[CRITERION].label]].count=1;
					ITEMSETS[prediction[CRITERION].label].list[0]=N_TRANSACTIONS;
				}
				else ITEMSETS[TARGET_ID[prediction[CRITERION].label]].list=(int*) realloc(ITEMSETS[TARGET_ID[prediction[CRITERION].label]].list, sizeof(int)*(ITEMSETS[TARGET_ID[prediction[CRITERION].label]].count+1));
				ITEMSETS[TARGET_ID[prediction[CRITERION].label]].list[ITEMSETS[TARGET_ID[prediction[CRITERION].label]].count]=N_TRANSACTIONS;
				ITEMSETS[TARGET_ID[prediction[CRITERION].label]].count++;
				if(N_TRANSACTIONS>=CAPACITY) {
					CAPACITY+=N_TRANSACTIONS;
					TID_CLASSES=(int*)realloc(TID_CLASSES, sizeof(int)*CAPACITY);
				}
				for(int j=0;j<test->size;j++) {
					ITEMSETS[test->instance[j]].list=(int*) realloc(ITEMSETS[test->instance[j]].list, sizeof(int)*(ITEMSETS[test->instance[j]].count+1));
					ITEMSETS[test->instance[j]].list[ITEMSETS[test->instance[j]].count]=N_TRANSACTIONS;
					ITEMSETS[test->instance[j]].count++;
				}
			}
			n_tests++;
			remaining_tests--;
			abstain=0;
			list_t* x=test->next;
			free(test->instance);
			free(test);
			test=x;
			TEST=test;
		}
		else if(abstain<remaining_tests) {
printf("abstaining id= %ld %d %d %f %f %f\n", test->id, abstain, remaining_tests, min_conf, prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[0]]/(float)prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[1]], avg_size);

			abstain++;
        		list_t* q=TEST;
			while(q->next!=NULL) q=q->next;
			list_t* t=(list_t*)malloc(sizeof(list_t));
			t->instance=(int*)malloc(sizeof(int)*test->size);
			t->size=0;
			for(int i=0;i<test->size;i++) t->instance[t->size++]=test->instance[i];
			t->id=test->id;
			t->label=test->label;
			t->next=NULL;
			q->next=t;
			list_t* x=test->next;
			free(test->instance);
			free(test);
			test=x;
			TEST=test;
		}
		else locked=0;
	}
	free(TID_CLASSES);
	printf("\n");
//anderson
//	for(int i=0;i<MAX_CLASSES;i++) printf("CLASS(%d)= %d Prec= %f Rec= %f F1= %f   ", i, evaluator.true_labels[i], evaluator.precision[i], evaluator.recall[i], evaluator.f1[i]);
	printf("Acc= %f  MF1= %f   *    hits= %ld %ld misses= %ld %ld\n", evaluator.acc, evaluator.mf1, CACHE_CRITERIA.hits, CACHE_CRITERIA.misses, CACHE_CLASSES.hits, CACHE_CLASSES.misses);
	finalize_evaluation(&evaluator);
	__FINISH_TIMER__
	return(0);
}

int lazy_semisupervised_classification() {
	__START_TIMER__
	CACHE_CRITERIA.max_size=CACHE_CLASSES.max_size=0;
	evaluation_t evaluator;
	prediction_t prediction[MAX_CRITERIA];
	for(int i=0;i<MAX_CLASSES;i++) mark_target[i]=0;
	initialize_evaluation(&evaluator);
	int abstain=0, n_points=1, remaining_points=N_POINTS;
	float min_conf=MIN_CONF;
	list_t* unlabeled=UNLABELED;
	while(unlabeled) {
		reset_rules();
		prediction[CRITERION]=get_THE_prediction(unlabeled->instance, unlabeled->size, unlabeled->label, CRITERION, unlabeled->id);
		if(prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[0]]>min_conf || prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[0]]/(float)prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[1]]>=FACTOR) {
		//if(prediction[CRITERION].label==unlabeled->label) {
			update_evaluation(&evaluator, prediction[CRITERION].label, unlabeled->label);
			print_statistics(n_points, unlabeled->label, unlabeled->id, prediction, evaluator);
			N_TRANSACTIONS++;
			COUNT_TARGET[prediction[CRITERION].label]++;
			TID_CLASSES[N_TRANSACTIONS]=prediction[CRITERION].label;
			ITEMSETS[TARGET_ID[prediction[CRITERION].label]].list=(int*) realloc(ITEMSETS[TARGET_ID[prediction[CRITERION].label]].list, sizeof(int)*(ITEMSETS[TARGET_ID[prediction[CRITERION].label]].count+1));
			ITEMSETS[TARGET_ID[prediction[CRITERION].label]].list[ITEMSETS[TARGET_ID[prediction[CRITERION].label]].count]=N_TRANSACTIONS;
			ITEMSETS[TARGET_ID[prediction[CRITERION].label]].count++;
			if(N_TRANSACTIONS>=CAPACITY) {
				CAPACITY+=N_TRANSACTIONS;
				TID_CLASSES=(int*)realloc(TID_CLASSES, sizeof(int)*CAPACITY);
			}
			for(int j=0;j<unlabeled->size;j++) {
				ITEMSETS[unlabeled->instance[j]].list=(int*) realloc(ITEMSETS[unlabeled->instance[j]].list, sizeof(int)*(ITEMSETS[unlabeled->instance[j]].count+1));
				ITEMSETS[unlabeled->instance[j]].list[ITEMSETS[unlabeled->instance[j]].count]=N_TRANSACTIONS;
				ITEMSETS[unlabeled->instance[j]].count++;
			}

			n_points++;
			remaining_points--;
			abstain=0;
			list_t* x=unlabeled->next;
			free(unlabeled->instance);
			free(unlabeled);
			unlabeled=x;
			UNLABELED=unlabeled;
		}
		else if(abstain<remaining_points) {
//printf("abstaining %d %d %f %f %f\n", abstain, remaining_points, min_conf, prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[0]]/(float)prediction[CRITERION].score.points[prediction[CRITERION].score.ordered_labels[1]], avg_size);
			abstain++;
        		list_t* q=UNLABELED;
			while(q->next!=NULL) q=q->next;
			list_t* t=(list_t*)malloc(sizeof(list_t));
			t->instance=(int*)malloc(sizeof(int)*unlabeled->size);
			t->size=0;
			for(int i=0;i<unlabeled->size;i++) t->instance[t->size++]=unlabeled->instance[i];
			t->label=unlabeled->label;
			t->next=NULL;
			q->next=t;
			list_t* x=unlabeled->next;
			free(unlabeled->instance);
			free(unlabeled);
			unlabeled=x;
			UNLABELED=unlabeled;
		}
		else {
			break;
			//abstain=0;
			//if(CRITERION<N_CRITERIA) {
			//	CRITERION++;
			//}
			//else {
			//	min_conf*=0.95;
			//	CRITERION=0;
			//}
		}
	}
	printf("\n");
//	for(int i=0;i<MAX_CLASSES;i++) printf("CLASS(%d)= %d Prec= %f Rec= %f F1= %f   ", i, evaluator.true_labels[i], evaluator.precision[i], evaluator.recall[i], evaluator.f1[i]);
	printf("Acc= %f  MF1= %f   *    hits= %ld %ld misses= %ld %ld\n", evaluator.acc, evaluator.mf1, CACHE_CRITERIA.hits, CACHE_CRITERIA.misses, CACHE_CLASSES.hits, CACHE_CLASSES.misses);
	finalize_evaluation(&evaluator);
	__FINISH_TIMER__
	return(0);
}
