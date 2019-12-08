import csv
from src.Classification.Dataset import Dataset
from sklearn.feature_extraction.text import CountVectorizer
from array import *

from src.Classification.Dataset import Dataset

ds = Dataset('round.csv')

import matplotlib.pyplot as plt
from sklearn import metrics
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score, roc_curve, auc
from src.Classification.Model import Model
from src.Classification.SVC import SVC
from src.Classification.SGD import SGD
from src.Classification.KNeighbors import KNeighbors
from src.Classification.GaussianNaiveBayes import GaussianNaiveBayes
from src.Classification.DecisionTrees import DecisionTree
from src.Classification.RandomForest import RandomForest
from src.Classification.MultiLayerPerceptron import MultiLayerPerceptron

model = SVC()
model.train()
model.predict()

model.metrics.classification_report()
model.metrics.accuracy_score()
model.metrics.confusion_matrix()
model.metrics.learning_curve()
model.metrics.roc_curve()
model.metrics.precision_recall_curve()

print("Terminated with success")

