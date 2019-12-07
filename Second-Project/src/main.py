import csv
from src.Classification.Dataset import Dataset
from sklearn.feature_extraction.text import CountVectorizer
from array import *

from src.Classification.Dataset import Dataset

ds = Dataset('round.csv')


from sklearn.metrics import classification_report, confusion_matrix, accuracy_score, roc_curve, auc
from src.Classification.Model import Model
from src.Classification.SVC import SVC
from src.Classification.SGD import SGD
from src.Classification.KNeighbors import KNeighbors
from src.Classification.GaussianNaiveBayes import GaussianNaiveBayes
from src.Classification.DecisionTrees import DecisionTree
from src.Classification.RandomForest import RandomForest

model = RandomForest(True)
model.train()
model.predict()

print(model.get_best_param())

# clf = GridSearchCV(svm.SVC(), parameters)
# clf.fit(ds.get_x_train(), ds.get_y_train())

# print("Best parameters set found on development set:")
# print()
# print(clf.best_params_)
# predicted = clf.predict(ds.get_x_test())
#
print(classification_report(model.predicted, ds.get_y_test()))
print('Accuracy score: ' + str(accuracy_score(model.predicted, ds.get_y_test())))

from sklearn.metrics import explained_variance_score, mean_squared_error

# print(mean_squared_error(ds.get_y_test(), predicted))

#
# ds = Dataset('round.csv', 0.25)
#
# clf = svm.SVC(kernel='linear', C=1).fit(ds.get_x_train(), ds.get_y_train())
#
# predicted = clf.predict(ds.get_x_test())
# print(predicted)

# print(clf.score(X_test, y_test))

# from sklearn.model_selection import cross_val_score
#
# clf = svm.SVC(kernel='linear', C=1)
# scores = cross_val_score(clf, X, Y, cv=5)
# print(scores)
# print(X_train)
# print(X_test)


