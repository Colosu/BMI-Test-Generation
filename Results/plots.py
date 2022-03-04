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
file = "H"
csv1 = './' + file + '/' + file + 'Results_0.txt'
csv2 = './' + file + '/' + file + 'Results_1.txt'
csv3 = './' + file + '/' + file + 'Results_2.txt'
csv4 = './' + file + '/' + file + 'Results_3.txt'
csv5 = './' + file + '/' + file + 'Results_4.txt'

file1 = open(csv1, "r")
file2 = open(csv2, "r")
file3 = open(csv3, "r")
file4 = open(csv4, "r")
file5 = open(csv5, "r")


list1 = []
list2 = []
list3 = []
list4 = []
list5 = []

for line in file1:
	if line != "\hline\n" and not "|" in line and not "Total" in line and not "Mean" in line and not "Time" in line:
		list1.append([float(x if not '\\' in x else x[:-3]) for x in line.split(' & ')])

for line in file2:
	if line != "\hline\n" and not "|" in line and not "Total" in line and not "Mean" in line and not "Time" in line:
		list2.append([float(x if not '\\' in x else x[:-3]) for x in line.split(' & ')])
        
for line in file3:
	if line != "\hline\n" and not "|" in line and not "Total" in line and not "Mean" in line and not "Time" in line:
		list3.append([float(x if not '\\' in x else x[:-3]) for x in line.split(' & ')])
        
for line in file4:
	if line != "\hline\n" and not "|" in line and not "Total" in line and not "Mean" in line and not "Time" in line:
		list4.append([float(x if not '\\' in x else x[:-3]) for x in line.split(' & ')])
        
for line in file5:
	if line != "\hline\n" and not "|" in line and not "Total" in line and not "Mean" in line and not "Time" in line:
		list5.append([float(x if not '\\' in x else x[:-3]) for x in line.split(' & ')])
        
#header = ['Order', 'WinBMI', 'WinVS', 'MutBMI', 'MutVS', 'TimeBMI', 'TimeVS']
header = ['Order', 'WinBMI', 'WinVS', 'MutBMI', 'MutVS', 'TimeBMI', 'TimeVS', 'SizeBMI', 'SizeVS', 'MinSize', 'MaxSize']

data1 = pd.DataFrame(list1, columns = header)
data2 = pd.DataFrame(list2, columns = header)
data3 = pd.DataFrame(list3, columns = header)
data4 = pd.DataFrame(list4, columns = header)
data5 = pd.DataFrame(list5, columns = header)

data = [data1[header[1]], data2[header[1]], data3[header[1]], data4[header[1]], data5[header[1]]]
head = ["", "", "", "", ""]
labe = ["100", "200", "300", "400", "500"]
ytic = [0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0]

valid = pd.concat(data, axis=1, keys=head)

valid_plot = valid.boxplot(figsize=[6.5,3], widths=0.9)

plt.axis([0.5, 5.5, -0.1, 1.1])
plt.xticks(ticks=[1,2,3,4,5], labels=labe, rotation=0, ha='center')
plt.yticks(ticks=ytic)
plt.xlabel("Length of the Test Suite")
plt.ylabel("Ratio of times BMI killed more mutants")
plt.savefig('WinBMI' + file + '.png', format='png', bbox_inches = 'tight', dpi=400)
plt.clf()


data = [data1[header[3]], data1[header[4]], data2[header[3]], data2[header[4]], data3[header[3]], data3[header[4]], data4[header[3]], data4[header[4]], data5[header[3]], data5[header[4]]]
head = ["", "", "", "", "", "", "", "", "", ""]
labe = ["100\nBMI", "100\n" + file, "200\nBMI", "200\n" + file, "300\nBMI", "300\n" + file, "400\nBMI", "400\n" + file, "500\nBMI", "500\n" + file]

new = pd.concat(data, axis=1, keys=head)

new_plot = new.boxplot(figsize=[6.5,3], widths=0.9)

plt.axis([0.5, 10.5, -0.1, 1.1])
plt.xticks(ticks=[1,2,3,4,5,6,7,8,9,10], labels=labe, rotation=0, ha='center')
plt.yticks(ticks=ytic)
plt.xlabel("Length of the Test Suite")
plt.ylabel("Ratio of killed mutants")
plt.savefig('mut' + file + '.png', format='png', bbox_inches = 'tight', dpi=400)
plt.clf()


data = [data1[header[5]], data1[header[6]], data2[header[5]], data2[header[6]], data3[header[5]], data3[header[6]], data4[header[5]], data4[header[6]], data5[header[5]], data5[header[6]]]

repeated = pd.concat(data, axis=1, keys=head)

repeated_plot = repeated.boxplot(figsize=[6.5,3], widths=0.9)

#plt.axis([0.5, 10.5, -0.1, 1.1])
plt.xticks(ticks=[1,2,3,4,5,6,7,8,9,10], labels=labe, rotation=0, ha='center')
#plt.yticks(ticks=ytic)
plt.xlabel("Length of the Test Suite")
plt.ylabel("Average Computation Time")
plt.savefig('time' + file + '.png', format='png', bbox_inches = 'tight', dpi=400)
plt.clf()


# data = [data1[header[4]], data2[header[4]], data3[header[4]], data4[header[4]], data5[header[4]]]

# repeatednew = pd.concat(data, axis=1, keys=head)

# repeatednew_plot = repeatednew.boxplot(figsize=[6,10], widths=0.9)

# plt.axis([0.5, 4.5, -0.1, 1.1])
# plt.xticks(ticks=[1,2,3,4,5], labels=labe, rotation=0, ha='center')
# plt.yticks(ticks=ytic)
# plt.xlabel("New (Previously Unseen) and Repeated Traces")
# plt.ylabel("Ratio")
# plt.savefig('repeatednew.png', format='png', bbox_inches = 'tight', dpi=400)