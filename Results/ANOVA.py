#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Jan 23 13:37:44 2020

@author: colosu
"""

import scipy.stats as st


for I in range(5):
    print("Ops part:")
    print(I)
    file = "Random"
    table = './' + file + '/' + file + 'Results_' + str(I) + '.txt'
    
    file = open(table, "r")    
    
    ops = []
    cap_ops = []
    
    for line in file:
    	if line != "\hline\n" and not "|" in line and not "Mean" in line:
    		contents = line.split(' & ')
    		ops.append(float(contents[3]))
    		cap_ops.append(float(contents[4]))
    
    # Check Homogeneity of Variance
    stat, pvalue = st.levene(ops, cap_ops)
    print("Levene-value: " + str(stat))
    print("p-value: " + str(pvalue))
    print()
    
    if pvalue > 0.1: #ANOVA test
    	stat, pvalue = st.f_oneway(ops, cap_ops)
    	print("ANOVA test")
    	print("F-value: " + str(stat))
    	print("p-value: " + str(pvalue))
    	print()
    else: #Kruskal-Wallis H-test
    	stat, pvalue = st.kruskal(ops, cap_ops, nan_policy='raise')
    	print("Kruskal-Wallis H-test")
    	print("H-value: " + str(stat))
    	print("p-value: " + str(pvalue))
    	print()
    
    # t-test
    stat, pvalue = st.ttest_ind(ops, cap_ops)
    print("T-value: " + str(stat))
    print("p-value: " + str(pvalue))
    print()