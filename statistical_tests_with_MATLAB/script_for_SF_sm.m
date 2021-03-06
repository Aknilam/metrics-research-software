clear all;

load1 = load('SF_sm_cumulative.mat');
load2 = load('SF_sm_standard.mat');

% Squared errors of predictionsof model based on cumulative metrics, SF small data set
SF_sm_cumulative = load1.SF_sm_cumulative;
% Squared errors of predictionsof model based on non-cumulative metrics, SF small data set
SF_sm_standard = load2.SF_sm_standard;

% Check normality with 2-sided Shapiro-Wilk test to find out if we can use ttest2
% Null Hypothesis: X is normal with unspecified mean and variance.
% Alternative: X is not normal.
alpha = 0.05; % significance level
[H1, pValue1, W1] = swtest(SF_sm_cumulative, alpha);
[H2, pValue2, W2] = swtest(SF_sm_standard, alpha);
% H = 0 => Do not reject the null hypothesis at significance level ALPHA.
% H = 1 => Reject the null hypothesis at significance level ALPHA.

% The tests shows H = 1 -> null hipothesis can be rejected,
% samples are not from normal distribution - we can't use Two Matched Samples T-test

% Check histogram to find out if we can use Wilcoxon's test
% hist(SF_sm_cumulative - SF_sm_standard);

% If data is not from normal distribution, we use LEFT-TAILED Wilcoxon rank-sum test.
% Null hypothesis: Data in SF_sm_cumulative and SF_sm_standard are samples from continuous 
% 				   distributions with equal medians
% Alternative: The median of SF_sm_cumulative is less than the median of SF_sm_standard. 
% Default significance level set: 0.05.
[P,H] = ranksum(SF_sm_cumulative, SF_sm_standard, 'tail', 'left');
% H = 0 => Do not reject the null hypothesis at significance level ALPHA.
% H = 1 => Reject the null hypothesis at significance level ALPHA.

% The test shows H = 0 -> so we cannot reject null hypothesis and we cannot
% state that cumulative metrics are better for prediction.