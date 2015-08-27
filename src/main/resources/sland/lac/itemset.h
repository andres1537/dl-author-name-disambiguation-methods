#ifndef _ITEMSET_
#define _ITEMSET_

#include <map>
#include <string>

#include "evidence.h"
#include "timer.h"

typedef struct {
	short size;
	int count;
	int *list;
	int *layout;
	int evidence;
}itemset_t;

extern itemset_t *ITEMSETS;
extern int N_ITEMSETS;
extern map<int, string> SYMBOL_TABLE;

int is_A_subset_of_B(int* A, int sizeA, int* B, int sizeB);
void print_itemset(itemset_t it);
int release_itemset(itemset_t a);
itemset_t get_THE_union(itemset_t a, itemset_t b);
itemset_t get_THE_intersection(itemset_t a, itemset_t b);
itemset_t create_itemset(int* its, int size);


#endif
