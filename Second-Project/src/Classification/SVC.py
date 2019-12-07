"""
 Import Module
"""

import os
from joblib import load, dump
from sklearn import svm
from src.Classification.Model import Model
from sklearn.model_selection import GridSearchCV


class SVC(Model):
    """
    SVC model class
    """
    algorithm = 'SVC'

    # Tuning parameters
    parameters = {'C': [1.0],
                  'kernel': ['rbf'],
                  'degree': [3],
                  'gamma': ['scale'],
                  'coef0': [0.0],
                  'shrinking': [True],
                  'probability': [False],
                  'tol': [0.001],
                  'cache_size': [200],
                  'class_weight': [None],
                  'verbose': [False],
                  'max_iter': [-1],
                  'decision_function_shape': ['ovr'],
                  'break_ties': [False],
                  'random_state': [None]}

    def __init__(self, grid_search=False, filename='round.csv'):
        """
        SVC class constructor

        @param grid_search: indicates whether classifier should be created
                            with the grid search classifier
        """
        super().__init__(self.get_classifier(grid_search), self.algorithm, filename)

    def get_classifier(self, grid_search):
        """
        Function responsible for getting the model
        classifier. If already created it loads it from
        the respective file otherwise creates it.
        """
        self.grid_search = grid_search

        if grid_search:
            if os.path.isfile('../joblib/GridSearchCV_' + self.algorithm + '.joblib'):
                clf = load('../joblib/GridSearchCV_' + self.algorithm + '.joblib')
            else:
                clf = GridSearchCV(svm.SVC(), self.parameters)
                dump(clf, '../joblib/GridSearchCV_' + self.algorithm + '.joblib')
        else:
            if os.path.isfile('../joblib/' + self.algorithm + '.joblib'):
                clf = load('../joblib/' + self.algorithm + '.joblib')
            else:
                clf = svm.SVC()

        return clf

    def get_algorithm(self):
        """
        Function responsible for retrieving the
        algorithm name
        @return: algorithm name
        """
        return self.algorithm

    def get_algorithm_gs_param(self):
        """
        Function responsible for retrieving the grid
        search parameters
        @return: grid search parameters
        """
        return self.parameters

    def get_y_score(self):
        """
        Function responsible for retrieving the y_score
        which consists on the array having all
        estimations
        @return: y_score
        """
        return self.clf.decision_function(self.dataset.get_y_test())

    def get_best_param(self):
        """
        Function responsible for showing the best
        parameters for this specific algorithm
        @return:
        """
        if self.grid_search:
            value = "Best parameters for " + self.algorithm + " algorithm:\n"
            for param_name in self.parameters:
                value = value + param_name + ": " + str(self.clf.best_params_[param_name]) + '\n'

            return value
