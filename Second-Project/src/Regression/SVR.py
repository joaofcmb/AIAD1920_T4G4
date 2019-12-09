"""
 Import Module
"""

import os
from joblib import load, dump
from sklearn import svm
from src.Model import Model
from sklearn.model_selection import GridSearchCV


class SVR(Model):
    """
    SVR model class
    """
    algorithm = 'SVR'

    # Default Tuning parameters
    default_parameters = {'kernel': ['rbf', 'linear', 'poly', 'sigmoid', 'precomputed'],
                          'degree': [3],
                          'gamma': ['scale', 'auto'],
                          'coef0': [0.0],
                          'tol': [0.001],
                          'C': [1.0],
                          'epsilon': [0.1],
                          'shrinking': [True, False],
                          'cache_size': [200],
                          'verbose': [True, False],
                          'max_iter': [-1]
                          }

    # Tuning parameters
    tuning_parameters = {'gamma': ['scale', 'auto']}

    def __init__(self, grid_search=False, filename='personality.csv'):
        """
        SVR class constructor

        @param grid_search: indicates whether classifier should be created
                            with the grid search classifier
        """
        super().__init__(self.get_classifier(grid_search), self.algorithm, filename, False)

    def get_classifier(self, grid_search):
        """
        Function responsible for getting the model
        classifier. If already created it loads it from
        the respective file otherwise creates it.
        """
        self.grid_search = grid_search

        if grid_search:
            if os.path.isfile('joblib/regression/GridSearchCV_' + self.algorithm + '.joblib'):
                clf = load('joblib/regression/GridSearchCV_' + self.algorithm + '.joblib')
            else:
                clf = GridSearchCV(svm.SVR(), self.tuning_parameters)
                dump(clf, 'joblib/regression/GridSearchCV_' + self.algorithm + '.joblib')
        else:
            if os.path.isfile('joblib/regression/' + self.algorithm + '.joblib'):
                clf = load('joblib/regression/' + self.algorithm + '.joblib')
            else:
                clf = svm.SVR()

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
