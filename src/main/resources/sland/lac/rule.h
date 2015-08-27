#ifndef _RULE_
#define _RULE_

#include <math.h>
#include <map>
#include <string>

#include "itemset.h"
#include "cache.h"
#include "training.h"
#include "limits.h"
#include "criteria.h"
#include "timer.h"

using namespace std;

#define PRED_CLASS 0
#define PRED_CRITERION 1

typedef struct {
	int ant[MAX_RULE_SIZE];
	int label;
	int size;
	int evidence;
	int id;
	int count;
	int* list;
	float criterion[MAX_CRITERIA];
} rule_t;

extern rule_t RULES[MAX_RULES];
extern int CRITERION, N_RULES, TARGET_ID[MAX_CLASSES], COUNT_TARGET[MAX_CLASSES];
extern map<int, string> SYMBOL_TABLE;

void print_rule(rule_t rule, short rule_type);
void reset_rule(int id);
void reset_rules();
void induce_rules(int* items, int n_items, int* count_target, int level, int projection_size, int min_count, short rule_type);

#endif
