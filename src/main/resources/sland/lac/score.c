/**
THIS MODULE IMPLEMENTS THE SCORING STRATEGY. EACH CAR CONTRIBUTES AS A VOTE FOR A CLASS.
THE VOTES ARE COMPUTED AND IN THE END, CLASSES ARE SORTED ACCORDING WITH THE RESPECTIVE SCORE.
*/

#include "score.h"

/**
RETRIEVES ALL RULES MATCHING A CERTAIN EVIDENCE. THEN, USES A CERTAIN CRITERION TO WEIGTH THE VOTES,
AND RETURNS THE SCORE ASSOCIATED WITH EACH CLASS. THE IDEA IS THAT CLASSES WITH HIGHER SCORES ARE MORE
LIKELY TO BE THE CORRECT ONE.
*/
float* get_THE_score(int evidence, int criterion) {
	__START_TIMER__
	float* points=(float*)malloc(sizeof(float)*MAX_CLASSES);
	int total_rules[MAX_CLASSES];
	for(int i=0;i<MAX_CLASSES;i++) {
		points[i]=0;
		total_rules[i]=1;
	}
	for(int i=0;i<N_RULES && i<RANKING_SIZE;i++) {
		if(RULES[i].evidence==evidence) {
			points[RULES[i].label]+=RULES[i].criterion[criterion];
			total_rules[RULES[i].label]++;
		}
	}
	for(int i=0;i<MAX_CLASSES;i++) points[i]=points[i]/(double)total_rules[i];
	__FINISH_TIMER__
	return points;
}

/**
RETURNS THE SCORE ASSOCIATED WITH EACH CLASS, ACCORDING TO A CERTAIN CRITERION.
*/
score_t get_TOTAL_score(int criterion) {
	__START_TIMER__
	float* r[COMB_EVIDENCES];
	score_t score;
	score.ordered_labels=(int*)malloc(sizeof(int)*MAX_CLASSES);
	score.points=(float*)malloc(sizeof(float)*MAX_CLASSES);
	float t_points=0;
	for(int i=0;i<MAX_CLASSES;i++) {
		score.ordered_labels[i]=i;
		score.points[i]=0;
	}
	for(int i=1;i<COMB_EVIDENCES;i++) {
		r[i]=get_THE_score(i, criterion);
		for(int j=0;j<MAX_CLASSES;j++) {
			score.points[j]+=r[i][j];
			t_points+=r[i][j];
		}
		free(r[i]);
	}
	if(t_points>0) {
		for(int i=0;i<MAX_CLASSES;i++) score.points[i]=score.points[i]/(float)t_points;
	}

	float points[MAX_CLASSES];
	for(int i=0;i<MAX_CLASSES;i++) points[i]=score.points[i];
	for(int i=0;i<MAX_CLASSES;i++) {
		int largest=i;
		float p=-1;
		for(int j=0;j<MAX_CLASSES;j++) {
			if(points[j]>p) {
				largest=j;
				p=points[j];
			}
		}
		score.ordered_labels[i]=largest;
		points[largest]=-1;
	}
	__FINISH_TIMER__
	return(score);
}
