#ifndef _CLASSIFICATION_
#define _CLASSIFICATION_

#define LAZY_SUPERVISED_CLASSIFICATION 0
#define LAZY_SEMISUPERVISED_CLASSIFICATION 1
#define LAZY_TRANSDUCTIVE_CLASSIFICATION 2

#include "rule.h"
#include "limits.h"
#include "timer.h"
#include "cache.h"
#include "evaluation.h"
#include "test.h"
#include "prediction.h"
#include "score.h"
#include "training.h"
#include "unlabeled.h"

extern int CAPACITY;
extern int N_CRITERIA, MIN_RULES, RELATIVE;

int lazy_supervised_classification();
int lazy_transductive_classification();
int lazy_semisupervised_classification();

#endif
