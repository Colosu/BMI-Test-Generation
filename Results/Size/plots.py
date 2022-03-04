#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Feb  6 14:49:30 2020

@author: colosu
"""

import numpy as np # linear algebra
import pandas as pd # data processing, CSV file I/O (e.g. pd.read_csv)
import matplotlib.pyplot as plt

#Variables
file = "Size"
for i in range(1):
    csv1 = './' + file + 'Results_' + str(i) + '.txt'
    
    file1 = open(csv1, "r")
    
    
    list1 = []
    
    for line in file1:
    	if line != "\hline\n" and not "|" in line and not "Total" in line and not "Mean" in line and not "Time" in line:
    		list1.append([float(x if not '\\' in x else x[:-3]) for x in line.split(' & ')])
            
    header = ['Size', 'MutBMI', 'MutRandom', 'MutH', 'MutTT', 'MutITSDm', 'MutOTSDm', 'MutIOTSDm', 'MutCoverage', 'TimeBMI', 'TimeRandom', 'TimeH', 'TimeTT', 'TimeITSDm', 'TimeOTSDm', 'TimeIOTSDm', 'TimeCoverage']
    
    data1 = pd.DataFrame(list1, columns = header)
    
    data = [data1[header[0]], data1[header[1]], data1[header[2]], data1[header[3]], data1[header[4]], data1[header[5]], data1[header[6]], data1[header[7]], data1[header[8]]]
    ytic = [0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0]
    legend = ['BMI', 'Random', 'H-Method', 'Transition Tour', 'ITSDm', 'OTSDm', 'IOTSDm', 'Transition Coverage']
    
    valid = pd.concat(data, axis=1)
    valid = valid.sort_values([header[0], header[1], header[2], header[3], header[4], header[5], header[6], header[7], header[8]])
    valid = valid.groupby(header[0]).mean()
    
    valid_plot = valid.plot(figsize=[15,10], marker='o')
    
    plt.axis([-0.5, 160.5, -0.1, 1.1])
    plt.yticks(ticks=ytic)
    plt.legend(legend)
    plt.xlabel("States of the FSM")
    plt.ylabel("Ratio of killed mutants")
    plt.savefig('Mut' + file + '_' + str(i) + '.png', format='png', bbox_inches = 'tight', dpi=400)
    plt.clf()
    
    
    data = [data1[header[0]], data1[header[9]], data1[header[10]], data1[header[11]], data1[header[12]], data1[header[13]], data1[header[14]], data1[header[15]], data1[header[16]]]
    
    valid = pd.concat(data, axis=1)
    valid = valid.sort_values([header[0], header[9], header[10], header[11], header[12], header[13], header[14], header[15], header[16]])
    valid = valid.groupby(header[0]).mean()
    
    valid_plot = valid.plot(figsize=[15,10], marker='o')
    
    maxi = max(valid.max())
    
    plt.axis([-0.5, 160.5, -0.1, int(maxi) + 1.1])
    plt.legend(legend)
    plt.xlabel("States of the FSM")
    plt.ylabel("Average computation time")
    plt.savefig('Time' + file + '_' + str(i) + '.png', format='png', bbox_inches = 'tight', dpi=400)
    plt.clf()