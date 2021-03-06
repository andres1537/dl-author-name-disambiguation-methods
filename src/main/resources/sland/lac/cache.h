#ifndef _CACHE_
#define _CACHE_

#include <string.h>
#include <stdlib.h>
#include <set>

#include "limits.h"
#include "timer.h"

typedef struct {
	short status, evidence;
	int count, ant_count, cons_count[MAX_CLASSES];
} content_t;

struct cache_cmp {
	bool operator()(const set<int>& i, const set<int>& j) const {
		return(i.size()>=j.size());
	}
};

typedef struct {
	unsigned int max_size;
	float factor;
	long int hits, misses;
	//map<set<int>, content_t, cache_cmp> content;
	map<set<int>, content_t> content;
} cache_t;

extern cache_t CACHE_CRITERIA, CACHE_CLASSES;

int insert_into_cache(cache_t & cache, int* layout, int size, content_t cont);
content_t get_from_cache(cache_t & cache, int* layout, int size);

#endif
