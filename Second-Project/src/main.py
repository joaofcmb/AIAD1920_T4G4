# import numpy as np
# from src.Classification.DecisionTrees import DecisionTree
# from src.Classification.SVC import SVC
# from src.Classification.RandomForest import RandomForest
# from src.Classification.KNeighbors import KNeighbors
# from sklearn.inspection import permutation_importance
# import matplotlib.pyplot as plt
#
# model = RandomForest()
# model.train()
# model.predict()
#
# result = permutation_importance(model.clf, model.dataset.get_x_train(), model.dataset.get_y_train(), n_repeats=10,
#                                 random_state=42)
# perm_sorted_idx = result.importances_mean.argsort()
#
# tree_importance_sorted_idx = np.argsort(model.clf.feature_importances_)
# tree_indices = np.arange(0, len(model.clf.feature_importances_)) + 0.5
#
# fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(12, 8))
# ax1.barh(tree_indices,
#          model.clf.feature_importances_[tree_importance_sorted_idx], height=0.7)
# ax1.set_yticklabels(['a', 'b', 'c', 'd'])
# ax1.set_yticks(tree_indices)
# ax1.set_ylim((0, len(model.clf.feature_importances_)))
# fig.tight_layout()
# plt.show()

from collections import defaultdict

import matplotlib.pyplot as plt
import numpy as np
from scipy.stats import spearmanr
from scipy.cluster import hierarchy

from sklearn.datasets import load_breast_cancer
from sklearn.ensemble import RandomForestClassifier
from sklearn.inspection import permutation_importance
from sklearn.model_selection import train_test_split

data = load_breast_cancer()
X, y = data.data, data.target
X_train, X_test, y_train, y_test = train_test_split(X, y, random_state=42)
#
# clf = RandomForestClassifier(n_estimators=100, random_state=42)
# clf.fit(X_train, y_train)
# print("Accuracy on test data: {:.2f}".format(clf.score(X_test, y_test)))
#
# result = permutation_importance(clf, X_train, y_train, n_repeats=10,
#                                 random_state=42)
# perm_sorted_idx = result.importances_mean.argsort()
#
# tree_importance_sorted_idx = np.argsort(clf.feature_importances_)
# tree_indices = np.arange(0, len(clf.feature_importances_)) + 0.5
#
# fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(12, 8))
# ax1.barh(tree_indices,
#          clf.feature_importances_[tree_importance_sorted_idx], height=0.7)
# ax1.set_yticklabels(data.feature_names)
# ax1.set_yticks(tree_indices)
# ax1.set_ylim((0, len(clf.feature_importances_)))
# ax2.boxplot(result.importances[perm_sorted_idx].T, vert=False,
#             labels=data.feature_names)
# fig.tight_layout()
# plt.show()

print(data.feature_names)
print(data)
