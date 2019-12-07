import csv
from src.Classification.Dataset import Dataset




from sklearn.model_selection import train_test_split
from sklearn import svm
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score, roc_curve, auc

ds = Dataset('round.csv', 0.25)

clf = svm.SVC(kernel='linear', C=1).fit(ds.get_x_train(), ds.get_y_train())

predicted = clf.predict(ds.get_x_test())
# print(predicted)
print(classification_report(predicted, ds.get_y_test()))
print('Accuracy score: ' + str(accuracy_score(predicted, ds.get_y_test())))

# print(clf.score(X_test, y_test))

# from sklearn.model_selection import cross_val_score
#
# clf = svm.SVC(kernel='linear', C=1)
# scores = cross_val_score(clf, X, Y, cv=5)
# print(scores)
# print(X_train)
# print(X_test)


