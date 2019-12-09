"""
 Import Module
"""

import os
from joblib import load, dump
from sklearn import svm
from src.Model import Model
from sklearn.model_selection import GridSearchCV


class SVC(Model):
    """
    SVC model class
    """
    algorithm = 'SVC'

    # Default Tuning parameters
    default_parameters = {'C': [1.0],
                          'kernel': ['rbf', 'linear', 'poly', 'sigmoid', 'precomputed'],
                          'degree': [3],
                          'gamma': ['scale', 'auto'],
                          'coef0': [0.0],
                          'shrinking': [True, False],
                          'probability': [True, False],
                          'tol': [0.001],
                          'cache_size': [200],
                          'class_weight': [None, 'dict', 'balanced'],
                          'verbose': [True, False],
                          'max_iter': [-1],
                          'decision_function_shape': ['ovo', 'ovr'],
                          'break_ties': [True, False],
                          'random_state': [None]
                          }

    # Tuning parameters
    tuning_parameters = {}

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
            if os.path.isfile('joblib/classification/GridSearchCV_' + self.algorithm + '.joblib'):
                clf = load('joblib/classification/GridSearchCV_' + self.algorithm + '.joblib')
            else:
                clf = GridSearchCV(svm.SVC(), self.tuning_parameters)
                dump(clf, 'joblib/classification/GridSearchCV_' + self.algorithm + '.joblib')
        else:
            if os.path.isfile('joblib/classification/' + self.algorithm + '.joblib'):
                clf = load('joblib/classification/' + self.algorithm + '.joblib')
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
        return self.tuning_parameters

    def get_best_param(self):
        """
        Function responsible for showing the best
        parameters for this specific algorithm
        @return:
        """
        if self.grid_search:
            value = "Best parameters for " + self.algorithm + " algorithm:\n"
            for param_name in self.tuning_parameters:
                value = value + param_name + ": " + str(self.clf.best_params_[param_name]) + '\n'

            return value
