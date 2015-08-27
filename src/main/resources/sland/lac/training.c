/**
THIS MODULE IMPLEMENTS ALL OPERATIONS OF READING/PARSING THE TRAINING DATA.
*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "training.h"

/**
READS THE TRAINING DATA. ALL ITEMS ARE STORED IN A GLOBAL ARRAY, WHICH IS
THEN ACCESSED DURING RULE INDUCTION.
*/
int read_training_set(char* training) {
	__START_TIMER__
	int target=-1, criterion=-1;
	char target_name[MAX_CLASSES][100], criterion_name[MAX_CRITERIA][100];
	set<string> proc_items;
	FILE* file=fopen(training,"r");
	if(file==NULL) {
		fprintf(stderr,"Training set %s not found.\n\n", training);
		exit(-1);
	}
	for(int i=0;i<MAX_CLASSES;i++) {
		sprintf(target_name[i], "CLASS=%d", i);
		sprintf(criterion_name[i], "CRITERIA=%d", i);
		CLASS_NAME[target_name[i]]=i;
		CRITERION_NAME[criterion_name[i]]=i;
		COUNT_TARGET[i]=0;
		COUNT_CRITERION[i]=0;
		TARGET_ID[i]=N_ITEMSETS;
		//TARGET_ID[i]=-1;
		CRITERION_ID[i]=-1;

		ITEMSETS[N_ITEMSETS].count=0;
		ITEMSETS[N_ITEMSETS].size=1;
		ITEMSETS[N_ITEMSETS].evidence=CLASS;
		ITEMSETS[N_ITEMSETS].layout=(int*)malloc(sizeof(int));
		ITEMSETS[N_ITEMSETS].list=(int*)malloc(sizeof(int));
		ITEMSETS[N_ITEMSETS].layout[0]=N_ITEMSETS;
		ITEMSETS[N_ITEMSETS].list[0]=-1;
		SYMBOL_TABLE[N_ITEMSETS]=strdup(target_name[i]);
		ITEM_MAP[target_name[i]]=N_ITEMSETS;
		++N_ITEMSETS;
	}
	TID_CLASSES=(int*)malloc(sizeof(int)*CAPACITY);
	while(1) {
		int attr_type=CLASS;
		char line[200*KB];
		fgets(line, 200*KB, file);
		if(feof(file)) break;
		N_TRANSACTIONS++;
		if(N_TRANSACTIONS>=CAPACITY) {
			CAPACITY+=N_TRANSACTIONS;
			TID_CLASSES=(int*)realloc(TID_CLASSES, sizeof(int)*CAPACITY);
		}
		proc_items.clear();
		target=criterion=-1;
		char* item=strtok(line, " \n");
		item=strtok(NULL, " \n");
		while(item!=NULL) {
			if(CRITERION_NAME.find(item)!=CRITERION_NAME.end()) {
				META_LEARNING=1;
				criterion=(int)CRITERION_NAME[item];
				COUNT_CRITERION[criterion]++;
			}
			if(target==-1) {
				if(CLASS_NAME.find(item)!=CLASS_NAME.end()) {
					target=(int)CLASS_NAME[item];
					COUNT_TARGET[target]++;
					TID_CLASSES[N_TRANSACTIONS]=target;
				}
			}
			if(proc_items.find(item)==proc_items.end()) {
				proc_items.insert(item);
				if(ITEM_MAP.find(item)!=ITEM_MAP.end()) {
					int index=(int)(ITEM_MAP[item]);
					ITEMSETS[index].list=(int*) realloc(ITEMSETS[index].list, sizeof(int)*(ITEMSETS[index].count+1));
					ITEMSETS[index].list[ITEMSETS[index].count]=N_TRANSACTIONS;
					ITEMSETS[index].count++;
				}
				else {
					if(CRITERION_NAME.find(item)!=CRITERION_NAME.end()) {
						META_LEARNING=1;
						int criterion=(int) CRITERION_NAME[item];
						CRITERION_ID[criterion]=N_ITEMSETS; 
					}
					ITEMSETS[N_ITEMSETS].count=1;
					ITEMSETS[N_ITEMSETS].size=1;
					ITEMSETS[N_ITEMSETS].evidence=attr_type;
					ITEMSETS[N_ITEMSETS].layout=(int*)malloc(sizeof(int));
					ITEMSETS[N_ITEMSETS].list=(int*)malloc(sizeof(int));
					ITEMSETS[N_ITEMSETS].layout[0]=N_ITEMSETS;
					ITEMSETS[N_ITEMSETS].list[0]=N_TRANSACTIONS;
					SYMBOL_TABLE[N_ITEMSETS]=strdup(item);
					ITEM_MAP[item]=N_ITEMSETS;
					++N_ITEMSETS;
				}
			}
			item=strtok(NULL, " \n");
			if(item==NULL) break;
			if(item[0]==TEXT) attr_type=1;
			else if(item[0]==OUTLINK) attr_type=2;
			else if(item[0]==INLINK) attr_type=4;
			else if(item[0]==VENUE) attr_type=8;
			else attr_type=CLASS;
		}
	}
	fclose(file);
	__FINISH_TIMER__
	return(0);
}
