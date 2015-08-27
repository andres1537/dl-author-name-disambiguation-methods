/**
THIS MODULE IMPLEMENTS OPERATIONS OF READING/PARSING THE TEST SET, AND PROJECTING THE TRAINING DATA
ACCORDING TO A TEST SET.
*/

#include <stdlib.h>
#include <string.h>
#include "test.h"

/**
GIVEN SOME ITEMS, PROJECTS THE ORIGINAL TRAINING DATA TO THE INSTANCES CONTAINING AT LEAST ONE SPECIFIED ITEM.
*/
int project_training(int* items, int n_items, int* tids) {
	__START_TIMER__
	short* temp=(short*)malloc(sizeof(short)*(N_TRANSACTIONS+1));
	for(int i=0;i<N_TRANSACTIONS+1;i++) temp[i]=0;
	for(int i=0;i<n_items;i++) {
		for(int j=0;j<ITEMSETS[items[i]].count;j++) temp[ITEMSETS[items[i]].list[j]]=1;
	}
	int n_transactions=0;
	for(int i=1;i<N_TRANSACTIONS+1;i++) {
		if(temp[i]==1) tids[n_transactions++]=i;
	}
	free(temp);
	__FINISH_TIMER__
	return(n_transactions);
}

/**
READS THE TEST SET. STORES ALL TEST INSTANCES IN A GLOBAL MAP.
*/
int read_test_set(char* test) {
	__START_TIMER__
	int target=0;
	FILE* file=fopen(test, "r");
	if(file==NULL) {
		fprintf(stderr,"Test file %s not found.\n\n",test);
		exit(-1);
	}
	set<int> instance;
       	TEST=(list_t*)malloc(sizeof(list_t));
	while(1) {
		char line[200*KB];
		fgets(line, 200*KB, file);
		if(feof(file)) break;
		N_TESTS++;
		target=-1;
		char* item=strtok(line, " \n");
		long id=atol(item);
		item=strtok(NULL, " \n");
		while(item!=NULL) {
			if(target==-1 && CLASS_NAME.find(item)!=CLASS_NAME.end()) target=(int)CLASS_NAME[item];
			if(ITEM_MAP.find(item)==ITEM_MAP.end()) {
				ITEM_MAP[item]=N_ITEMSETS;
				int attr_type=NONE;
				if(item[0]==TEXT) attr_type=1;
				else if(item[0]==OUTLINK) attr_type=2;
				else if(item[0]==INLINK) attr_type=4;
				else if(item[0]==VENUE) attr_type=8;
				ITEMSETS[N_ITEMSETS].count=0;
				ITEMSETS[N_ITEMSETS].size=1;
				ITEMSETS[N_ITEMSETS].evidence=attr_type;
				ITEMSETS[N_ITEMSETS].layout=(int*)malloc(sizeof(int));
				ITEMSETS[N_ITEMSETS].list=(int*) malloc(sizeof(int));
				ITEMSETS[N_ITEMSETS].layout[0]=N_ITEMSETS;
				SYMBOL_TABLE[N_ITEMSETS]=strdup(item);
				++N_ITEMSETS;
			}
			if(CLASS_NAME.find(item)==CLASS_NAME.end()) instance.insert(ITEM_MAP[item]);
			item=strtok(NULL, " \n");
			if(item==NULL) break;
		}
		if(N_TESTS==1) {
			TEST->instance=(int*)malloc(sizeof(int)*instance.size());
			TEST->size=0;
			for(set<int>::iterator it=instance.begin();it!=instance.end();it++) TEST->instance[TEST->size++]=*it;
			TEST->id=id;
			TEST->label=target;
      			TEST->next=NULL;
		}
		else {
        		list_t* q=TEST;
			while(q->next!=NULL) q=q->next;
			list_t* t=(list_t*)malloc(sizeof(list_t));
			t->instance=(int*)malloc(sizeof(int)*instance.size());
			t->size=0;
			for(set<int>::iterator it=instance.begin();it!=instance.end();it++) t->instance[t->size++]=*it;
			t->id=id;
			t->label=target;
			t->next=NULL;
			q->next=t;
		}
		instance.clear();
	}
	fclose(file);
	__FINISH_TIMER__
	return(0);
}
