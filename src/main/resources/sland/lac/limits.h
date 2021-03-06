#ifndef _LIMITS_
#define _LIMITS_

#define KB 1024
#define MB 1024*1024
#define GB 1024*1024*1024

#define MAX_CLASSES 500 
#define MAX_CRITERIA MAX_CLASSES
#define MAX_RULE_SIZE 50
#define MAX_RULES 500*KB
#define MAX_EVIDENCES 5
#define COMB_EVIDENCES 32
#define MAX_TIME_PER_TEST 1.00

extern int MIN_COUNT, RANKING_SIZE, MIN_SIZE, MAX_SIZE;
extern float MIN_CONF, MIN_SUPP, FACTOR;

#endif
