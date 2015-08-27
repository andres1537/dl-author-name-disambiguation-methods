#ifndef _TRAINING_
#define _TRAINING_

#include <stdio.h>
#include <set>
#include <map>

#include "itemset.h"
#include "evidence.h"
#include "limits.h"
#include "timer.h"

using namespace std;

extern int CLASS_MAP[MAX_CLASSES], META_LEARNING, N_TRANSACTIONS, TARGET_ID[MAX_CLASSES], CRITERION_ID[MAX_CLASSES], COUNT_TARGET[MAX_CLASSES], COUNT_CRITERION[MAX_CLASSES], *TID_CLASSES, CAPACITY;
extern char *DELIM;
extern map<string, int> CLASS_NAME;
extern map<string, int> CRITERION_NAME;
extern map<int, string> SYMBOL_TABLE;
extern map<string, int> ITEM_MAP;

int read_training_set(char* training);

#endif
