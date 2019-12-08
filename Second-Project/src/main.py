import csv
from src.Classification.Dataset import Dataset as ClassDataset
from src.Regression.Dataset import Dataset as RegrDataset
from sklearn.feature_extraction.text import CountVectorizer
from array import *

from src.Classification.Dataset import Dataset

import matplotlib.pyplot as plt
from sklearn import metrics
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score, roc_curve, auc
# from src.Classification.SVC import SVC
# from src.Classification.SGD import SGD
# from src.Classification.KNeighbors import KNeighbors
# from src.Classification.GaussianNaiveBayes import GaussianNaiveBayes
# from src.Classification.DecisionTrees import DecisionTree
# from src.Classification.RandomForest import RandomForest
# from src.Classification.MultiLayerPerceptron import MultiLayerPerceptron

from src.Regression.SVR import SVR
from src.Regression.KNeighbors import KNeighbors
from src.Regression.DecisionTrees import DecisionTree
from src.Regression.RandomForest import RandomForest
from src.Regression.MultiLayerPerceptron import MultiLayerPerceptron

from sklearn.metrics import explained_variance_score

model = KNeighbors()
model.train()
model.predict()

print(model.get_best_param())
# model.metrics.classification_report()
# model.metrics.accuracy_score()
# model.metrics.confusion_matrix()
# model.metrics.learning_curve()
# model.metrics.roc_curve()
# model.metrics.precision_recall_curve()

# print(explained_variance_score(model.dataset.get_y_test(), model.get_predicted()))

print("Terminated with success")

